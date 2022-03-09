package cn.hx.applike.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class AppLikeTransform extends Transform {

    Project project

    AppLikeTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "AppLike"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    static void printCopyRight() {
        println()
        println("\033[0;32;40m******************************************************************************\033[0m")
        println("\033[0;32;40m******                                                                  ******\033[0m")
        println("\033[0;32;40m******                欢迎使用 AppLike 编译插件                            ******\033[0m")
        println("\033[0;32;40m******                                                                  ******\033[0m")
        println("\033[0;32;40m******************************************************************************\033[0m")
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
//        println "\nstart to transform-------------->>>>>>>"
        printCopyRight()
        def appLikeProxyClassList = []
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                        //形如 AppLike$$****$$Proxy.class 的类，是我们要找的目标class
                        if (ScanUtil.isTargetProxyClass(file)) {
                            appLikeProxyClassList.add(file.name)
                        }
                    }
                }

                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.each { JarInput jarInput ->
                def jarName = jarInput.name
                def md5 = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
                    //处理jar包里的代码
                    File src = jarInput.file
                    if (ScanUtil.shouldProcessPreDexJar(src.absolutePath)) {
                        List<String> list = ScanUtil.scanJar(src, dest)
                        if (list != null) {
                            appLikeProxyClassList.addAll(list)
                        }
                    }
                }
                FileUtils.copyFile(jarInput.file, dest)
            }
        }

//        println "找到APPLike代理类:"
//        appLikeProxyClassList.forEach({fileName ->
//            println fileName
//        })
//        println "\n包含AppLifeCycleManager类的jar文件"
//        println ScanUtil.FILE_CONTAINS_INIT_CLASS.getAbsolutePath()
//        println "开始自动注册"

        new AppLikeCodeInjector(appLikeProxyClassList).execute()

//        println "transform finish----------------<<<<<<<\n"
    }


}