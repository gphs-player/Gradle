package com.leo.transform;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


/**
 * <p>Date:2020-03-30.15:56</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
public class MyTransform extends Transform {
    private static final String TAG = "LeoTransform";

    @Override
    public String getName() {
        return "LeoTransform";
    }


    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        outputProvider.deleteAll();
        Collection<TransformInput> inputs = transformInvocation.getInputs();


        for (TransformInput input : inputs) {
            Collection<JarInput> jarInputs = input.getJarInputs();
            for (JarInput jarInput : jarInputs) {
//                File tmpFile = new File(jarInput.getFile().getParent() + File.separator + "classes_temp.jar");
            String md5Name = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath());
            File dest = outputProvider.getContentLocation(jarInputs + md5Name,
                    jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
            FileUtils.copyFile(jarInput.getFile(), dest);
            }



            Collection<DirectoryInput> directoryInputs = input.getDirectoryInputs();
            for (DirectoryInput directoryInput : directoryInputs) {
                String name = directoryInput.getName();
                System.out.println("directoryInput : " + name);
                //.class源文件地址
                String absolutePath = directoryInput.getFile().getAbsoluteFile().getAbsolutePath();
                System.out.println("absolutePath : " + absolutePath);
                //transform地址
                File contentLocation = outputProvider.getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(), directoryInput.getScopes()
                        , Format.DIRECTORY);
                System.out.println("contentLocation : " + contentLocation);
                File[] files = directoryInput.getFile().listFiles();
                LinkedList<File> list = new LinkedList<>();
                HashSet<File> resultlist = new HashSet<>();
                for (File file : files) {
                    Collections.addAll(list, file);
                }

                while (!list.isEmpty()) {
                    File fileDir = list.remove(0);
                    for (File zfile : fileDir.listFiles()) {
                        if (zfile.isDirectory()) {
                            list.add(zfile);
                        } else {
                            String targetname = zfile.getName();
                            if (targetname.contains("Activity") &&
                                    targetname.endsWith(".class")
                                    && !targetname.endsWith("R.class")
                                    && !targetname.contains("R$")
                                    && !targetname.endsWith("BuildConfig.class")) {
                                resultlist.add(zfile);
                            }

                        }
                    }
                }
                for (File file : resultlist) {
                    System.out.println(getName() + "::: " + file);
                }
                for (File file : resultlist) {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    ClassReader classReader = new ClassReader(fileContent);
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                    ClassVisitor cv = new MyClassVisitor(classWriter);
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES);

                    byte[] bytes = classWriter.toByteArray();

                    FileOutputStream fio = new FileOutputStream(file);
                    fio.write(bytes);
                    fio.close();
                    System.out.println("Now generator finished !");
                }

                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
                FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }
        }

    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getReferencedScopes() {
        return TransformManager.EMPTY_SCOPES;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }
}
