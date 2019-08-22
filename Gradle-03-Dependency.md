### 一、添加依赖



想添加其他的类库到项目中？
默认情况下，工程下的`build.gradle`文件有一个`dependencies`项：

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:27.1.1'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
}
```
&nbsp;&nbsp;&nbsp;&nbsp;Gradle支持几种不同的方式添加依赖，比较通用的就是使用引号分割开`group`,`name`,`version`,按照约定的语法将你需要的类库添加在`dependencies`区块下。<br><br>
&nbsp;&nbsp;&nbsp;&nbsp;每个依赖项的分配都伴随着一个configuration，Android项目提供了`compile`,`runtime`,`testCompile`,`testRuntime`等配置选项，你可以定义自己的依赖项。<br><br>
&nbsp;&nbsp;&nbsp;&nbsp;dependency的完整语法：`testImplementation group:'junit',name:'junit',version:'4.12'`,它完全等价于`testImplementation 'junit:junit:4.12'`

> 还有一种合法但是不被推荐的写法，就是通过“+”号：`testCompile 'junit:junit:4.+'`,它要求任何超过4.0版本的库都可以参与编译，直到编译通过。但是这种方法会造成编译不确定性且复用性低。推荐使用确定版本的库。

<br>
如果你想单独添加一些文件到配置中，可以使用`files`和`fileTree`,而不必使用`dependencies`。

```
dependencies {
    compile files('libs/a.jar', 'libs/b.jar')
    compile fileTree(dir: 'libs', include: '*.jar')
}
```



### 二、传递依赖

终端运行

```shell
➜  HelloWorld ./gradlew :app:dependencies --configuration  releaseCompileClasspath

> Task :app:dependencies

------------------------------------------------------------
Project :app
------------------------------------------------------------

releaseCompileClasspath - Resolved configuration for compilation for variant: release
+--- com.android.support:appcompat-v7:27.1.1
|    +--- com.android.support:support-annotations:27.1.1
|    +--- com.android.support:support-core-utils:27.1.1
|    |    +--- com.android.support:support-annotations:27.1.1
|    |    \--- com.android.support:support-compat:27.1.1
|    |         +--- com.android.support:support-annotations:27.1.1
|    |         \--- android.arch.lifecycle:runtime:1.1.0
|    |              +--- android.arch.lifecycle:common:1.1.0
|    |              \--- android.arch.core:common:1.1.0
|    +--- com.android.support:support-fragment:27.1.1
|    |    +--- com.android.support:support-compat:27.1.1 (*)
|    |    +--- com.android.support:support-core-ui:27.1.1
|    |    |    +--- com.android.support:support-annotations:27.1.1
|    |    |    +--- com.android.support:support-compat:27.1.1 (*)
|    |    |    \--- com.android.support:support-core-utils:27.1.1 (*)
|    |    +--- com.android.support:support-core-utils:27.1.1 (*)
|    |    +--- com.android.support:support-annotations:27.1.1
|    |    +--- android.arch.lifecycle:livedata-core:1.1.0
|    |    |    +--- android.arch.lifecycle:common:1.1.0
|    |    |    +--- android.arch.core:common:1.1.0
|    |    |    \--- android.arch.core:runtime:1.1.0
|    |    |         \--- android.arch.core:common:1.1.0
|    |    \--- android.arch.lifecycle:viewmodel:1.1.0
|    +--- com.android.support:support-vector-drawable:27.1.1
|    |    +--- com.android.support:support-annotations:27.1.1
|    |    \--- com.android.support:support-compat:27.1.1 (*)
|    \--- com.android.support:animated-vector-drawable:27.1.1
|         +--- com.android.support:support-vector-drawable:27.1.1 (*)
|         \--- com.android.support:support-core-ui:27.1.1 (*)
+--- com.android.support.constraint:constraint-layout:1.1.2
|    \--- com.android.support.constraint:constraint-layout-solver:1.1.2
+--- project :country
\--- com.leo.library:man:0.0.1

(*) - dependencies omitted (listed previously)


BUILD SUCCESSFUL in 1s
1 actionable task: 1 executed
```
可以看到当前工程所有的依赖库，然后在`android`代码块下添加代码

```
configurations {
        all {
            transitive = false//这个值默认是true，附属依赖都会被添加到工程中。
        }
    }
```
再次查看依赖项；

```groovy
➜  HelloWorld ./gradlew :app:dependencies --configuration  releaseCompileClasspath

> Task :app:dependencies

------------------------------------------------------------
Project :app
------------------------------------------------------------

releaseCompileClasspath - Resolved configuration for compilation for variant: release
+--- com.android.support:appcompat-v7:27.1.1
+--- com.android.support.constraint:constraint-layout:1.1.2
+--- project :country
\--- com.leo.library:man:0.0.1


BUILD SUCCESSFUL in 0s
1 actionable task: 1 executed
```
可以看到只有当前工程直接添加的依赖被添加到工程中，附属于依赖项的资源没有被添加。也可以单独为工程添加依赖过滤：

```
    implementation ('com.leo.library:man:0.0.1'){
        transitive = false
    }
```
当然也可以使用`exclude`控制附属依赖项的编译：

```
    implementation ('com.leo.library:man:0.0.1'){
        exclude group:'com.google.code.gson'
    }
```
如果在本地有文件需要依赖，可以使用如下方式：

```
repositories{
        flatDir{
            dirs 'libs'
        }
    }
```
这是将依赖文件添加到`dependencies`的另外选择方式，等同于`files`和`fileTree`
