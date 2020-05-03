package edu.wpi.cs3733.d20.teamL.util;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class AsyncTaskManager {

    private static ForkJoinPool forkJoinPool = new ForkJoinPool();

    public static ForkJoinTask newTask(VoidFunction task) {
        ForkJoinTask newForkJoinTask = new ForkJoinTask() {
            @Override
            public Object getRawResult() {
                return null;
            }

            @Override
            protected void setRawResult(Object value) {

            }

            @Override
            protected boolean exec() {
                task.update();
                return true;
            }
        };

        forkJoinPool.execute(newForkJoinTask);

        return newForkJoinTask;
    }

}
