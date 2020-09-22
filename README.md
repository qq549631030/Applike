#  Android启动初始化插件

[ ![Download](https://api.bintray.com/packages/qq549631030/maven/applike-plugin/images/download.svg) ](https://bintray.com/qq549631030/maven/applike-plugin/_latestVersion)

此插件用于android组件化开发时，各模块启动初始化

### 下载

根目录的build.gradle中：
```groovy
buildscript {
    dependencies {
        classpath "cn.hx.applike:plugin:1.0.0"
    }
}
```
所有模块目录的build.gradle模块中：
```groovy
apply plugin: 'com.android.application'
//apply plugin: 'com.android.library'

dependencies {
	implementation 'cn.hx.applike:api:1.0.0'
	annotationProcessor 'cn.hx.applike:compiler:1.0.0'
}
```
app模块中：
```groovy
apply plugin: 'com.android.application'
apply plugin: 'cn.hx.applike'
```

### 使用

在每个模块定义一个XXXAppLike实现IAppLike接口，并加上@AppLike注解

```kotlin
@AppLike
class AppAppLike : IAppLike {
    override fun getPriority(): Int {
        return 1
    }

    override fun onCreate(context: Context) {
        //做启动初始化工作
    }

    override fun onTerminate(context: Context) {
    }
}
```

在Application的onCreate和onTerminate方法中调用AppLikeManager的对应方法

```kotlin
class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppLikeManager.onCreate(this)
    }

    override fun onTerminate() {
        AppLikeManager.onTerminate(this)
        super.onTerminate()
    }
}
```

这样当App启动时所有的XXXAppLike的onCreate方法都会调用，可以通过getPriority()决定调用的优先级，值越大越先调用