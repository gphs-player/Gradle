&nbsp;&nbsp;&nbsp;&nbsp;Gradle的核心类库是一种基于程序的依赖语言，从Gradle的方面来说就是你可以在Task之间定义task和依赖。Gradle可以保证这些Task能够按照依赖关系的顺序执行，而且每个Task只会被执行一次。所有Task在执行之前会生成一个有向非循环图，而Gradle在执行Task之前会去编译所有依赖的任务图（graph）。
### 一、构建生命周期

一次Gradle的构建有三个显著的阶段
#### 1.Initialization（初始化）

Gradle支持单个工程和多个工程构建，在`Initialization`阶段，Gradle会决定有哪些工程参与构建，并且为每个工程生成一个`Project`对象。
#### 2.Configuration（配置）

* 在这个阶段，相关的`project`对象已经被配置结束，所有工程的编译脚本都会作为构建的一部分被执行。<br>
* Gradle行为背后的基本规则是：从当前目录开始向下递归寻找Task的名字并执行，需要注意的是，Gradle总是评估每个工程并且创建所有存在的task的对象，然后根据task的名称、参数以及所在的目录，Gradle会过滤哪些task应该被执行。因为Gradle的交叉项目配置，每个工程都会在task执行前被评估。
* Gradle1.4版本介绍了一个新的待孵化概念[`configuration on demand`](https://docs.gradle.org/current/userguide/multi_project_builds.html#sec:configuration_on_demand),在这种模式下，Gradle只会配置相关的工程。

#### 3. Execution（执行）

Gradle判定task的集合，创建并且配置它们都是在`Configuration`阶段，以备后续执行。待执行task的集合是由task的名字和参数以及当前`gradle`命令所在的目录决定的。然后Gradle才会执行选中的Task。



### 二、Hook

这三个阶段我们怎么利用？

```
//初始化阶段之后，配置阶段之前
//项目执行前
gradle.beforeProject {
    project -> 
}
//配置阶段之后，执行阶段之前
//Task图生成
gradle.taskGraph.whenReady {
    graph -> 
}
//执行阶段之后
//构建完成
gradle.buildFinished {
    result->
}
```