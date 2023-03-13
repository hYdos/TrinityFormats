package me.hydos.trinityutils.launch;

import me.hydos.trinityutils.format.TrinitySchemas;
import me.hydos.trinityutils.format.tr2022.skeleton.TrinitySkeleton;
import me.hydos.trinityutils.util.GenericModel;

import java.nio.file.Paths;
import java.util.HashMap;

public class SkeletonDiffer {

    public static void main(String[] args) {
        var model = new GenericModel(Paths.get("C:/Users/allegra/Desktop/eeveeMale.glb"));
        var correctSkeleton = TrinitySchemas.latest().getSkeletonSchema().read(Paths.get("C:/Users/allegra/Downloads/pm0133_00_00.json"));
        var skeleton = new TrinitySkeleton(model.skeleton, true);
        correctSkeleton.convertToDegrees();

        // Compare the Skeletons
        System.out.println("======Diff (Transform Node Count)======");
        System.out.println("GameFreaks: " + correctSkeleton.transformNodes.size());
        System.out.println("Ours: " + correctSkeleton.transformNodes.size());

        System.out.println("======Diff (Rig Bone Count)======");
        System.out.println("GameFreaks: " + correctSkeleton.bones.size());
        System.out.println("Ours: " + correctSkeleton.bones.size());

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
        var incorrectCount = 0;
        for (var entry : correctToOursMap.entrySet()) {
            var correct = entry.getKey();
            var ours = entry.getValue();

            if (skeleton.isInDegrees()) {
                if (Math.round(correct.transform.rotation.x) != Math.round(ours.transform.rotation.x) &&
                        Math.round(correct.transform.rotation.y) != Math.round(ours.transform.rotation.y) &&
                        Math.round(correct.transform.rotation.z) != Math.round(ours.transform.rotation.z)
                ) {
                    incorrectCount++;
                    System.out.println("\nINCORRECT TRANSFORM: " + correct.name);
                    System.out.println("Correct Rotations");
                    System.out.println("X: " + Math.round(correct.transform.rotation.x));
                    System.out.println("Y: " + Math.round(correct.transform.rotation.y));
                    System.out.println("Z: " + Math.round(correct.transform.rotation.z));
                    System.out.println("Our Rotations");
                    System.out.println("X: " + Math.round(ours.transform.rotation.x));
                    System.out.println("Y: " + Math.round(ours.transform.rotation.y));
                    System.out.println("Z: " + Math.round(ours.transform.rotation.z));
                }
            } else {
                if ((correct.transform.rotation.x * 1000f) / 1000f != Math.round(ours.transform.rotation.x * 1000f) / 1000f &&
                        Math.round(correct.transform.rotation.y * 1000f) / 1000f != Math.round(ours.transform.rotation.y * 1000f) / 1000f &&
                        Math.round(correct.transform.rotation.z * 1000f) / 1000f != Math.round(ours.transform.rotation.z * 1000f) / 1000f
                ) {
                    incorrectCount++;
                    System.out.println("\nINCORRECT TRANSFORM: " + correct.name);
                    System.out.println("Correct Rotations");
                    printRadiansTransform(correct);
                    System.out.println("Our Rotations");
                    printRadiansTransform(ours);
                }
            }
        }

        System.out.println("Incorrect Transforms: (" + incorrectCount + "/" + skeleton.transformNodes.size() + ")");
    }

    private static void printRadiansTransform(TrinitySkeleton.TransformNode node) {
        System.out.println("X: " + Math.round(node.transform.rotation.x * 1000f) / 1000f);
        System.out.println("Y: " + Math.round(node.transform.rotation.y * 1000f) / 1000f);
        System.out.println("Z: " + Math.round(node.transform.rotation.z * 1000f) / 1000f);
    }
}
