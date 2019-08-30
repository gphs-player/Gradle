## Plugin

### 一、build.gradle定义

#### 1.继续定义Task

先在`build.gradle`中写一个`Task`

```groovy
project.task("showConfig"){
    doLast {
        println("$project.name:showConfig")
    }
}
```

运行查看结果

```shell
➜  Gradle ./gradlew :app:showConfig
......
Task :app:showConfig
app:showConfig
```



#### 2.换个方式定义Task

刚才我们只定义了Task，使用的时候还是要在gradle文件中手动添加Task进行命名等，而通过plugin的方式进行引入，task会被自动添加到gradle的构建，而且方法名在plugin中已经固定。

在`build.gradle`文件中写代码:

```groovy
class SimplePlugin implements Plugin<Project>{
    @Override
    void apply(Project target) {
        //生成greeting模块，在gradle文件中使用
        target.task("simplePlugin"){
            doLast {
                println("==============")
                println("Hello , Plugin")
                println("==============")
            }
        }
    }
}
//然后引入plugin
apply plugin: SimplePlugin
```

运行查看：

```shell
➜  Gradle ./gradlew :app:simplePlugin
......
> Task :app:simplePlugin
==============
Hello , Plugin
==============
BUILD SUCCESSFUL in 1s
1 actionable task: 1 executed

```

​	我们实现`Plugin`接口写了一个类，覆写`apply`方法并实现了一个打印的`Task`，随后在`build.gradle`文件中引入`apply plugin: SimplePlugin`，这样就算完成了一个自定义的plugin。

#### 3.灵活一点？

如果在刚才的plugin中要打印一些可配置的信息怎么办？通过对象传进来应该挺好：

```groovy
class ShowPlugin implements Plugin<Project>{
    @Override
    void apply(Project target) {
        //生成greeting模块，在gradle文件中使用
        PluginExtension extension =  		        target.getExtensions().create("greeting",PluginExtension.class)
        target.task("showPlugin"){
            doLast {
                println("Hello ,$extension.greeter")
                println("I have msg for you : $extension.msg")
            }
        }
    }
}
//动态在gradle文件中配置
class PluginExtension{
    String greeter = "leo"
    String msg = "msg from the plugin"
}
//模块greeting可用
greeting{
    greeter = "leo"
    msg = "It's ok"
}

apply plugin: ShowPlugin
```

运行查看

```shell
➜  Gradle ./gradlew :app:showPlugin
......
> Task :app:showPluginBuildGradle
Hello ,leo
I have msg for you : It's ok

BUILD SUCCESSFUL in 0s
1 actionable task: 1 executed

```

这样我们就把可配置的数据封装在了`PluginExtension`对象中,生成了名字为`greeting`的`Extension`，那么在build.gradle中就能通过以下方式配置。

```
greeting{
    greeter = "leo"
    msg = "It's ok"
}
```

[参考官方Demo](https://guides.gradle.org/writing-gradle-plugins/#apply_the_plugin_to_the_host_project)



### 二、buildSrc目录

​	但是刚才定义的task都是写在gradle文件中，其实也可以写在java文件中。

​	Java工程下有一个buildSrc目录是会主动参与编译的，但是它也是一个类Module结构，创建过程如下：

#### 1.创建BuildSrc

##### 1.1 手动创建目录buildSrc

​	主工程下创建此目录，和其他子工程同级。

##### 1.2 手动添加build.gradle文件

​	build.gradle文件如下：

```groovy
apply plugin: 'groovy'
dependencies {//调用相关SDK
    compile gradleApi()
    compile localGroovy()
}
```



##### 1.3 完善buildSrc目录

因为buildSrc也是要参与编译的一个module，所以在buildSrc目录下创建`src/main/java`层级目录，



##### 1.4 setting.gradle

将工程添加到编译`include 'buildSrc'`



#### 2.Hello，buildSrc!

先来验证一下buildSrc目录下的资源是自动参与打包并能被其他工程引用的

在java目录下新建一个类（没有创建包）

```java
public class Fun {
    public static void sayHello() {
        System.out.println("Hello from buildSrc!");
    }
}
```

然后在工程内任意build.gradle文件中添加

```groovy
task hello{
    doLast{
        Fun.sayHello()
    }
}
```

我们都能得到类似的输出

```shell
> Task :hello
Hello from buildSrc!
```

这样一来，我们的Task就能抽取到buildSrc工程下，其他工程就会干净很多。



#### 3.定义Task

在java目录下新建包名为`co.leo`的java类

```java
public class HelloTask extends DefaultTask {
    @TaskAction
    public void run(){
        System.out.println("Hello from task " + getPath() + "!");
    }
}
```

继承了`DefaultTask`并添加了使用`@TaskAction`注解的方法，方法名不限。

然后我们在gradle文件中使用

```java
task sayHello(type: com.leo.HelloTask)
```

执行查看

```shell
> Task :sayHello
Hello from task :sayHello!

BUILD SUCCESSFUL in 0s
```



#### 4.定义Plugin

还是刚才的`HelloTask`,这次通过plugin的方式来实现

```java
public class SimplePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create("aSimpleHello", HelloTask.class);
    }
}
```

​	我们的plugin想生成一个名字为`aSimpleHello`的Task，Task的类型是`HelloTask`，做的事情也是在`HelloTask`当中定义。

​	这样`Plugin`的定义就完成了，如何引用？

##### 4.1 全路径引用

在子工程`build.gradle`下引用：

```groovy
apply plugin: com.leo.SimplePlugin
```

执行查看

```shell
➜  Gradle ./gradlew :app:aSH

> Task :app:aSimpleHello
Hello from task :app:aSimpleHello!

BUILD SUCCESSFUL in 1s
1 actionable task: 1 executed
```



##### 4.2 通过id引用

类似APT的id引用，plugin的id引用也是一样，在main目录下生成一个`resources`目录，依次生成`META-INF/gradle-plugins`目录，整个工程的结构看起来是这样：

```shell
└── main
    ├── java
    │   ├── Fun.java
    │   └── com
    │       └── leo
    │           ├── HelloTask.java
    │           └── SimplePlugin.java
    └── resources
        └── META-INF
            └── gradle-plugins
                └── simplePlugin.properties
```

然后在gradle-plugins目录下新建一个`{id}.properties`配置文件，id就是你在引用plugin的时候想用的名字，如果你的包名是`com.bob.plugin`,那么你的配置文件名字用`com.bob.plugin.properties`会比较合适。在这里我的用的是`simplePlugin.properties`,配置文件内容很简单也固定:

```java
implementation-class=包名+类名
```

> 配置文件个格式是KEY-VALUE形式
>
> KEY：固定的是implementation-class
>
> VALUE：就是包名+类名，比如：com.leo.SimplePlugin

配置文件写好之后再Sync一下工程，

在app工程下build.gradle文件下通过id引用：

```groovy
apply plugin: 'simplePlugin'
```

运行查看和通过类名引用的结果是一样的：

```
➜  Gradle ./gradlew :app:aSimpleHello
> Task :app:aSimpleHello
Hello from task :app:aSimpleHello!

BUILD SUCCESSFUL in 0s
1 actionable task: 1 executed
```



### 三、独立工程

​	刚才的plugin想提供给别的工程使用怎么办？copy整个buildSrc? 还是抽取plugin到plugin.gradle文件,copy整个文件？还是上传到maven？

​	

#### 1.新建工程

​	新建一个工程（java-library，因为我们不需要Android的API），项目结构和buildSrc类似：

```shell
└── main
    ├── java
    │   └── com
    │       └── leo
    │           └── plugin
    │               ├── CustomPlugin.java
    │               └── CustomTask.java
    └── resources
        └── META-INF
            └── gradle-plugins
                └── com.leo.plugin.properties
```

CustomTask类：

```java
public class CustomTask extends DefaultTask {
    private String msg;
    private String recipient;
    @TaskAction
    public void sayGreeting() {
        System.out.println("==============");
        System.out.printf("%s,%s!\n",getRecipient(),getMsg());
        System.out.println("==============");
    }
    public String getMsg() {
        return msg;
    }
    public String getRecipient() {
        return recipient;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
```

CustomPlugin类：

```java
public class CustomPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getTasks().create("aShowHello", CustomTask.class, customTask -> {
            //默认配置信息
            customTask.setMsg("This is a greeting!");
            customTask.setRecipient("Leo");
        });
    }
}
```

build.gradle文件

```groovy
apply plugin: 'groovy'
apply plugin: 'maven'

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation gradleApi()
    implementation localGroovy()
}
//上传到本地maven
uploadArchives {
    repositories {
        mavenDeployer {
            pom.groupId = "com.leo.plugin"
            pom.artifactId = "customPlugin"
            pom.version = "1.0.0"
            repository(url: uri('../repo'))
        }
    }
}
```

定义一个`aShowHello`的Task，并通过这个task打印一些信息。我们的plugin名字命名为`com.leo.plugin`

相关类写完之后就能上传到maven了，在主工程下就生成了repo目录和相关文件。



#### 2.引用

在引用的时候，需要先配置主工程的build.gradle文件，添加我们自定义插件的配置：

```groovy
buildscript {
    repositories {
        maven {
            url './repo'
        }
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.3.2'
        classpath 'com.leo.plugin:customPlugin:1.0.0'
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url './repo'
        }
    }
}
```

编译过后，就能在子工程引用

```java
apply plugin: 'com.leo.plugin'
```

运行查看

```shell
➜  Gradle ./gradlew :app:aShowHello
> Task :app:aShowHello
==============
Leo,This is a greeting!!
==============

BUILD SUCCESSFUL in 2s
1 actionable task: 1 executed
```

我们的打印任务就完成了，但是打印的信息还是固定的啊！

aShowHello是`CustomTask`的类型，它有两个变量`msg`和`recipient`,这两个变量是能在gradle中配置的，在子工程的build.gradle文件中添加以下结点

```java
aShowHello{
    msg = "how are you !"
    recipient = "James"
}
```

再次运行查看结果：

```
> Task :app:aShowHello
==============
James,how are you !!
==============
```

