package cn.hx.applike.compiler;


import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import cn.hx.applike.annotation.AppLikeConfig;

public class AppLikeProxyClassCreator {

    private Elements mElementUtils;
    private TypeElement mTypeElement;
    private String mProxyClassSimpleName;

    public AppLikeProxyClassCreator(Elements elements, TypeElement typeElement) {
        mElementUtils = elements;
        mTypeElement = typeElement;
        mProxyClassSimpleName = AppLikeConfig.PROXY_CLASS_PREFIX +
                mTypeElement.getSimpleName().toString() +
                AppLikeConfig.PROXY_CLASS_SUFFIX;
    }

    /**
     * 获取要生成的代理类的完整类名
     *
     * @return
     */
    public String getProxyClassFullName() {
        return AppLikeConfig.PROXY_CLASS_PACKAGE_NAME + "." + mProxyClassSimpleName;
    }

    /**
     * 生成java代码
     */
    public String generateJavaCode() {
        StringBuilder sb = new StringBuilder();
        //设置包名
        sb.append("package ").append(AppLikeConfig.PROXY_CLASS_PACKAGE_NAME).append(";\n\n");

        //设置import部分
        sb.append("import android.content.Context;\n");
        sb.append("import cn.hx.applike.api.IAppLike;\n");
        sb.append("import ").append(mTypeElement.getQualifiedName()).append(";\n\n");

        sb.append("public class ").append(mProxyClassSimpleName)
                .append(" implements ").append("IAppLike ").append(" {\n\n");

        //设置变量
        sb.append("  private ").append(mTypeElement.getSimpleName().toString()).append(" mAppLike;\n\n");

        //构造函数
        sb.append("  public ").append(mProxyClassSimpleName).append("() {\n");
        sb.append("  mAppLike = new ").append(mTypeElement.getSimpleName().toString()).append("();\n");
        sb.append("  }\n\n");

        //onCreate()方法
        sb.append("  public void onCreate(Context context) {\n");
        sb.append("    mAppLike.onCreate(context);\n");
        sb.append("  }\n\n");

        //getPriority()方法
        sb.append("  public int getPriority() {\n");
        sb.append("    return mAppLike.getPriority();\n");
        sb.append("  }\n\n");

        //onTerminate方法
        sb.append("  public void onTerminate(Context context) {\n");
        sb.append("    mAppLike.onTerminate(context);\n");
        sb.append("  }\n\n");


        sb.append("\n}");
        return sb.toString();
    }
}
