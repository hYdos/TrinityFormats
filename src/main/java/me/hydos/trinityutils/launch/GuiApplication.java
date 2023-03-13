package me.hydos.trinityutils.launch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GuiApplication extends Application {
    public static final Path TRSKL_SCHEMA = Paths.get("C:/Users/allegra/Documents/PokeDocs-main/SV/Flatbuffers/model/trskl.fbs");
    private static final Path TRANM_SCHEMA = Paths.get("C:/Users/allegra/Documents/PokeDocs-main/SV/Flatbuffers/animation/tranm.fbs");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(GuiApplication.class.getResource("/gui.fxml"));
        var scene = new Scene(loader.load());
        primaryStage.setTitle("Trinity Explorer");
        primaryStage.setScene(scene);
        primaryStage.show();

        var dir = "C:/Users/allegra/Documents/Github/hYdos/PokeFileTools/src/main/java";
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
}
