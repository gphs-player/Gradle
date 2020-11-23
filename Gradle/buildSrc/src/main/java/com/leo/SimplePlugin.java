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

//        AppExtension android = target.getExtensions().getByType(AppExtension);
//        def signConfigs = android.getSigningConfigs()
//        println("signConfigs : "+signConfigs.size()+"--\n" + signConfigs.toString())
//        android.getProductFlavors().register("Plugin") { flavor ->
//                flavor.dimension = "default"
//            flavor.applicationId = "com.leo.plugin"
//            flavor.versionName = "2.2.0"
//            flavor.versionCode = 200200
//            flavor.signingConfig = android.getSigningConfigs().getByName("Plugin")
//        }
//        //create manifestPlaceholders
//        android.applicationVariants.all {
//            variant ->
//                    def mergedFlavor = variant.getMergedFlavor()
////            println "variant.productFlavors : " + variant.productFlavors.get(0).name
////            println "variant.buildType.name : " + variant.buildType.name
//            def flavorName = variant.productFlavors.get(0).name
//            def buildTypeName = variant.buildType.name
//            println("create manifestPlaceholders for ${flavorName}")
//            if (flavorName == "proA") {
////                if (buildTypeName == "debug"){
////                    mergedFlavor.manifestPlaceholders = ["PUSH_VALUE": ""]
////                }
//            } else if (flavorName == "proB") {
//
//            } else if (flavorName == "proC") {
//
//            } else {
////                    throw Exception("未知的构建渠道!!!")
//            }
//
//            //这里配置的优先级比buildType低 比Flavor高，在buildType中配置manifestPlaceholders会替换掉
////                mergedFlavor.manifestPlaceholders = ["PUSH_VALUE" : flavorName + "---" + buildTypeName
////                                                     , "MAP_VALUE": flavorName + "&&&" + buildTypeName]
//        }
    }
}
