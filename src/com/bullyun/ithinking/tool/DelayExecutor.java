package com.bullyun.ithinking.tool;

public class DelayExecutor {
    private Runnable runnable;

    public void delayExecute(Runnable runnable) {
        this.runnable = runnable;
    }

    public void doDelayExecute() {
        if (runnable != null) {
            Runnable runnable = this.runnable;
            this.runnable = null;
            runnable.run();
        }
    }
}
