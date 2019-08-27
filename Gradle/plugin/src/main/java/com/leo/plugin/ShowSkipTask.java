package com.leo.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

/**
 * <p>Date:2019-08-23.17:24</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
public class ShowSkipTask extends DefaultTask {


    public ShowSkipTask() {
        setGroup(Constants.SKIP_GROUP_NAME);
        setDescription("该模块是否忽略编译");
    }

    @TaskAction
    void run() {
        SkipBuild skipBuild = (SkipBuild) getProject().getExtensions().getByName(Constants.SKIP);
        System.out.println("> " + Constants.SKIP_GROUP_NAME + " ： " + getProject().getName() + " ignore : " + skipBuild.isSkip());
    }
}
