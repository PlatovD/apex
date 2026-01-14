package com.apex.gui.util;

import com.apex.model.scene.RenderObject;
import com.apex.model.util.RenderObjectStatus;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.util.function.Consumer;

public class GuiElementsBuilder {
    public static Node createObjectNode(
            RenderObject.RenderObjectMetadata metadata,
            Consumer<String> onDelete,
            Consumer<String> onAddTexture,
            Consumer<String> onRemoveTexture,
            Consumer<String> onChangeVisibility,
            Consumer<String> onChangeActiveStatus) {

        HBox hbox = new HBox(4);

        hbox.getStyleClass().add("model-node");
        hbox.getStyleClass().add(metadata.status.name().toLowerCase());
        if (!metadata.isVisible) {
            hbox.getStyleClass().add("hidden");
        }

        Button visibilityBtn = new Button(metadata.isVisible ? "✓" : "✕");
        visibilityBtn.getStyleClass().addAll("model-btn", "visibility");
        visibilityBtn.setOnAction(e -> onChangeVisibility.accept(metadata.name));

        Button selectBtn = new Button(metadata.name);
        selectBtn.getStyleClass().addAll("model-btn", "name");
        if (metadata.status == RenderObjectStatus.UNACTIVE) {
            selectBtn.getStyleClass().add("inactive");
        }
        selectBtn.setOnAction(e -> onChangeActiveStatus.accept(metadata.name));

        Button addTextureBtn = new Button("+");
        addTextureBtn.getStyleClass().addAll("model-btn", "texture-add");
        addTextureBtn.setOnAction(e -> onAddTexture.accept(metadata.name));

        Button removeTextureBtn = new Button("-");
        removeTextureBtn.getStyleClass().addAll("model-btn", "texture-remove");
        removeTextureBtn.setOnAction(e -> onRemoveTexture.accept(metadata.name));

        Button deleteBtn = new Button("×");
        deleteBtn.getStyleClass().addAll("model-btn", "delete");
        deleteBtn.setOnAction(e -> onDelete.accept(metadata.name));

        hbox.getChildren().addAll(visibilityBtn, selectBtn, addTextureBtn, removeTextureBtn, deleteBtn);
        hbox.setUserData(metadata.name);
        return hbox;
    }
}
