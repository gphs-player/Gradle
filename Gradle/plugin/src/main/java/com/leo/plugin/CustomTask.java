package com.leo.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

public class CustomTask extends DefaultTask {
    private String msg;
    private String recipient;

    @TaskAction
    public void sayGreeting() {
        System.out.println("==============");
        System.out.printf("%s,%s!\n",getRecipient(),getMsg());
        System.out.println("==============");
    }

    public String getMsg() {
        return msg;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
