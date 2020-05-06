package edu.wpi.cs3733.d20.teamL.util;

import edu.wpi.cs3733.d20.teamL.App;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.FutureTask;

@Slf4j
public class AsyncTaskManager {
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();
    public static boolean isTaskRunning = false;

    public static ForkJoinTask newTask(VoidMethod task) {
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
                task.execute();
                return true;
            }
        };
		isTaskRunning = true;
        forkJoinPool.execute(newForkJoinTask);
        isTaskRunning = false;

        return newForkJoinTask;
    }

    public static void startTaskWithPopup(VoidMethod task, String loadMessage, String doneMessage) {
        App.allowCacheUpdates = false;

        Executor uiExec = Platform::runLater;

        Alert loading = new Alert(Alert.AlertType.NONE);
        loading.setResult(ButtonType.OK);
        ImageView spinner = new ImageView(new Image("edu/wpi/cs3733/d20/teamL/assets/spinner.gif"));
        spinner.setPreserveRatio(true);
        spinner.setFitWidth(40);
        loading.setGraphic(spinner);
        loading.setContentText(loadMessage);

        Button btn = new Button("Start");
        btn.setOnAction(evt -> {
            btn.setDisable(true);

            // make alert appear / disappear
            Thread t = new Thread(() -> {
                boolean showing = false;
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        log.error("{}", ex);
                    }
                    Runnable action = showing ? loading::close : loading::show;
                    Platform.runLater(action);
                    showing = !showing;
                }
            });
            t.setDaemon(true);
            t.start();
        });

        loading.show();

        AsyncTaskManager.newTask(() -> {
            task.execute();
            log.info(doneMessage);
            uiExec.execute(new FutureTask<>(() -> {
                loading.close();
                showDoneDialogue(doneMessage);
                return null;
            }));
        });
    }

    private static void showDoneDialogue(String message) {
        Alert done = new Alert(Alert.AlertType.INFORMATION);
        done.setContentText(message);
        done.showAndWait();
    }
}
