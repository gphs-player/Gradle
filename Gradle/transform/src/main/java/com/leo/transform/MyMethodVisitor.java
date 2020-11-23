package com.leo.transform;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <p>Date:2020-03-30.21:17</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
public class MyMethodVisitor extends MethodVisitor implements Opcodes {

    private boolean mInject = false;

    public MyMethodVisitor(MethodVisitor methodVisitor) {
        super(Opcodes.ASM5, methodVisitor);
    }


    @Override
    public void visitCode() {
        super.visitCode();
        if (mInject) {

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("method call");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }

    @Override
    public void visitInsn(int opcode) {

        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
                || opcode == Opcodes.ATHROW) {
            if (mInject) {
                //方法返回之前打印end
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn("method end : " );
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }
        }
        mv.visitInsn(opcode);
    }


    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        System.out.println("descriptor :" + descriptor);
        if (descriptor != null && descriptor.equals("Lcom/leo/gradle/NewTag;")) {
            mInject = true;
        }
        return super.visitAnnotation(descriptor, visible);
    }
}
