package me.hydos.trinityutils.launch;

import me.hydos.trinityutils.format.TrinitySchemas;
import me.hydos.trinityutils.format.tr2022.skeleton.TrinitySkeleton;
import me.hydos.trinityutils.util.GenericModel;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkeletonDiffer {

    public static void main(String[] args) {
        var model = new GenericModel(Paths.get("D:/Projects/hYdos/PokeFileTools/src/test/resources/pm0133.glb"));
        var correctSkeleton = TrinitySchemas.latest().getSkeletonSchema().read(Paths.get("D:/Projects/hYdos/PokeFileTools/pm0133_00_00.json"));
        var skeleton = new TrinitySkeleton(model.skeleton, true);
        correctSkeleton.convertToDegrees();

        // Blender fixes
//        for (var node : skeleton.transformNodes) {
//            if(node.name.equals("waist")) {
//                node.transform.rotation.add(0, -90, 90);
//            }
//        }

        // Compare the Skeletons
        System.out.println("======Diff (Transform Node Count)======");
        System.out.println("GameFreaks: " + correctSkeleton.transformNodes.size());
        System.out.println("Ours: " + skeleton.transformNodes.size());

        System.out.println("======Diff (Rig Bone Count)======");
        System.out.println("GameFreaks: " + correctSkeleton.bones.size());
        System.out.println("Ours: " + skeleton.bones.size());

        // Slow but it doesn't matter for us.
        var correctToOursMap = new HashMap<TrinitySkeleton.TransformNode, TrinitySkeleton.TransformNode>();
        for (var correctNode : correctSkeleton.transformNodes) {
            for (var transformNode : skeleton.transformNodes) {
                if (correctNode.name.equals(transformNode.name)) {
                    correctToOursMap.put(correctNode, transformNode);
                }
            }
        }

        System.out.println("======Diff (Transform Node Deep Diff. Ignoring Extra Bones)======");
        var incorrectNodes = new ArrayList<TrinitySkeleton.TransformNode>();
        for (var entry : correctToOursMap.entrySet()) {
            var correct = entry.getKey();
            var ours = entry.getValue();

            if (skeleton.isInDegrees()) {
                if (Math.round(correct.transform.rotation.x) != Math.round(ours.transform.rotation.x) &&
                    Math.round(correct.transform.rotation.y) != Math.round(ours.transform.rotation.y) &&
                    Math.round(correct.transform.rotation.z) != Math.round(ours.transform.rotation.z)
                ) {
                    incorrectNodes.add(ours);
                    System.out.println("/nINCORRECT TRANSFORM: " + correct.name);
                    System.out.println("/nBone Type: " + correct.type);
                    System.out.println("Correct Rotations");
                    System.out.println("X: " + Math.round(correct.transform.rotation.x));
                    System.out.println("Y: " + Math.round(correct.transform.rotation.y));
                    System.out.println("Z: " + Math.round(correct.transform.rotation.z));
                    System.out.println("Our Rotations");
                    System.out.println("X: " + Math.round(ours.transform.rotation.x));
                    System.out.println("Y: " + Math.round(ours.transform.rotation.y));
                    System.out.println("Z: " + Math.round(ours.transform.rotation.z));
                }
            }
        }

        System.out.println("Incorrect Transforms: (" + incorrectNodes.size() + "/" + skeleton.transformNodes.size() + ")");

        System.out.println("Exporting Anyway");
        var path = "C:/Users/hydos/AppData/Roaming/yuzu/load/0100A3D008C5C000/testMod/romfs/pokemon/data/pm0133/pm0133_00_00/pm0133_00_00.trskl";
        skeleton.convertToRadians();
        skeleton.exportToBinary(Paths.get(path));
    }
}
