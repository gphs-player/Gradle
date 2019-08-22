package com.lh.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project


class LHPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.task("aGetProjectName") {
            doLast {
                println("----------START--------------")
                println(target.name)
                println("----------OVER--------------")
            }
        }
    }
}
