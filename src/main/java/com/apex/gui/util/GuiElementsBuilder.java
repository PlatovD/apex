package com.apex.gui.util;

import com.apex.model.scene.RenderObject;
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

        HBox hbox = new HBox();

        Button selectBtn = new Button(metadata.name);
        selectBtn.setFont(Font.font(10));
        selectBtn.setOnAction(e -> {
            if (onChangeActiveStatus != null) {
                onChangeActiveStatus.accept(metadata.name);
            }
        });

        Button visibilityBtn = new Button("V");
        visibilityBtn.setFont(Font.font(10));
        visibilityBtn.setOnAction(e -> {
            if (onChangeVisibility != null) {
                onChangeVisibility.accept(metadata.name);
            }
        });

        Button addTextureBtn = new Button("+T");
        addTextureBtn.setFont(Font.font(10));
        addTextureBtn.setOnAction(e -> {
            if (onAddTexture != null) {
                onAddTexture.accept(metadata.name);
            }
        });

        Button removeTextureBtn = new Button("-T");
        removeTextureBtn.setFont(Font.font(10));
        removeTextureBtn.setOnAction(e -> {
            if (onRemoveTexture != null) {
                onRemoveTexture.accept(metadata.name);
            }
        });

        Button deleteBtn = new Button("DEL");
        deleteBtn.setFont(Font.font(10));
        deleteBtn.setOnAction(e -> {
            if (onDelete != null) {
                onDelete.accept(metadata.name);
            }
        });

        hbox.getChildren().addAll(
                visibilityBtn,
                selectBtn,
                addTextureBtn,
                removeTextureBtn,
                deleteBtn
        );

        hbox.setUserData(metadata.name);
        return hbox;
    }
}
