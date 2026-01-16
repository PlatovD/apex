package com.apex.io.read;

import com.apex.io.util.IOProcessParams;
import com.apex.io.util.IOProcessor;
import com.apex.model.geometry.Model;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.tool.normals.NormalCalculator;
import com.apex.tool.normals.StaticNormalCalculator;
import com.apex.tool.triangulator.Triangulator;

@AutoCreation
public class ReadIOProcessor implements IOProcessor {
    @AutoInject
    private Triangulator triangulator;

    @AutoInject
    private NormalCalculator normalCalculator;

    @Override
    public void process(IOProcessParams params) {
        triangulator.triangulateModel(params.model());
//        normalCalculator.calculateNormals(params.model());
        StaticNormalCalculator.calculateVerticesNormals(params.model());
    }
}
