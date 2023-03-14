package me.hydos.trinityutils.util;

import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.io.GltfModelReader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GenericModel {
    private static final GltfModelReader READER = new GltfModelReader();
    public final Skeleton skeleton;
    public final Map<String, Animation> animations;

    public GenericModel(Path path) {
        try {
            var gltfModel = READER.read(path);
            if (gltfModel.getSkinModels().size() > 0)
                this.skeleton = new Skeleton(gltfModel.getSkinModels().get(0));
            else this.skeleton = null;

            if (gltfModel.getSkinModels().size() > 0) {
                this.animations = new HashMap<>();

                for (var animation : gltfModel.getAnimationModels()) {
                    this.animations.put(animation.getName(), new Animation(animation));
                }
            } else this.animations = null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to open stream for " + path.getFileName().toString(), e);
        }
    }

    public static class Skeleton implements Iterable<Bone> {
        private final Bone[] bones;
        private final Map<String, Bone> nameMap;

        public Skeleton(SkinModel armature) {
            this.bones = new Bone[armature.getJoints().size()];
            this.nameMap = new HashMap<>();

            for (int i = 0; i < armature.getJoints().size(); i++) {
                var joint = armature.getJoints().get(i);
                var name = joint.getName();
                var bone = new Bone(i, name, joint.getTranslation(), joint.getRotation(), joint.getScale(), joint.getParent().getName());
                bones[i] = bone;
                nameMap.put(name, bone);
            }
        }

        public Bone getBone(String name) {
            return nameMap.get(name);
        }

        public boolean hasBone(String boneName) {
            return nameMap.containsKey(boneName);
        }

        @Override
        public Iterator<Bone> iterator() {
            return Arrays.stream(bones).iterator();
        }
    }

    public static final class Bone {
        private final int id;
        private final String name;
        private final Matrix4f offset;
        private final String parent;

        public Bone(int id, String name, float[] translation, float[] rotation, float[] scale, String parent) {
            this.id = id;
            this.name = name;
            this.parent = parent;
            this.offset = convert(translation, rotation, scale);
        }

        private static Matrix4f convert(float[] translation, float[] rotation, float[] scale) {
            var mat4 = new Matrix4f().identity();
            if (translation != null) mat4.translate(translation[0], translation[1], translation[2]);
            if (rotation != null) mat4.rotate(rotation[0], rotation[1], rotation[2], rotation[3]);
            if (scale != null) mat4.scale(scale[0], scale[1], scale[2]);

            return mat4;
        }

        public Vector3f getTransform() {
            return offset.getTranslation(new Vector3f());
        }

        public Vector3f getRotation() {
            return offset.getEulerAnglesXYZ(new Vector3f());
        }

        public Vector3f getRotationDegrees() {
            var rotRadians = getRotation();
            return new Vector3f(
                    (float) Math.toDegrees(rotRadians.x),
                    (float) Math.toDegrees(rotRadians.y),
                    (float) Math.toDegrees(rotRadians.z)
            );
        }

        public Vector3f getScale() {
            return offset.getScale(new Vector3f());
        }

        public String parent() {
            return parent;
        }

        public int id() {
            return id;
        }

        public String name() {
            return name;
        }
    }

    private static class Animation {

        public Animation(AnimationModel animation) {
            System.out.println("e");

            for (var channel : animation.getChannels()) {
                if (channel.getSampler().getInterpolation() != AnimationModel.Interpolation.LINEAR)
                    throw new RuntimeException("Linear Channels are supported only. TODO: Dynamic channel use other interpolation");

                switch (channel.getPath()) {
                    case "translation" -> {
                    }
                    case "rotation" -> {
                    }
                    case "scale" -> {
                    }
                    default -> throw new RuntimeException("Unknown channel type " + channel.getPath());
                }
            }
        }
    }
}
