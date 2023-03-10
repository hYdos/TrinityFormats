package me.hydos.trinityutils.model.skeleton;

import org.joml.Vector3f;

/**
 * What the fuck.
 */
public class MayaTransformMatrix {
    public Vector3f x;
    public Vector3f y;
    public Vector3f z;
    public Vector3f w;

    public MayaTransformMatrix() {
        this.x = new Vector3f();
        this.y = new Vector3f();
        this.z = new Vector3f();
        this.w = new Vector3f();
    }

    public MayaTransformMatrix(Vector3f x, Vector3f y, Vector3f z, Vector3f w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public MayaTransformMatrix identity() {
        this.x = new Vector3f(1, 0, 0);
        this.y = new Vector3f(0, 1, 0);
        this.z = new Vector3f(0, 0, 1);
        this.w = new Vector3f(0, 0, 0);
        return this;
    }
}
