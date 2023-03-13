package me.hydos.trinityutils.format.tr2022.animation.track;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class Framed16RotationTrack implements AnimationTrack {
    public List<Integer> frames;
    public List<Vector3f> co;

    public List<Quaternionf> getRotations() {
        return null;
    }
}
