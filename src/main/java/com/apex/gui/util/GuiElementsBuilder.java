package com.apex.gui.util;

import com.apex.model.scene.RenderObject;
import com.apex.model.util.RenderObjectStatus;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public class GuiElementsBuilder {
    public static Node createObjectNode(
            RenderObject renderObject,
            Consumer<String> onDelete,
            Consumer<String> onAddTexture,
            Consumer<String> onRemoveTexture,
            Consumer<String> onChangeVisibility,
            Consumer<String> onChangeActiveStatus) {

        RenderObject.RenderObjectMetadata metadata = renderObject.getMetadata();

        VBox container = new VBox(2);
        container.getStyleClass().add("model-item-container");

        // --- Model Row ---
        HBox modelRow = new HBox(4);
        modelRow.getStyleClass().add("model-node");
        modelRow.getStyleClass().add(metadata.status.name().toLowerCase());
        if (!metadata.isVisible) {
            modelRow.getStyleClass().add("hidden");
        }

        modelRow.setPrefWidth(230);
        modelRow.setMaxWidth(230);

        Button visibilityBtn = createVisibilityButton(metadata.isVisible);
        visibilityBtn.getStyleClass().addAll("model-btn", "visibility");
        visibilityBtn.setOnAction(e -> onChangeVisibility.accept(metadata.name));

        Button selectBtn = new Button(metadata.name);
        selectBtn.getStyleClass().addAll("model-btn", "name");
        selectBtn.setMinWidth(60);
        selectBtn.setPrefWidth(120);
        selectBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(selectBtn, Priority.ALWAYS);

        if (metadata.status == RenderObjectStatus.UNACTIVE) {
            selectBtn.getStyleClass().add("inactive");
        }
        selectBtn.setOnAction(e -> onChangeActiveStatus.accept(metadata.name));

        Button deleteBtn = createIconButton(
                "M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z",
                "delete-icon",
                0.6);
        deleteBtn.getStyleClass().addAll("model-btn", "delete");
        deleteBtn.setOnAction(e -> onDelete.accept(metadata.name));

        modelRow.getChildren().addAll(visibilityBtn, selectBtn, deleteBtn);

        // --- Texture Row ---
        HBox textureRow = new HBox(4);
        textureRow.getStyleClass().add("texture-row");
        textureRow.setPadding(new javafx.geometry.Insets(0, 0, 0, 24)); // Indent

        String textureName = renderObject.isTextured() ? renderObject.getTexture().getCache() : "Solid Color";
        Label textureLabel = new Label(textureName);
        textureLabel.getStyleClass().add("texture-label");
        textureLabel.setMinWidth(60);
        textureLabel.setMaxWidth(130);
        HBox.setHgrow(textureLabel, Priority.ALWAYS);

        Button textureActionBtn;
        if (renderObject.isTextured()) {
            textureActionBtn = createIconButton(
                    "M19 13H5v-2h14v2z",
                    "texture-remove-icon",
                    0.5);
            textureActionBtn.getStyleClass().addAll("model-btn", "texture-remove");
            textureActionBtn.setOnAction(e -> onRemoveTexture.accept(metadata.name));
        } else {
            textureActionBtn = createIconButton(
                    "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z",
                    "texture-add-icon",
                    0.5);
            textureActionBtn.getStyleClass().addAll("model-btn", "texture-add");
            textureActionBtn.setOnAction(e -> onAddTexture.accept(metadata.name));
        }

        textureRow.getChildren().addAll(textureLabel, textureActionBtn);

        container.getChildren().addAll(modelRow, textureRow);
        container.setUserData(metadata.name);
        return container;
    }

    public static Node createCameraNode(
            String name,
            boolean isActive,
            Consumer<String> onSelect,
            Consumer<String> onDelete) {

        HBox hbox = new HBox(4);
        hbox.getStyleClass().add("model-node"); // Reuse model-node style for consistency
        if (isActive) {
            hbox.getStyleClass().add("active");
        }

        hbox.setPrefWidth(230);
        hbox.setMaxWidth(230);

        Button activeIndicator = createIconButton(
                "M13 7h-2V6.08c0-1.13.92-2.08 2.09-2.08s2.1 1 2.1 2.12V18c0 1.1-.9 2-2 2H5c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2h2.18l2.58-3H12c1.1 0 2 .9 2 2v2h2V3h2v18h-2v-1h-2v1h-2v-2h2v-1h-2v1h-2v-2h2v-1h-2v1h-2V6h2V5h4v2h-2z",
                "camera-icon",
                0.6);
        activeIndicator.getStyleClass().addAll("model-btn", "camera-active");
        if (!isActive) {
            activeIndicator.getStyleClass().add("inactive-camera");
        }
        activeIndicator.setOnAction(e -> onSelect.accept(name));

        Button selectBtn = new Button(name);
        selectBtn.getStyleClass().addAll("model-btn", "name");
        selectBtn.setMinWidth(60);
        selectBtn.setPrefWidth(120);
        selectBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(selectBtn, Priority.ALWAYS);
        selectBtn.setOnAction(e -> onSelect.accept(name));

        Button deleteBtn = createIconButton(
                "M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z",
                "delete-icon",
                0.6);
        deleteBtn.getStyleClass().addAll("model-btn", "delete");
        deleteBtn.setOnAction(e -> onDelete.accept(name));

        // Дефолтную камеру удалять нельзя
        if (name.equals(com.apex.core.Constants.DEFAULT_CAMERA_NAME)) {
            deleteBtn.setDisable(true);
            deleteBtn.setOpacity(0.3);
        }

        hbox.getChildren().addAll(activeIndicator, selectBtn, deleteBtn);
        return hbox;
    }

    private static Button createIconButton(String svgPath, String cssClass, double scale) {
        Button btn = new Button();
        SVGPath path = new SVGPath();
        path.setContent(svgPath);
        path.setScaleX(scale);
        path.setScaleY(scale);
        path.getStyleClass().add(cssClass);

        StackPane graphic = new StackPane(path);
        graphic.setPrefSize(16, 16);
        btn.setGraphic(graphic);
        return btn;
    }

    private static Button createVisibilityButton(boolean isVisible) {
        if (isVisible) {
            return createIconButton(
                    "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z",
                    "eye-icon",
                    0.6);
        } else {
            return createIconButton(
                    "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z",
                    "eye-icon-hidden",
                    0.6);
        }
    }
}
