package com.leo.transform;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * <p>Date:2020-03-30.16:00</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
public class TransformPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        BaseExtension extension = project.getExtensions().findByType(BaseExtension.class);
//        AppExtension appExtension = (AppExtension) project.getProperties().get("android");
        extension.registerTransform(new MyTransform());
    }
}
