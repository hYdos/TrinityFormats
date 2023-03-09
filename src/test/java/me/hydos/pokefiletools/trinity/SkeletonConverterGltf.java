package me.hydos.pokefiletools.trinity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.io.GltfModelReader;
import me.hydos.pokefiletools.sv.FlatCWrapper;
import me.hydos.pokefiletools.sv.json.shared.MayaTransformMatrix;
import me.hydos.pokefiletools.sv.json.trskl.TrinitySkeleton;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class SkeletonConverterGltf {
    private static final GltfModelReader READER = new GltfModelReader();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path SCHEMA = Paths.get("D:\\Projects\\hYdos\\PokeFileTools\\src\\main\\resources\\trskl.fbs");

    public static void main(String[] args) throws IOException {
        var gltfSkeleton = read(SkeletonConverterGltf.class.getResourceAsStream("/pm0133.glb")).getSkinModels().get(0);

        // convert to degrees for easier debugging
        var original = GSON.fromJson(Files.readString(Paths.get("D:/Projects/hYdos/PokeFileTools/pm0133_00_00.json")), TrinitySkeleton.class);
        for (var transformNode : original.transform_nodes) {
            transformNode.transform.VecRot = fixRotations(transformNode.transform.VecRot);
        }
        Files.writeString(Paths.get("D:/Projects/hYdos/PokeFileTools/pm0133_00_00.degrees.json"), GSON.toJson(original));

        var boneMap = new HashMap<String, TrinityBone>();
        var boneIds = new HashMap<String, Integer>();
        var boneParents = new HashMap<String, String>();

        // First Pass. Create Bones and populate Bone Map
        for (int i = 0; i < gltfSkeleton.getJoints().size(); i++) {
            var joint = gltfSkeleton.getJoints().get(i);
            var name = joint.getName();
            boneMap.put(name, new TrinityBone(joint));
            boneIds.put(name, i);
        }

        // Second Pass. Assign Parents
        for (int i = 0; i < gltfSkeleton.getJoints().size(); i++) {
            var joint = gltfSkeleton.getJoints().get(i);
            var name = joint.getName();
            var parentName = joint.getParent().getName();
            boneParents.put(name, parentName);
        }

        var outSkeleton = new TrinitySkeleton();
        // Third Pass. Actually Create the Skeleton
        for (int i = 0; i < gltfSkeleton.getJoints().size(); i++) {
            var name = gltfSkeleton.getJoints().get(i).getName();
            var bone = boneMap.get(name);
            var nodeIdx = boneIds.get(boneParents.get(name)) == null || gltfSkeleton.getJoints().get(i).getMeshModels().size() > 0 ? -1 : boneIds.get(boneParents.get(name));

            if (nodeIdx != -1)
                outSkeleton.bones.add(new TrinitySkeleton.Bone(1, 1, new MayaTransformMatrix(
                        new Vector3f(1, 0, 0),
                        new Vector3f(0, 1, 0),
                        new Vector3f(0, 0, 1),
                        new Vector3f(0, 0, 0)
                )));

            var node = new TrinitySkeleton.TransformNode(
                    name,
                    new TrinitySkeleton.TransformNode.Transform(
                            bone.scale,
                            fixRotations(bone.eulerRotation),
                            bone.position
                    ),
                    new Vector3f(),
                    new Vector3f(),
                    nodeIdx == -1 && outSkeleton.transform_nodes.size() > 0 ? 0 : nodeIdx,
                    outSkeleton.bones.size() - 1,
                    "",
                    "Default"
            );

            outSkeleton.transform_nodes.add(node);
        }

        var output = Paths.get("output.json");
        var outputTrskl = Paths.get("D:\\Projects\\hYdos\\PokeFileTools\\output.trskl");
        var modTrskl = Paths.get("C:/Users/hydos/AppData/Roaming/yuzu/load/0100A3D008C5C000/testMod/romfs/pokemon/data/pm0133/pm0133_00_00/pm0133_00_00.trskl");
        Files.writeString(output, GSON.toJson(outSkeleton));
        FlatCWrapper.convertToBinary(SCHEMA, output);
        Files.deleteIfExists(modTrskl);
        Files.copy(outputTrskl, modTrskl);
    }

    private static Vector3f fixRotations(Vector3f degrees) {
        var halfTurn = (float) Math.toRadians(180);
        return new Vector3f(
                (float) Math.toRadians(Math.round(degrees.x)),
                (float) Math.toRadians(Math.round(degrees.y)),
                (float) Math.toRadians(Math.round(degrees.z))
        );
    }

    private static GltfModel read(InputStream is) throws IOException {
        return READER.readWithoutReferences(Objects.requireNonNull(is));
    }

    private static class TrinityBone {

        public final String name;
        public final Vector3f position;
        public final Vector3f eulerRotation;
        public final Vector3f scale;

        public TrinityBone(NodeModel joint) {
            this.name = joint.getName();
            this.position = joint.getTranslation() == null ? new Vector3f() : readVec3(joint.getTranslation());
            this.eulerRotation = joint.getRotation() == null ? new Vector3f() : readRot(joint.getRotation());
            this.scale = joint.getScale() == null ? new Vector3f(1, 1, 1) : readVec3(joint.getScale());
        }

        private Vector3f readRot(float[] rotation) {
            // Do Trinity's Expected Rounding
            var format = new DecimalFormat("#");
            format.setMaximumFractionDigits(6);

            if (rotation.length == 4) {
                var quaternion = new Quaternionf(rotation[0], rotation[1], rotation[2], rotation[3]);
                var rawAngles = quaternion.getEulerAnglesXYZ(new Vector3f());
                var x = (float) Math.toDegrees(Float.parseFloat(format.format(rawAngles.x)));
                var y = (float) Math.toDegrees(Float.parseFloat(format.format(rawAngles.y)));
                var z = (float) Math.toDegrees(Float.parseFloat(format.format(rawAngles.z)));
                return new Vector3f(x, y, z);
            } else throw new RuntimeException();
        }

        private Vector3f readVec3(float[] translation) {
            // Do Trinity's Expected Rounding
            var format = new DecimalFormat("#");
            format.setMaximumFractionDigits(6);

            var x = Float.parseFloat(format.format(translation[0]));
            var y = Float.parseFloat(format.format(translation[1]));
            var z = Float.parseFloat(format.format(translation[2]));

            return new Vector3f(x, y, z);
        }
    }
}
