package com.apex.gui.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class ErrorDialogRenderer {

    private static long lastErrorTime = 0;
    private static int errorCount = 0;
    private static final int STORM_THRESHOLD = 5; // errors
    private static final long STORM_PERIOD = 3000; // ms

    private static javafx.scene.layout.VBox notificationContainer;
    private static final int DISPLAY_DURATION_MS = 5000;

    public static void setNotificationContainer(javafx.scene.layout.VBox container) {
        notificationContainer = container;
    }

    public static void showError(String title, Throwable throwable) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showError(title, throwable));
            return;
        }

        // Log to console as well
        System.err.println("Error: " + title);
        if (throwable != null)
            throwable.printStackTrace();

        if (notificationContainer == null) {
            System.err.println("Notification container not set, falling back to basic alert.");
            return;
        }

        // Storm Protection
        long now = System.currentTimeMillis();
        if (now - lastErrorTime < STORM_PERIOD) {
            errorCount++;
            if (errorCount > STORM_THRESHOLD) {
                return;
            }
        } else {
            errorCount = 1;
        }
        lastErrorTime = now;

        createToast(title, throwable);
    }

    private static void createToast(String title, Throwable throwable) {
        VBox toast = new VBox(5);
        toast.getStyleClass().add("error-toast");
        toast.setPadding(new Insets(10, 15, 10, 15));
        toast.setMaxWidth(340);
        toast.setPickOnBounds(true); // Allow interactions within the toast

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("error-toast-title");
        titleLabel.setWrapText(true);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        Button closeBtn = new Button("×");
        closeBtn.getStyleClass().add("error-toast-close");
        closeBtn.setOnAction(e -> removeToast(toast));

        topRow.getChildren().addAll(titleLabel, closeBtn);

        toast.getChildren().add(topRow);

        if (throwable != null) {
            String msg = throwable.getMessage();
            if (msg == null || msg.isEmpty())
                msg = throwable.getClass().getSimpleName();
            Label msgLabel = new Label(msg);
            msgLabel.getStyleClass().add("error-toast-msg");
            msgLabel.setWrapText(true);
            toast.getChildren().add(msgLabel);
        }

        notificationContainer.getChildren().add(toast);

        // Auto-dismiss logic
        PauseTransition delay = new PauseTransition(Duration.millis(DISPLAY_DURATION_MS));
        delay.setOnFinished(e -> removeToast(toast));
        delay.play();
    }

    private static void removeToast(VBox toast) {
        if (notificationContainer != null) {
            notificationContainer.getChildren().remove(toast);
        }
    }

    private static String formatException(Throwable t) {
        if (t == null)
            return "No details provided.";
        StringBuilder sb = new StringBuilder();
        sb.append(t.getClass().getName()).append(": ").append(t.getMessage()).append("\n\n");
        for (StackTraceElement element : t.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
