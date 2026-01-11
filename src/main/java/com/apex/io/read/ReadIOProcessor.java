package com.apex.io.read;

import com.apex.io.util.IOProcessor;
import com.apex.model.geometry.Model;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.tool.normals.NormalCalculator;
import com.apex.tool.triangulator.Triangulator;

@AutoCreation
public class ReadIOProcessor implements IOProcessor {
    @AutoInject
    private Triangulator triangulator;

    @Override
    public void process(Model model) {
        triangulator.triangulateModel(model);
        NormalCalculator.calculateVerticesNormals(model);
    }
}
