###一.构建类型
构建类型表明了构建怎么样的APP，Android的gradle插件默认提供了两种类型：debug和release。它们都配置在`buildType`区块中。一个新建项目的`build.gradle`文件配置的`buildType`应该如下所示：

```
android {
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'),
                'proguard-rules.pro'
        }
    }
}
```
虽然只有`release`版本的配置，但是要新增一个`debug`类型也是很简单的。在上述代码块中，`minifyEnabled `意味着是否自动移除无用的资源文件，如果设置为`true`，`Gradle`同时也会移除依赖库中未使用到的资源，但是它只有在`shrinkResources`属性同时也为`true`的时候才会生效。

```groovy
android {
    buildTypes {
        release {
            minifyEnabled true     //Code精简
            shrinkResources true   //Resource精简
            proguardFiles getDefaultProguardFile('proguard-android.txt'),
                'proguard-rules.pro'
        }
    }
}
```

另外一个可配置属性是`debuggable`,Debug模式下的配置默认为true，其他情况的构建默认都是false。
为了能够安装多个不同构建类型的APP在一台机器上，Android必须能够区别应用程序的ID，`applicationIDsuffix`允许Gradle生成多个不同ID的APK。如下图:

```
android {
    buildTypes {
        debug {
            applicationIDsuffix '.debug'
            versionNameSuffix '-debug'
        }
    }
}
```
现在release和debug版本的APP都能安装在一台机器上了。
###二.Flavors和Variants
Build Type是研发过程的一部分，基本作为一个从研发到生产的一个发展过程。<br>
Flavors允许你为同一个APP构建不同的版本，比如你想为不同的客户构建不同的产品，或者你想同时构建免费和付费的产品。<br>
要声明一个产品的Flavor，需要在`android`模块下使用`productFlavors`配置。

```
productFlavors {
        green {
            applicationId "hello.com.helloworld.green"
        }
        red {
            applicationId "hello.com.helloworld.red"
        }
        yellow {
            applicationId "hello.com.helloworld.yellow"
        }
    }
```
这样就可以构建出不同版本的APK，而且他们的ID也有轻微的区别。
####风味维度
---
> 需要注意的是，Flavor的名字不能是Gradle已经预定义使用的。
上述配置貌似没什么问题，但是Gradle在3.0以后新增了自动匹配变体的机制，要求给Flavor指定维度`dimension`,即使只需要一个维度也应该指定。否则将会遇到编译错误：

```
Error:All flavors must now belong to a named flavor dimension.
The flavor 'flavor_name' is not assigned to a flavor dimension.
```
所以现在添加Flavor的方式应该是：

```
android {
	......
    defaultConfig {
        applicationId "hello.com.helloworld"
        minSdkVersion 15
        targetSdkVersion 27
        versionCode 1
        versionName "1.0"
        flavorDimensions "hello"
    }
    productFlavors {
        green {
            dimension "hello"
            applicationId "hello.com.helloworld.green"
        }
        red {
            dimension "hello"
            applicationId "hello.com.helloworld.red"
        }
        yellow {
            dimension "hello"
            applicationId "hello.com.helloworld.yellow"
        }
    }
}
```
想要查看所有可用的变体的名称，我们可以自定义一个Task：

```
task printVariantNames() {
    doLast {
        android.applicationVariants.all { variant ->
            println "variant.name : "variant.name
        }
    }
}
```
输出结果如下：

```
variant.name : redDebug
variant.name : redRelease
variant.name : greenDebug
variant.name : greenRelease
variant.name : yellowDebug
variant.name : yellowRelease
```

示例：如果我想针对"color"和“api”不同的版本输出不同的构建APK。

```
android {
  ...
  buildTypes {
    debug {...}
    release {...}
  }

  // 指定需要的维度. 顺序决定优先级且依次是从高到低
  flavorDimensions "hello","api"
    productFlavors {
        green {
            dimension "hello"
            applicationId "hello.com.helloworld.green"
        }
        red {
            dimension "hello"
            applicationId "hello.com.helloworld.red"
        }
        yellow {
            dimension "hello"
            applicationId "hello.com.helloworld.yellow"
        }
        minApi23{
            dimension "api"
            minSdkVersion 23
        }
        minApi18{
            dimension "api"
            minSdkVersion 18
        }
    }
}
```
Gradle 创建的构建变体数量等于每个风味维度中的风味数量与您配置的构建类型数量的乘积。在 Gradle 为每个构建变体或对应 APK 命名时，属于较高优先级风味维度的产品风味首先显示，之后是较低优先级维度的产品风味，再之后是构建类型。以上面的构建配置为例，Gradle 可以使用以下命名方案创建总共 12 个构建变体：

构建变体：[minApi23, minApi18][green, green，green][Debug, Release]
对应 APK：app-[minApi23, minApi18]-[green, green。green]-[debug, release].apk
Gradle对应的输出就是：

```
variant.name : redMinApi23Debug
variant.name : redMinApi23Release
variant.name : redMinApi18Debug
variant.name : redMinApi18Release
variant.name : greenMinApi23Debug
variant.name : greenMinApi23Release
variant.name : greenMinApi18Debug
variant.name : greenMinApi18Release
variant.name : yellowMinApi23Debug
variant.name : yellowMinApi23Release
variant.name : yellowMinApi18Debug
variant.name : yellowMinApi18Release
```

#### 过滤变体

---

```
variantFilter { variant ->
    def names = variant.flavors*.name
    // To check for a build type instead, use variant.buildType.name == "buildType"
    if (names.contains("minApi21") && names.contains("demo")) {
      // Gradle ignores any variants that satisfy the conditions above.
      setIgnore(true)
    }
  }
```






