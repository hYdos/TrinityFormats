package me.hydos.pokefiletools.sv.json.shared;

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
    }

    public MayaTransformMatrix(Vector3f x, Vector3f y, Vector3f z, Vector3f w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
}
