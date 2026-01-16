package com.apex.io.write;

import com.apex.io.util.IOProcessParams;
import com.apex.io.util.IOProcessor;
import com.apex.math.Matrix4x4;
import com.apex.math.Vector3f;
import com.apex.math.Vector4f;
import com.apex.model.geometry.Model;
import com.apex.reflection.AutoCreation;

@AutoCreation
public class WriteIOProcessor implements IOProcessor {
    @Override
    public void process(IOProcessParams params) {
        Model model = params.model();
        if (!params.saveModified()) return;

        Matrix4x4 matrixWorld = params.worldMatrix();
        Vector4f v4Reusable = new Vector4f();
        for (int i = 0; i < model.vertices.size(); i++) {
            Vector3f v = model.vertices.get(i);
            v4Reusable.setX(v.getX());
            v4Reusable.setY(v.getY());
            v4Reusable.setZ(v.getZ());
            v4Reusable.setW(1);
            Vector4f res = matrixWorld.multiply(v4Reusable);
            model.vertices.set(i, res.toVector3());
        }
    }
}
