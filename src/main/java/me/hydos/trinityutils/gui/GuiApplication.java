package me.hydos.trinityutils.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GuiApplication extends Application {
    private static final Path TRSKL_SCHEMA = Paths.get("C:/Users/allegra/Documents/PokeDocs-main/SV/Flatbuffers/model/trskl.fbs");
    private static final Path TRANM_SCHEMA = Paths.get("C:/Users/allegra/Documents/PokeDocs-main/SV/Flatbuffers/animation/tranm.fbs");

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(GuiApplication.class.getResource("/gui.fxml"));
        var scene = new Scene(loader.load());
        primaryStage.setTitle("Trinity Explorer");
        primaryStage.setScene(scene);
        primaryStage.show();

        var dir = "C:\\Users\\allegra\\Documents\\Github\\hYdos\\PokeFileTools\\src\\main\\java";
        var treeView = (TreeView<String>) scene.lookup("#fileTree");
        // Creates the root item.
        var rootItem = new TreeItem<>("file.glb");

        treeView.setCellFactory(TextFieldTreeCell.forTreeView());

        createTree(Paths.get(dir), rootItem);
        treeView.setRoot(rootItem);
        System.out.println("ok");
    }

    public static void createTree(Path file, TreeItem<String> parent) {
        if (Files.isDirectory(file)) {
            var treeItem = new TreeItem<>(file.getFileName().toString());
            parent.getChildren().add(treeItem);
            try {
                for (var f : Files.list(file).toList()) {
                    createTree(f, treeItem);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else parent.getChildren().add(new TreeItem<>(file.getFileName().toString()));
    }

    public static void main(String[] args) {
        launch(args);
//        var model = new GenericModel(Paths.get("C:/Users/allegra/Desktop/eeveeMale.glb"));
//        var skeleton = new TrinitySkeleton(model.skeleton, true);
//
//        // Apply +90 degree x rotation root bones (any bones with -1 rig index which is the root and origin)
//        for (var node : skeleton.transformNodes)
//            if (node.rigIndex == -1) node.transform.rotation
//                    .round()
//                    .add(90, 0, 0);
//
//        // Convert skeleton back into radians for exporting
//        skeleton.convertToRadians();
//        skeleton.exportToBinary(Paths.get("skeleton.trskl"), TRSKL_SCHEMA);
//        skeleton.exportToJson(Paths.get("skeleton.json"));
    }
}
