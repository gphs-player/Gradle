package com.leo.plugin;

import org.gradle.BuildListener;
import org.gradle.BuildResult;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.ProjectEvaluationListener;
import org.gradle.api.ProjectState;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskCollection;

import java.util.Set;


public class CustomPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getPlugins().apply(CustomPlugin.class);
        System.err.println("> quickBuild Init : " + target.getDisplayName());

        target.getExtensions().create(Constants.SKIP, SkipBuild.class);

        createTask(target, Constants.SKIP_TASK, ShowSkipTask.class);
        createTask(target, Constants.SKIP_BUILD_TASK, SkipBuildTask.class);

        target.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                PluginContainer plugins = project.getPlugins();
                Plugin plugin = plugins.findPlugin("com.android.application");
                System.out.println("hasPlugin : "+plugin);
                SkipBuild skipBuild = (SkipBuild) project.getExtensions().getByName(Constants.SKIP);
                System.out.println("isSkip : "+skipBuild.isSkip());
                if (plugin!=null && skipBuild.isSkip()) {
                    throw new GradleException("application 模块不能忽略!!!");
                }
            }
        });
        //在初始化阶段就会执行，不会在Task内部执行
        target.getGradle().getTaskGraph().beforeTask(new Action<Task>() {
            @Override
            public void execute(Task task) {
                SkipBuild skipBuild = (SkipBuild) target.getExtensions().getByName("skipBuild");
                if (skipBuild.isSkip() && task.getProject().getName().equals(target.getName())) {
                    System.out.println("::::ignore: " + task.getProject().getDisplayName()+ "----" + task.getName());
                }
            }
        });

    }

    public static void createTask(Project project, String taskName, Class type) {
        project.getTasks().create(taskName, type);
    }
}
