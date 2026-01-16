package com.apex.model.scene;

import com.apex.math.Matrix4x4;
import com.apex.math.Vector3f;

public class Camera {
    private Vector3f position;
    private Vector3f target;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;
    private float yaw = -90f;
    private float pitch = 0f;
    private float sensitivity = 0.1f;
    private Vector3f direction;
    private Vector3f upVector = new Vector3f(0, 1, 0);

    public Camera() {
    }

    public Camera(
            final Vector3f position,
            final Vector3f target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        updateCameraVectors();
        updateAnglesFromTarget();
    }

    public void setPosition(final Vector3f position) {
        this.position = position;
        updateTargetFromAngles();
    }

    public void setTarget(final Vector3f target) {
        this.target = target;
        updateAnglesFromTarget();
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getTarget() {
        return target;
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void movePosition(final Vector3f translation) {
        this.position.addLocal(translation);
        updateTargetFromAngles();
    }

    public void moveTarget(final Vector3f translation) {
        this.target.addLocal(translation);
        updateAnglesFromTarget();
    }

    public Matrix4x4 getViewMatrix() {
        return Matrix4x4.lookAt(position, target);
    }

    public Matrix4x4 getProjectionMatrix() {
        return Matrix4x4.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    public void rotate(float deltaX, float deltaY) {
        this.yaw += deltaX * sensitivity;
        this.pitch += deltaY * sensitivity;
        this.pitch = Math.max(-89f, Math.min(89f, this.pitch));
        updateTargetFromAngles();
    }

    public void rotateAroundTarget(float deltaX, float deltaY) {
        rotateAroundTarget(deltaX, deltaY, sensitivity);
    }

    public void rotateAroundTarget(float deltaX, float deltaY, float customSensitivity) {
        Vector3f cameraToTarget = position.subtract(target);

        float horizontalAngle = (-deltaX * customSensitivity * (float) Math.PI / 180.0f);

        float verticalAngle = (-deltaY * customSensitivity * (float) Math.PI / 180.0f);

        float distance = cameraToTarget.length();

        // сферические коорды
        float theta = (float) Math.atan2(cameraToTarget.getX(), cameraToTarget.getZ());
        float phi = (float) Math.atan2(Math.sqrt(cameraToTarget.getX() * cameraToTarget.getX() +
                        cameraToTarget.getZ() * cameraToTarget.getZ()),
                cameraToTarget.getY());

        theta += horizontalAngle;
        phi += verticalAngle;

        float epsilon = 0.01f;
        phi = Math.max(epsilon, Math.min((float) Math.PI - epsilon, phi));

        float x = distance * ((float) Math.sin(phi) * (float) Math.sin(theta));
        float y = distance * (float) Math.cos(phi);
        float z = distance * ((float) Math.sin(phi) * (float) Math.cos(theta));

        position = new Vector3f(x, y, z).addLocal(target);

        updateCameraVectors();
    }

    // панорамирование
    public void pan(float deltaX, float deltaY) {
        pan(deltaX, deltaY, sensitivity);
    }

    public void pan(float deltaX, float deltaY, float customSensitivity) {
        Vector3f right = direction.cross(upVector).normalize();

        Vector3f realUp = right.cross(direction).normalize();

        Vector3f translation = right.multiply(-deltaX * customSensitivity)
                .add(realUp.multiply(deltaY * customSensitivity));

        position = position.add(translation);
        target = target.add(translation);

        updateCameraVectors();
    }

    public void zoom(float delta) {
        zoom(delta, sensitivity);
    }

    public void zoom(float delta, float customSensitivity) {
        Vector3f toTarget = target.subtract(position);
        float distance = toTarget.length();

        float newDistance = Math.max(0.1f, distance - delta * customSensitivity);

        Vector3f newPosition = target.subtract(toTarget.normalize().multiply(newDistance));

        position = newPosition;

        updateCameraVectors();
    }

    public void moveForwardBackward(float amount) {
        Vector3f forward = direction.normalize().multiply(amount);
        position = position.add(forward);
        target = target.add(forward);
        updateCameraVectors();
    }

    public void moveRightLeft(float amount) {
        Vector3f right = direction.cross(upVector).normalize().multiply(amount);
        position = position.add(right);
        target = target.add(right);
        updateCameraVectors();
    }

    public void moveUpDown(float amount) {
        Vector3f up = upVector.multiply(amount);
        position = position.add(up);
        target = target.add(up);
        updateCameraVectors();
    }

    private void updateCameraVectors() {
        direction = target.subtract(position).normalize();
    }

    private void updateAnglesFromTarget() {
        Vector3f direction = target.subtract(position);
        this.pitch = (float) Math.toDegrees(Math.asin(direction.getY() / direction.length()));
        this.yaw = (float) Math.toDegrees(Math.atan2(direction.getZ(), direction.getX()));
        if (direction.getX() >= 0) yaw += 90;
        else yaw -= 90;
        updateCameraVectors();
    }

    private void updateTargetFromAngles() {
        Vector3f front = new Vector3f();
        front.setX((float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))));
        front.setY((float) Math.sin(Math.toRadians(pitch)));
        front.setZ((float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))));
        front.normalizeLocal();

        this.target = position.add(front);
        updateCameraVectors();
    }
}