package me.hydos.pokefiletools.sv;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.hydos.pokefiletools.sv.json.shared.MayaTransformMatrix;
import me.hydos.pokefiletools.sv.json.trskl.TrinitySkeleton;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.assimp.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SkeletonConverterAssimp {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Path SCHEMA = Paths.get("D:\\Projects\\hYdos\\PokeFileTools\\src\\main\\resources\\trskl.fbs");

    public static void main(String[] args) throws IOException {
        var scene = Assimp.aiImportFile(
                "D:\\Projects\\hYdos\\PokeFileTools\\src\\test\\resources\\pm0133.glb",
                Assimp.aiProcess_Triangulate | Assimp.aiProcess_PopulateArmatureData
        );

        Matrix4f daeMat4 = new Matrix4f(
                new Vector4f(1.000e0f, 1.490e-7f, -2.608e-8f, 3.496e-2f),
                new Vector4f(-1.490e-7f, 1.000e0f, -1.222e-8f, -2.235e-8f),
                new Vector4f(2.608e-8f, 1.222e-8f, 1.000e0f, 8.368e-11f),
                new Vector4f(0.000e0f, 0.000e0f, 0.000e0f, 1.000e0f)
        );
        System.out.println("e");


        var nodeParents = new HashMap<String, String>();
        var nodeOffsets = new HashMap<String, Matrix4f>();
        var boneIds = new HashMap<String, Integer>();
        fillInMaps(scene.mRootNode(), null, nodeParents, nodeOffsets);

        var skeleton = new TrinitySkeleton();

        // Calculate Bone ID's
        for (int i = 0; i < scene.mNumMeshes(); i++) {
            var mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(i));
            for (int j = 0; j < mesh.mNumBones(); j++) {
                var bone = AIBone.create(Objects.requireNonNull(mesh.mBones()).get(j));

                if (boneIds.entrySet().stream().noneMatch(entry -> entry.getKey().equals(bone.mName().dataString())))
                    boneIds.put(bone.mName().dataString(), boneIds.size());
            }
        }

        for (int i = 0; i < scene.mNumMeshes(); i++) {
            var mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(i));
            for (int j = 0; j < mesh.mNumBones(); j++) {
                var mayaTransformMatrix = new Matrix4x3f(); // Remember: this is meant to be a matrix 4x3
                var bone = AIBone.create(Objects.requireNonNull(mesh.mBones()).get(j));

                if (skeleton.transform_nodes.stream().noneMatch(transformNode -> transformNode.name.equals(bone.mName().dataString()))) {
                    var name = bone.mName().dataString();
                    mayaTransformMatrix.invert();

                    var boneOffset = from(bone.mNode().mTransformation());
                    var trinityWhy = doMayaMatrixStuff(bone.mOffsetMatrix());
                    var nodeIdx = nodeParents.get(name) == null ? -1 : boneIds.get(nodeParents.get(name));

                    if (nodeIdx != -1)
                        skeleton.bones.add(new TrinitySkeleton.Bone(1, 1, new MayaTransformMatrix(
                                new Vector3f(1, 0, 0),
                                new Vector3f(0, 1, 0),
                                new Vector3f(0, 0, 1),
                                new Vector3f(0, 0, 0)
                        )));

                    var node = new TrinitySkeleton.TransformNode(
                            name,
                            new TrinitySkeleton.TransformNode.Transform(
                                    boneOffset.getScale(new Vector3f()),
                                    boneOffset.getEulerAnglesXYZ(new Vector3f()),
                                    boneOffset.getTranslation(new Vector3f())
                            ),
                            new Vector3f(),
                            new Vector3f(),
                            nodeIdx == -1 && skeleton.transform_nodes.size() > 0 ? 0 : nodeIdx,
                            skeleton.bones.size() - 1,
                            "",
                            "Default"
                    );

                    skeleton.transform_nodes.add(node);
                }
            }
        }

        System.out.println("validate");

        var output = Paths.get("output.json");
        var outputTrskl = Paths.get("D:\\Projects\\hYdos\\PokeFileTools\\output.trskl");
        var modTrskl = Paths.get("C:/Users/hydos/AppData/Roaming/yuzu/load/0100A3D008C5C000/testMod/romfs/pokemon/data/pm0133/pm0133_00_00/pm0133_00_00.trskl");
        Files.writeString(output, GSON.toJson(skeleton));
        FlatCWrapper.convertToBinary(SCHEMA, output);
        Files.deleteIfExists(modTrskl);
        Files.copy(outputTrskl, modTrskl);
    }

    private static MayaTransformMatrix doMayaMatrixStuff(AIMatrix4x4 mOffsetMatrix) {
        var normalTransform = from(mOffsetMatrix);
        var translation = normalTransform.getTranslation(new Vector3f());
        var rotations = normalTransform.getEulerAnglesXYZ(new Vector3f());
        var scale = normalTransform.getScale(new Vector3f());

        var m = getMayaScale(scale)
                .mul(getMayaRot(rotations))
                .mul(getMayaTrans(translation));
        m.invert();

        return new MayaTransformMatrix(
                new Vector3f(m.m00(), m.m01(), m.m01()),
                new Vector3f(m.m10(), m.m11(), m.m11()),
                new Vector3f(m.m20(), m.m21(), m.m21()),
                new Vector3f(m.m30(), m.m31(), m.m31())
        );
    }

    private static Matrix4f getMayaTrans(Vector3f trans) {
        return new Matrix4f(
                new Vector4f(1, 0, 0, 0),
                new Vector4f(0, 1, 0, 0),
                new Vector4f(0, 0, 1, 0),
                new Vector4f(trans.x(), trans.y(), trans.z(), 1)
        );
    }

    private static Matrix4f getMayaRot(Vector3f rot) {
        var rx = new Matrix4f(
                new Vector4f(1, 0, 0, 0),
                new Vector4f(0, (float) Math.cos(rot.x()), (float) Math.sin(rot.x()), 0),
                new Vector4f(0, -(float) Math.sin(rot.x()), (float) Math.cos(rot.x()), 0),
                new Vector4f(0, 0, 0, 1)
        );

        var ry = new Matrix4f(
                new Vector4f((float) Math.cos(rot.y()), 0, (float) -Math.sin(rot.y()), 0),
                new Vector4f(0, 1, 0, 0),
                new Vector4f((float) Math.sin(rot.y()), 0, (float) Math.cos(rot.y()), 0),
                new Vector4f(0, 0, 0, 1)
        );

        var rz = new Matrix4f(
                new Vector4f((float) Math.cos(rot.z()), (float) Math.sin(rot.z()), 0, 0),
                new Vector4f((float) -Math.sin(rot.z()), (float) Math.cos(rot.z()), 0, 0),
                new Vector4f(0, 0, 1, 0),
                new Vector4f(0, 0, 0, 1)
        );

        return rx.mul(ry).mul(rz);
    }

    private static Matrix4f getMayaScale(Vector3f scale) {
        return new Matrix4f().scale(scale);
    }

    public static Matrix4f from(AIMatrix4x4 aiMat4) {
        return new Matrix4f()
                .m00(aiMat4.a1())
                .m10(aiMat4.a2())
                .m20(aiMat4.a3())
                .m30(aiMat4.a4())
                .m01(aiMat4.b1())
                .m11(aiMat4.b2())
                .m21(aiMat4.b3())
                .m31(aiMat4.b4())
                .m02(aiMat4.c1())
                .m12(aiMat4.c2())
                .m22(aiMat4.c3())
                .m32(aiMat4.c4())
                .m03(aiMat4.d1())
                .m13(aiMat4.d2())
                .m23(aiMat4.d3())
                .m33(aiMat4.d4());
    }

    private static void fillInMaps(AINode current, AINode parent, HashMap<String, String> parentNodeMap, HashMap<String, Matrix4f> nodeOffsets) {
        for (int i = 0; i < current.mNumChildren(); i++)
            fillInMaps(AINode.create(current.mChildren().get(i)), current, parentNodeMap, nodeOffsets);

        nodeOffsets.put(current.mName().dataString(), from(current.mTransformation()));

        if (parent != null && !parent.mName().dataString().contains("trmdl"))
            parentNodeMap.put(current.mName().dataString(), parent.mName().dataString());
    }
}
