package com.apex.gui.element;

import com.apex.model.scene.RenderObject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ModelSaveRowGUI extends HBox {
    private final CheckBox selectCheck;
    private final Label nameLabel;
    private final CheckBox withTransformsCheck;
    private final RenderObject renderObject;

    public ModelSaveRowGUI(RenderObject renderObject) {
        super(10);
        this.renderObject = renderObject;

        this.setPadding(new Insets(5));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefWidth(Double.MAX_VALUE);

        this.selectCheck = new CheckBox();
        this.selectCheck.setSelected(true);

        this.nameLabel = new Label(renderObject.getFilename());

        this.withTransformsCheck = new CheckBox("With transformations");
        this.withTransformsCheck.setSelected(true);

        this.getChildren().addAll(
                selectCheck,
                nameLabel,
                withTransformsCheck
        );

        this.getStyleClass().add("model-save-row");
        nameLabel.getStyleClass().add("model-name-label");
        selectCheck.getStyleClass().add("model-save-checkbox");
        withTransformsCheck.getStyleClass().add("transform-checkbox");
    }

    public boolean isSaveModified() {
        return withTransformsCheck.isSelected();
    }

    public String getModelName() {
        return nameLabel.getText();
    }

    public boolean isSelected() {
        return selectCheck.isSelected();
    }

    public RenderObject getRenderObject() {
        return renderObject;
    }

    public void setSelected(boolean selected) {
        selectCheck.setSelected(selected);
    }

    public void setSaveModified(boolean saveModified) {
        withTransformsCheck.setSelected(saveModified);
    }
}

