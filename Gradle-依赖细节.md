# 依赖

|名称|类型|
|---|---|
| Module-Lib | Library |
| Module-Framework | Library |
| app | Application |

三者之间依赖关系如下图：
<pre>
   	   app
		↓
		↓(implementation/api)
		↓ 
Module-Framework
		↓
		↓ (api)
		↓
	Module-Lib
</pre>
### implementation和api

这个时候如果`app`访问到了 `Module-Lib`库中的类，那么`Module-Framework`在添加`Module-Lib`依赖的时候必须用`api`的引用方式，或者单独依赖`Module-Lib`，因为`api`才会把代码打包并且进行编译。