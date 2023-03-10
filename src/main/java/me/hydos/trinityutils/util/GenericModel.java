package me.hydos.trinityutils.util;

import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.io.GltfModelReader;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class GenericModel {
    private static final GltfModelReader READER = new GltfModelReader();
    public final Skeleton skeleton;

    public GenericModel(Path path) {
        try {
            var gltfModel = READER.read(path);
            if (gltfModel.getSkinModels().size() > 0)
                this.skeleton = new Skeleton(gltfModel.getSkinModels().get(0));
            else this.skeleton = null;
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

        public Bone getBone(int id) {
            return bones[id];
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
        private final Vector3f translation;
        private final Vector3f rotDegrees;
        private final Vector3f rotRadians;
        private final Vector3f scale;
        private final String parent;

        public Bone(int id, String name, float[] translation, float[] rotation, float[] scale, String parent) {
            this.id = id;
            this.name = name;
            this.parent = parent;
            this.translation = readVec3f(translation, false);
            this.rotRadians = readQuaternion(rotation, false);
            this.rotDegrees = readQuaternion(rotation, true);
            this.scale = readVec3f(scale, true);
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

        public Vector3f translation() {
            return translation;
        }

        public Vector3f rotDegrees() {
            return rotDegrees;
        }

        public Vector3f rotRadians() {
            return rotRadians;
        }

        public Vector3f scale() {
            return scale;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Bone) obj;
            return this.id == that.id &&
                    Objects.equals(this.name, that.name) &&
                    Objects.equals(this.translation, that.translation) &&
                    Objects.equals(this.rotDegrees, that.rotDegrees) &&
                    Objects.equals(this.rotRadians, that.rotRadians) &&
                    Objects.equals(this.scale, that.scale);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, translation, rotDegrees, rotRadians, scale);
        }

        @Override
        public String toString() {
            return "Bone[" +
                    "id=" + id + ", " +
                    "name=" + name + ", " +
                    "translation=" + translation + ", " +
                    "rotDegrees=" + rotDegrees + ", " +
                    "rotRadians=" + rotRadians + ", " +
                    "scale=" + scale + ']';
        }

        private static Vector3f readQuaternion(float[] arr, boolean convertToDegrees) {
            var quaternion = arr == null ? new Quaternionf() : new Quaternionf(arr[0], arr[1], arr[2], arr[3]);
            var result = quaternion.getEulerAnglesXYZ(new Vector3f());

            if (convertToDegrees) {
                result.x = (float) Math.toDegrees(result.x);
                result.y = (float) Math.toDegrees(result.y);
                result.z = (float) Math.toDegrees(result.z);
            }

            return result;
        }

        private static Vector3f readVec3f(float[] arr, boolean defaultOnes) {
            return arr == null ? (defaultOnes ? new Vector3f(1, 1, 1) : new Vector3f()) : new Vector3f(arr[0], arr[1], arr[2]);
        }
    }
}
