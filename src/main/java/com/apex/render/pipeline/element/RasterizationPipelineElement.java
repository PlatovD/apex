package com.apex.render.pipeline.element;

import com.apex.buffer.RasterizationBuffer;
import com.apex.core.Constants;
import com.apex.math.Vector2f;
import com.apex.math.Vector3f;
import com.apex.model.scene.RenderObject;
import com.apex.model.scene.ZBuffer;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.exception.RasterizationException;
import com.apex.model.geometry.Model;
import com.apex.model.geometry.Polygon;
import com.apex.tool.colorization.ColorData;
import com.apex.tool.rasterization.Rasterization;
import com.apex.tool.rasterization.VertexAttribute;
import com.apex.util.ActiveCameraWrapper;

import java.util.List;

@AutoCreation
public class RasterizationPipelineElement implements PipelineElement {
    @AutoInject
    private RasterizationBuffer rb;

    @AutoInject
    private ZBuffer zBuffer;

    @AutoInject
    private ActiveCameraWrapper activeCameraWrapper;

    @Override
    public void apply(RenderObject ro) {
        VertexAttribute vertex0Attribute = new VertexAttribute();
        VertexAttribute vertex1Attribute = new VertexAttribute();
        VertexAttribute vertex2Attribute = new VertexAttribute();

        Vector2f textureVertex0;
        Vector2f textureVertex1;
        Vector2f textureVertex2;

        Vector3f normalVertex0;
        Vector3f normalVertex1;
        Vector3f normalVertex2;

        // инициализирую то, что будет переиспользоваться. Уменьшаю нагрузку на сборщик мусора
        ColorData colorData = ro.getColorData();
        double[] barycentric = new double[3];

        List<Integer> textureIndices;
        List<Integer> normalIndices;

        Model model = ro.getModel();
        float[] rawVertices = ro.getWorkVertices();
        Vector3f light = activeCameraWrapper.getActiveCamera().getTarget().subtract(activeCameraWrapper.getActiveCamera().getPosition());
        light.normalizeLocal();
        for (Polygon polygon : ro.getModel().polygons) {
            if (polygon.getVertexIndices().size() != 3)
                throw new RasterizationException("One of polygons is not triangle");

            int vertex1Index = polygon.getVertexIndices().get(0);
            int vertex2Index = polygon.getVertexIndices().get(1);
            int vertex3Index = polygon.getVertexIndices().get(2);

            textureIndices = polygon.getTextureVertexIndices();

            // обновил координаты
            {
                refreshVertexAttributeForPolygon(vertex0Attribute, vertex1Index, rawVertices);
                refreshVertexAttributeForPolygon(vertex1Attribute, vertex2Index, rawVertices);
                refreshVertexAttributeForPolygon(vertex2Attribute, vertex3Index, rawVertices);
            }

            if (!isOnScreen(vertex0Attribute, vertex1Attribute, vertex2Attribute, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT))
                continue;

            // надо передавать текстурные вершины и смотреть, чтобы они были
            if (ro.isTextured()) {
                textureVertex0 = model.textureVertices.get(textureIndices.get(0));
                textureVertex1 = model.textureVertices.get(textureIndices.get(1));
                textureVertex2 = model.textureVertices.get(textureIndices.get(2));

                vertex0Attribute.u = textureVertex0.getX();
                vertex0Attribute.v = textureVertex0.getY();

                vertex1Attribute.u = textureVertex1.getX();
                vertex1Attribute.v = textureVertex1.getY();

                vertex2Attribute.u = textureVertex2.getX();
                vertex2Attribute.v = textureVertex2.getY();
                refreshVertexPerspectiveCorrection(vertex0Attribute);
                refreshVertexPerspectiveCorrection(vertex1Attribute);
                refreshVertexPerspectiveCorrection(vertex2Attribute);
            }

            // здесь прикрепляю нормали
            {
                normalIndices = polygon.getNormalIndices();

                normalVertex0 = model.normals.get(normalIndices.get(0));
                normalVertex1 = model.normals.get(normalIndices.get(1));
                normalVertex2 = model.normals.get(normalIndices.get(2));

                vertex0Attribute.n_x = normalVertex0.getX();
                vertex0Attribute.n_y = normalVertex0.getY();
                vertex0Attribute.n_z = normalVertex0.getZ();

                vertex1Attribute.n_x = normalVertex1.getX();
                vertex1Attribute.n_y = normalVertex1.getY();
                vertex1Attribute.n_z = normalVertex1.getZ();

                vertex2Attribute.n_x = normalVertex2.getX();
                vertex2Attribute.n_y = normalVertex2.getY();
                vertex2Attribute.n_z = normalVertex2.getZ();
            }

            Rasterization.drawTriangle(rb, zBuffer,
                    light, colorData, ro.getColorProvider(), ro.getTexture(),
                    vertex0Attribute, vertex1Attribute, vertex2Attribute,
                    barycentric
            );
        }
    }

    private void refreshVertexAttributeForPolygon(VertexAttribute vertexAttribute, int vertexIndex, float[] rawVertices) {
        vertexAttribute.x = Math.round(rawVertices[vertexIndex * 4]);
        vertexAttribute.y = Math.round(rawVertices[vertexIndex * 4 + 1]);
        vertexAttribute.z = rawVertices[vertexIndex * 4 + 2];
        vertexAttribute.invW = rawVertices[vertexIndex * 4 + 3] == 0 ? 1 / Constants.EPS : 1 / rawVertices[vertexIndex * 4 + 3];
    }

    private void refreshVertexPerspectiveCorrection(VertexAttribute vertexAttribute) {
        vertexAttribute.uOverW = vertexAttribute.u * vertexAttribute.invW;
        vertexAttribute.vOwerW = vertexAttribute.v * vertexAttribute.invW;
    }

    private boolean isOnScreen(VertexAttribute v0, VertexAttribute v1, VertexAttribute v2, int screenWidth, int screenHeight) {
        boolean lefter = v0.x < 0 && v1.x < 0 && v2.x < 0;
        boolean righter = v0.x >= screenWidth && v1.x >= screenWidth && v2.x >= screenWidth;
        boolean toper = v0.y < 0 && v1.y < 0 && v2.y < 0;
        boolean downer = v0.y >= screenHeight && v1.y >= screenHeight && v2.y >= screenHeight;
        return !(lefter || righter || toper || downer);
    }

    @Override
    public void prepare() {
        zBuffer.clear();
        rb.clear();
    }
}
