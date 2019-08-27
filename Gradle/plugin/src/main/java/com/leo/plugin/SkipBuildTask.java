package com.leo.plugin;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskAction;

import java.util.Set;

/**
 * <p>Date:2019-08-26.15:13</p>
 * <p>Author:niu bao</p>
 * <p>Desc: 忽略模块编译</p>
 */
public class SkipBuildTask extends DefaultTask {

    public SkipBuildTask() {
        setGroup(Constants.SKIP_GROUP_NAME);
        setDescription("编译流程忽略某个模块");

    }
}
