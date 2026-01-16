package com.apex.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public interface Controller extends Initializable {
    void onOpenModelHandler(ActionEvent event);

    void onDeleteModelHandler(String filename);

    @FXML
    void onSaveModelHandler(ActionEvent event);

    void onAddTextureHandler(String filename);

    void onRemoveTextureHandler(String filename);

    void onChangeVisibilityHandler(String filename);

    void onChangeActiveStatusHandler(String filename);

    void startOperation();

    void endOperation();

    void refreshGui();

    void refreshBuffer(int newWidth, int newHeight);

    void refreshRender();

    void handleCameraForward(ActionEvent event);

    void handleCameraBackward(ActionEvent event);

    void handleCameraLeft(ActionEvent event);

    void handleCameraRight(ActionEvent event);

    void handleCameraUp(ActionEvent event);

    void handleCameraDown(ActionEvent event);

    @FXML
    @Override
    void initialize(URL url, ResourceBundle resourceBundle);

    @FXML
    void handleBaseTextureColorChange(ActionEvent event);

    void handleScrollPaneKeyPressed(KeyEvent keyEvent);

    void handleRenderPaneClick(MouseEvent mouseEvent);

    void handleAffineCollapseToggle(MouseEvent mouseEvent);

    void handleCamerasCollapseToggle(MouseEvent mouseEvent);

    void handleAffineApply(ActionEvent event);

    void handleAffineReset(ActionEvent event);

    void handleRenderingModesCollapseToggle(MouseEvent mouseEvent);

    void handleWireframeToggle();

    void handleTexturesToggle();

    void handleLightingToggle();

    void handleSettingsCollapseToggle(MouseEvent mouseEvent);

    void handleSaveCamera(ActionEvent event);
}
