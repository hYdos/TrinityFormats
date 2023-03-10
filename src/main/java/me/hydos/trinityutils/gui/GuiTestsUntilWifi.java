package me.hydos.trinityutils.gui;

import me.hydos.trinityutils.model.skeleton.TrinitySkeleton;
import me.hydos.trinityutils.util.FlatCWrapper;
import me.hydos.trinityutils.util.GenericModel;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GuiTestsUntilWifi {
    private static final Path TRSKL_SCHEMA = Paths.get("C:/Users/allegra/Documents/PokeDocs-main/SV/Flatbuffers/model/trskl.fbs");
    private static final Path TRANM_SCHEMA = Paths.get("C:/Users/allegra/Documents/PokeDocs-main/SV/Flatbuffers/animation/tranm.fbs");

    public static void main(String[] args) {
        var model = new GenericModel(Paths.get("C:/Users/allegra/Desktop/eeveeMaleAnim.glb"));
        var skeleton = new TrinitySkeleton(model.skeleton, true);

        // Apply +90 degree x rotation root bones (any bones with -1 rig index which is the root and origin)
        for (var node : skeleton.transformNodes)
            if (node.rigIndex == -1) node.transform.rotation
                    .round()
                    .add(90, 0, 0);

        // Convert skeleton back into radians for exporting
        skeleton.convertToRadians();
        skeleton.exportToBinary(Paths.get("skeleton.trskl"), TRSKL_SCHEMA);
        skeleton.exportToJson(Paths.get("skeleton.json"));
    }
}
