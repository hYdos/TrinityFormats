package me.hydos.trinityutils.format.tr2022.skeleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import me.hydos.trinityutils.launch.GuiApplication;
import me.hydos.trinityutils.util.FlatCWrapper;
import me.hydos.trinityutils.util.GenericModel;
import org.joml.Vector3f;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class TrinitySkeleton {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeSpecialFloatingPointValues()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();
    @SerializedName("res_0")
    public int resource0;
    @SerializedName("transform_nodes")
    public ArrayList<TransformNode> transformNodes;
    public ArrayList<Bone> bones;
    @SerializedName("iks")
    public ArrayList<InverseKinematic> inverseKinematics;
    @SerializedName("rig_offset")
    public int rigOffset;
    private boolean inDegrees;

    public TrinitySkeleton() {
        this.bones = new ArrayList<>();
        this.transformNodes = new ArrayList<>();
        this.inverseKinematics = new ArrayList<>();
        this.inDegrees = false;
    }

    // TODO: investigate if rotation and translation are both all zeros, Then rig_idx should be -1
    public TrinitySkeleton(GenericModel.Skeleton skeleton, boolean useDegrees) {
        this();
        this.inDegrees = useDegrees;

        for (var bone : skeleton) {
            var nodeIdx = !skeleton.hasBone(bone.parent()) ? -1 : skeleton.getBone(bone.parent()).id();
            if (nodeIdx != -1) bones.add(new TrinitySkeleton.Bone());

            var node = new TrinitySkeleton.TransformNode(
                    bone.name(),
                    new TrinitySkeleton.TransformNode.Transform(
                            bone.getScale(),
                            inDegrees ? bone.getRotationDegrees() : bone.getRotation(),
                            bone.getTransform()
                    ),
                    new Vector3f(),
                    new Vector3f(),
                    nodeIdx == -1 && transformNodes.size() > 0 ? 0 : nodeIdx,
                    bones.size() - 1,
                    "",
                    "Default"
            );

            transformNodes.add(node);
        }
    }

    public static TrinitySkeleton read(Path path) {
        var rawFileName = path.getFileName().toString();
        var fileExtension = rawFileName.substring(rawFileName.lastIndexOf(".") + 1);

        return switch (fileExtension) {
            case "json" -> readJson(path);
            case "trskl", "bin" -> throw new RuntimeException("Not Implemented");
            default -> throw new RuntimeException("Unknown format " + fileExtension);
        };
    }

    public static TrinitySkeleton readJson(Path path) {
        try {
            return GSON.fromJson(Files.readString(path), TrinitySkeleton.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed reading TrinitySkeleton json", e);
        }
    }

    public void convertToRadians() {
        if (!inDegrees) throw new RuntimeException("Cannot go from radians to radians");

        for (var transformNode : transformNodes) {
            var degreesRot = transformNode.transform.rotation;
            transformNode.transform.rotation = new Vector3f(
                    (float) Math.toRadians(degreesRot.x()),
                    (float) Math.toRadians(degreesRot.y()),
                    (float) Math.toRadians(degreesRot.z())
            );
        }

        inDegrees = false;
    }

    public void convertToDegrees() {
        if (inDegrees) throw new RuntimeException("Cannot go from degrees to degrees");

        for (var transformNode : transformNodes) {
            var radiansRot = transformNode.transform.rotation;
            transformNode.transform.rotation = new Vector3f(
                    (float) Math.toDegrees(radiansRot.x()),
                    (float) Math.toDegrees(radiansRot.y()),
                    (float) Math.toDegrees(radiansRot.z())
            );
        }

        inDegrees = true;
    }

    public void exportToJson(Path path) {
        try {
            Files.writeString(path, GSON.toJson(this));
        } catch (IOException e) {
            throw new RuntimeException("Failed exporting TrinitySkeleton", e);
        }
    }

    public void exportToBinary(Path path) {
        try {
            var schemaLocation = GuiApplication.TRSKL_SCHEMA;
            if (!Files.readString(schemaLocation).contains("file_extension"))
                throw new RuntimeException("Please add a file extension to your flatbuffer schema");
            var workingDir = Files.createTempDirectory("genBinFlatC");
            var json = workingDir.resolve("json.json");
            Files.writeString(json, GSON.toJson(this));
            FlatCWrapper.convertToBinary(workingDir, schemaLocation, json);
            Files.deleteIfExists(path);
            var schemaName = schemaLocation.getFileName().toString();
            Files.copy(workingDir.resolve("json." + schemaName.substring(0, schemaName.indexOf("."))), path);
        } catch (IOException e) {
            throw new RuntimeException("Failed exporting TrinitySkeleton", e);
        }
    }

    public boolean isInDegrees() {
        return inDegrees;
    }

    public static class TransformNode {
        public String name;
        public Transform transform;
        public Vector3f scalePivot;
        public Vector3f rotatePivot;
        @SerializedName("parent_idx")
        public int parentIndex;
        @SerializedName("rig_idx")
        public int rigIndex;
        @SerializedName("effect_node")
        public String effectNode;
        public String type;

        public static class Transform {
            @SerializedName("VecScale")
            public Vector3f scale;
            @SerializedName("VecRot")
            public Vector3f rotation;
            @SerializedName("VecTranslate")
            public Vector3f translate;

            public Transform() {
            }

            public Transform(Vector3f scale, Vector3f rotation, Vector3f translate) {
                this.scale = scale;
                this.rotation = rotation;
                this.translate = translate;
            }

            @Override
            public String toString() {
                return "Transform{" +
                       ", translation=" + translate +
                       ", rotation=" + rotation +
                       "scale=" + scale +
                       '}';
            }
        }

        public TransformNode() {
        }

        public TransformNode(String name, Transform transform, Vector3f scalePivot, Vector3f rotatePivot, int parentIndex, int rigIndex, String effectNode, String type) {
            this.name = name;
            this.transform = transform;
            this.scalePivot = scalePivot;
            this.rotatePivot = rotatePivot;
            this.parentIndex = parentIndex;
            this.rigIndex = rigIndex;
            this.effectNode = effectNode;
            this.type = type;
        }

        @Override
        public String toString() {
            return "TransformNode{" +
                   "name='" + name + '\'' +
                   ", transform=" + transform +
                   ", parent_idx=" + parentIndex +
                   ", rig_idx=" + rigIndex +
                   '}';
        }
    }

    public static class Bone {
        @SerializedName("inherit_position")
        public int inheritPosition;
        @SerializedName("unk_bool_2")
        public int unknown;
        public MayaTransformMatrix matrix;

        public Bone() {
            this.inheritPosition = 1;
            this.unknown = 1;
            this.matrix = new MayaTransformMatrix().identity();
        }

        public Bone(int inheritPosition, MayaTransformMatrix matrix) {
            this.inheritPosition = inheritPosition;
            this.unknown = 1;
            this.matrix = matrix;
        }
    }

    public static class InverseKinematic {
    }
}
