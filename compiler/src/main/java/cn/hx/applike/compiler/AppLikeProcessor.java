package cn.hx.applike.compiler;

import com.google.auto.service.AutoService;

import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import cn.hx.applike.annotation.AppLike;

@AutoService(Processor.class)
public class AppLikeProcessor extends AbstractProcessor {

    private Elements mElementUtils;
    private Messager messager;
    private Map<String, AppLikeProxyClassCreator> mMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(AppLike.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        log("start process");
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(AppLike.class);
        mMap.clear();
        for (Element element : elements) {
            if (!element.getKind().isClass()) {
                throw new RuntimeException("Annotation AppLike can only be used in class.");
            }
            TypeElement typeElement = (TypeElement) element;

            //这里检查一下，使用了该注解的类，同时必须要实现cn.hx.applike.api.IAppLike接口，否则会报错，因为我们要实现一个代理类
            List<? extends TypeMirror> mirrorList = typeElement.getInterfaces();
            if (mirrorList.isEmpty()) {
                throw new RuntimeException(typeElement.getQualifiedName() + " must implements interface cn.hx.applike.api.IAppLike");
            }
            boolean checkInterfaceFlag = false;
            for (TypeMirror mirror : mirrorList) {
                if ("cn.hx.applike.api.IAppLike".equals(mirror.toString())) {
                    checkInterfaceFlag = true;
                }
            }
            if (!checkInterfaceFlag) {
                throw new RuntimeException(typeElement.getQualifiedName() + " must implements interface cn.hx.applike.api.IAppLike");
            }
            String fullClassName = typeElement.getQualifiedName().toString();
            if (!mMap.containsKey(fullClassName)) {
                log("process class name : " + fullClassName);
                AppLikeProxyClassCreator creator = new AppLikeProxyClassCreator(mElementUtils, typeElement);
                mMap.put(fullClassName, creator);
            }
        }

        log("start to generate proxy class code");
        for (Map.Entry<String, AppLikeProxyClassCreator> entry : mMap.entrySet()) {
            String className = entry.getKey();
            AppLikeProxyClassCreator creator = entry.getValue();
            log("generate proxy class for " + className);

            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(creator.getProxyClassFullName());
                Writer writer = jfo.openWriter();
                writer.write(creator.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private void log(String msg) {
        //log(msg);
        messager.printMessage(Diagnostic.Kind.OTHER, msg);
    }
}
