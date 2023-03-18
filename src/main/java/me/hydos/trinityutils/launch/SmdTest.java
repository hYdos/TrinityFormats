package me.hydos.trinityutils.launch;

import me.hydos.trinityutils.format.tr2022.skeleton.TrinitySkeleton;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class SmdTest {

    public static void main(String[] args) throws IOException {
        var path = Paths.get("C:/Users/hydos/Downloads/pm0133_00_00.smd");
        var blocks = Files.readString(path).substring("version 1".length())
                .replace("\r", "")
                .split("end\n");

        var idToParentMap = new HashMap<Integer, Integer>();
        var idToStrMap = new HashMap<Integer, String>();
        var idToTransMap = new HashMap<Integer, Vector3f>();
        var idToRotMap = new HashMap<Integer, Vector3f>();

        for (var block : blocks) {
            var lines = block.split("\n");

            if (lines[1].equals("nodes")) {
                for (int i = 2; i < lines.length; i++) {
                    var line = lines[i];
                    var split = line.split(" ");
                    var id = Integer.parseInt(split[0]) - 1;
                    var name = split[1].substring(1, split[1].length() - 1);
                    idToStrMap.put(id, name);
                    idToParentMap.put(id, Integer.parseInt(split[2]));
                }
            }

            if (lines[0].equals("skeleton")) {
                for (int i = 2; i < lines.length; i++) {
                    var line = lines[i];
                    var split = line.split(" {2}");
                    var id = Integer.parseInt(split[0]) - 1;
                    idToTransMap.put(id, parseVec3(split[1]));
                    idToRotMap.put(id, parseVec3(split[2]));
                }
            }
        }

        var skeleton = new TrinitySkeleton();

        for (var bone : idToStrMap.keySet()) {
            if (bone < 0) continue; // Blender Implicit
            if (bone != 0) skeleton.bones.add(new TrinitySkeleton.Bone());

            var node = new TrinitySkeleton.TransformNode(
                    idToStrMap.get(bone),
                    new TrinitySkeleton.TransformNode.Transform(
                            new Vector3f(1, 1, 1),
                            idToRotMap.get(bone),
                            idToTransMap.get(bone)
                    ),
                    new Vector3f(),
                    new Vector3f(),
                    bone == 0 ? -1 : bone - 1,
                    Math.max(-1, skeleton.bones.size() - 2),
                    "",
                    "Default"
            );

            skeleton.transformNodes.add(node);
        }

        System.out.println("Exporting Anyway");
        var exportPath = "C:/Users/hydos/AppData/Roaming/yuzu/load/0100A3D008C5C000/testMod/romfs/pokemon/data/pm0133/pm0133_00_00/pm0133_00_00.trskl";
        skeleton.exportToBinary(Paths.get(exportPath));
        var jsonExportPath = "D:\\Projects\\hYdos\\PokeFileTools\\file.json";
        skeleton.exportToJson(Paths.get(jsonExportPath));
    }

    private static Vector3f parseVec3(String str) {
        var xyz = str.split(" ");
        return new Vector3f(Float.parseFloat(xyz[0]), Float.parseFloat(xyz[1]), Float.parseFloat(xyz[2]));
    }
}
