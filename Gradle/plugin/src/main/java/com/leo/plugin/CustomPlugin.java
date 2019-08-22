package com.leo.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;


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
