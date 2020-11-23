package com.leo.transform;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MyClassVisitor extends ClassVisitor implements Opcodes {

    public static final String TAG = "MyClassVisitor:::";


    public MyClassVisitor(ClassVisitor classVisitor) {
        super(ASM5, classVisitor);
    }

    @Override
    public void visit(int i, int i1, String s, String s1, String s2, String[] strings) {
        cv.visit(i, i1, s, s1, s2, strings);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String sign, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, sign, exceptions);
        if (!name.equals("<init>")) {
            methodVisitor = new MyMethodVisitor(methodVisitor);
        }
        return methodVisitor;
    }


}
