package com.leo;

import org.gradle.BuildResult;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SimplePlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {

//        Collection<IncludedBuild> includedBuilds = project.getGradle().getIncludedBuilds();
        target.getGradle().buildFinished(new Action<BuildResult>() {
            @Override
            public void execute(BuildResult buildResult) {
                //捕获构建过程中抛出的异常
                if (buildResult.getFailure() == null) {
                    System.out.println("-------QUICK BUILD FINISHED-------");
                } else {
                    System.out.println("build failure - " + buildResult.getFailure());
                }
            }
        });
    }
}
