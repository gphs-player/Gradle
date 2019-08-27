package com.leo.plugin;

/**
 * <p>Date:2019-08-23.17:19</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
public class SkipBuild {
    private boolean skip;

    public SkipBuild() {
    }

    public SkipBuild(boolean skip) {
        this.skip = skip;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
