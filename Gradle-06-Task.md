###一.自定义Task
Gradle支持`task`代码块内自定义Task，它支持一系列内置的Task类型（如：Wrapper、Copy、Delete、Exec等）。<br>
例如Copy类型，它内部包含`from`和`into`属性，`from`内部通过`exclude`设置文件名的过滤规则等。下面简单写一个拷贝APK的Task,但是会过滤掉一部分不需要的文件。
<a id="copyTask"></a>

```
task copyApks(type: Copy) {
    from("$buildDir/outputs/apk"){
        exclude '**/*debug.apk' 	//debug结尾的apk不做处理
    }
    into 'apks'
}
```

如果你不想使用内置的Task类型，也可以完全自己写，但是要明白配置和执行两个阶段的区别，如果想让你的Task执行，使用`doLast`即可.

```
task printVariantNames() {
    doLast {
        android.applicationVariants.all { variant ->
            println variant.name
        }
    }
}
```
上述自定义的Task，`doLast`是一个分界点，在它之前或者之后的代码都是在配置期间执行的，在它内部的代码只有在执行这个Task的时候才会运行。

如果我想安装应用的所有变体在同一台机器上（前提是他们拥有唯一的ID），那么就可以写一个下面的Task

```
task installDebugFlavors() {
    android.applicationVariants.all { variant ->
        if (variant.name.endsWith("Debug")) {
            String name = variant.name.capitalize() //将首字母大写
            dependsOn "install$name" //然后组合一个install的任务执行
        }
    }
}
```

输出日志如下

```
CompileOptions.bootstrapClasspath property instead.
variant : redMinApi23Debug	//增加一行输出名字的命令
variant : redMinApi23Release
variant : redMinApi18Debug
variant : redMinApi18Release
variant : greenMinApi23Debug
variant : greenMinApi23Release
variant : greenMinApi18Debug
variant : greenMinApi18Release
variant : yellowMinApi23Debug
variant : yellowMinApi23Release
variant : yellowMinApi18Debug
variant : yellowMinApi18Release
:app:preBuild UP-TO-DATE
:app:preGreenMinApi18DebugBuild UP-TO-DATE
......
:app:packageGreenMinApi18Debug
:app:assembleGreenMinApi18Debug
:app:installGreenMinApi18Debug
Installing APK 'app-green-minApi18-debug.apk' on 'X86-NiuBao(AVD) - 9' for app:greenMinApi18Debug
Installed on 1 device.
:app:preGreenMinApi23DebugBuild UP-TO-DATE
......
:app:packageGreenMinApi23Debug
:app:assembleGreenMinApi23Debug
:app:installGreenMinApi23Debug
Installing APK 'app-green-minApi23-debug.apk' on 'X86-NiuBao(AVD) - 9' for app:greenMinApi23Debug
Installed on 1 device.
:app:preRedMinApi18DebugBuild UP-TO-DATE
......
:app:packageRedMinApi18Debug
:app:assembleRedMinApi18Debug
:app:installRedMinApi18Debug
Installing APK 'app-red-minApi18-debug.apk' on 'X86-NiuBao(AVD) - 9' for app:redMinApi18Debug
Installed on 1 device.
:app:preRedMinApi23DebugBuild UP-TO-DATE
......
:app:packageRedMinApi23Debug
:app:assembleRedMinApi23Debug
:app:installRedMinApi23Debug
Installing APK 'app-red-minApi23-debug.apk' on 'X86-NiuBao(AVD) - 9' for app:redMinApi23Debug
Installed on 1 device.
:app:preYellowMinApi18DebugBuild UP-TO-DATE
......
:app:packageYellowMinApi18Debug
:app:assembleYellowMinApi18Debug
:app:installYellowMinApi18Debug
Installing APK 'app-yellow-minApi18-debug.apk' on 'X86-NiuBao(AVD) - 9' for app:yellowMinApi18Debug
Installed on 1 device.
:app:preYellowMinApi23DebugBuild UP-TO-DATE
......
:app:packageYellowMinApi23Debug
:app:assembleYellowMinApi23Debug
:app:installYellowMinApi23Debug
Installing APK 'app-yellow-minApi23-debug.apk' on 'X86-NiuBao(AVD) - 9' for app:yellowMinApi23Debug
Installed on 1 device.
:app:installDebugFlavors

BUILD SUCCESSFUL in 14s
```
最后可以看到所有的APK都安装在了同一台手机上
<image width=600 height=300 src="file://users/Users/lihua/Learn/Gradle/pics/gradle_install_flavors.png"/>

> 扩展一下ADB的超时时间,因为编译时间可能比较快，但是部署的时间可能会比较慢，有时可能需要调整一下ADB的超时时间。

```
android {
    adbOptions {
        timeOutInMs = 30 * 1000
    }
}
```
###二.将自定义Task和编译过程相关联
如果你想将自定义的Task作为整个编译流程的一部分，可使用`dependsOn`属性把你的Task插入到一个有向非循环图。
>有向：每一个依赖只有一个方向
>非循环：每一个步骤不可逆
回头看一下我们定义的<a href="#copyTask">copyTask</a>,如果在尚未编译出任何APK的时候它是没什么用的，如果把它和编译APK的任务相关联呢？

```
task copyApks(type: Copy, dependsOn: "assembleDebug") {
    from("$buildDir/outputs/apk") {
        exclude '**/*unsign.apk'
    }
    into 'apks'
}
```
这个时候，它在执行的时候就会依赖`assembleDebug`，这样就能确保每次都能有APK被操作。

###三.过滤Task
有时候我们想过滤某个不需要的Task，这个时候就用到了`-x（全称：--exclude-task）`属性。
比如，lint检查在编译的时候并不是每次都需要的，则使用下命令过滤lint的检查：

```
./gradlew build -x lint
```
但是可能会有的一个问题是，当你使用了变体的时候，就需要每次手动指定过滤了，比较好的方式是使这个过滤规则成为整个编译过程的一部分。

```
gradle.taskGraph.whenReady { graph ->
    graph.allTasks.findAll { it.name ==~ /lint.*/ }*.enabled = false
}
```
`taskGraph`表示在Gradle运行的时候，自身构建的一个有向非循环图。
`whenReady`表示任何“图”的形成时机，因为任何操作都要在“图”形成之后。
再来看下配置之后的结果：

```
➜  HelloWorld ./gradlew build | grep lint
:app:lintVitalGreenMinApi18Release SKIPPED
:app:lintVitalGreenMinApi23Release SKIPPED
:app:lintVitalRedMinApi18Release SKIPPED
:app:lintVitalRedMinApi23Release SKIPPED
:app:lintVitalYellowMinApi18Release SKIPPED
:app:lintVitalYellowMinApi23Release SKIPPED
:app:lint SKIPPED
```

###四.定义Task类型

首先看一下如何完备一个Task的声明，比如内置的TASK

```
init - Initializes a new Gradle build.
wrapper - Generates Gradle wrapper files.
```
对于Task的描述是通过`group`和`description`属性来设置的，接着来效仿一个：

```
task hello {
    group 'Welcome'
    description 'Produces a greeting'

    doLast {
        println 'Hello, World'
    }
}
```
然后运行`./gradlew tasks`：

```
Welcome tasks
-------------
hello - Produces a greeting
```
OK了！

再来看一个Copy的Task：

```
task copy(type:Copy, group:"Custom",description:"Copies sources to the dest directory"){    from "src"
    into "dest"
}
```
这个Task 内部用到了两个属性`from`和`info`，当这两个字段不满足需求的时候，就需要自定义新的属性了。

```
//自定义Task的类型
class Greeting extends DefaultTask{//1. DefaultTask是最通用的情景
    String msg		//2.声明需要的字段
    String recipient

    @TaskAction	//3默认的TaskAction，这个Task的行为
    void say(){
        println("${msg},${recipient}")
    }

}

task hello(type: Greeting){
    group 'Welcome'
    description 'First Task Type'
    msg 'halo'
    recipient 'leo'
}
```
输出结果如下：

```
> Task :app:hello 
halo,leo
```
最后，可以小小总结一下：
> doLast执行一个Task
> 为Task添加声明
> 将一个自定义的Task转化为Gradle的Task类型并且实例化
> 为Task添加默认的行为（@TaskAction）




