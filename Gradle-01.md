##新建Gradle项目

在一个empty的目录下执行`gradle init`命令

```groovy
➜  basic-demo tree
.
├── build.gradle		//当前项目的任务配置脚本
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties 	//Gradle Wrapper 的配置属性
├── gradlew		//Unix平台下的Gradle Wrapper脚本
├── gradlew.bat		//Windows平台下的Gradle Wrapper脚本
└── settings.gradle		//Settings配置脚本，用来配置参与构建的项目

2 directories, 6 files
```
然后查看当前的目录结构，这就是一个初始化的Gradle项目。
> 可使用`gradle init --type  java-library`指定初始化的工程类型，支持的类型有`'basic', 'groovy-library', 'java-library', 'pom', 'scala-library'.`<br>
> 如果不指定类型，Gradle会根据你当前的项目环境去寻找最合适的初始化类型，比如在项目中发现了`pom.xml`文件，就会指定“pom”作为初始化类型。如果Gradle无法找到合适的类型，就会生成“basic”类型。

##怎么使用Gradle？
###1.创建TASK
---
Gradle通过`Groovy`和`Kotlin`提供了API来创建和配置`Task`，一个`Project`包含一系列可执行的`Task`，执行一些简单的操作。
举个例子，Gradle中有一个叫做`Copy`的东西，能从一个地方把文件复制到另外一个地方，它是Gradle的一个较核心的类。来看看怎么用？

* 新建一个src文件夹，并在文件夹下新建一个myFile的文件。<br>
* 在`build.gradle`中定义一个名字叫`copy`的`Task`,写法如下：

 ```groovy
 task copy(type: Copy, group: "Custom", description: "Copies sources to the dest directory") {
    from "src"
    into "dest"
}
 ```
 在上面的代码中，`group`和`description`可以省略不写。然后来执行我们新写的`copy`任务：

 ```shell
➜  basic-demo ./gradlew copy
:copy

 BUILD SUCCESSFUL

 Total time: 0.918 secs 
 ```
然后可以验证一下我们的任务是否执行成功。

 

###2.引入Plugin
---
`Gradle`内部包含了很多`Plugin`,已经发布的一个`base`的`Plugin`,可以结合一个`Zip`的核心类库创建一个归档任务。
在`build.gradle`文件里添加代码：

```
plugins{//先引入base的Plugin
    id "base"
}
//定义Task
task zip(type: Zip,group:"Archive",description:"Archive sources in a zip file"){
    from "src"
    setArchiveName "basic-demo-1.0.zip"
}
```
然后执行`zip`任务
```
➜  basic-demo ./gradlew zip
:zip UP-TO-DATE

BUILD SUCCESSFUL

Total time: 0.721 secs
```
然后可以看到在`build`目录下生成了我们想要的文件：

```
➜  basic-demo tree
.
├── build
│   └── distributions
│       └── basic-demo-1.0.zip
├── build.gradle
├── dest
│   └── myFile.txt
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src
    └── myFile.txt

6 directories, 9 files
```

