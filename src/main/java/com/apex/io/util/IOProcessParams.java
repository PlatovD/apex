package com.apex.io.util;

import com.apex.math.Matrix4x4;
import com.apex.model.geometry.Model;

public record IOProcessParams(Model model, IOType type, Boolean saveModified, Matrix4x4 worldMatrix) {
    public enum IOType {
        INPUT,
        OUTPUT
    }
}
