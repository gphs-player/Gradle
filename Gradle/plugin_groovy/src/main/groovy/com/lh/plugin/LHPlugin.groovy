package com.lh.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project


class LHPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        def android = target.extensions.findByName(AppExtension)
        println(android)
        target.task("aGetProjectName") {
            doLast {
                println("----------START--------------")
                println(target.name)
                println("----------OVER--------------")
            }
        }
    }
}
