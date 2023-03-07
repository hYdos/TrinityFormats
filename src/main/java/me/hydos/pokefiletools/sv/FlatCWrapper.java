package me.hydos.pokefiletools.sv;

import java.io.IOException;
import java.nio.file.Path;

public class FlatCWrapper {
    private static final boolean FLATC_INSTALLED = checkPath();

    public static void convertToJson(Path schema, Path file) {
        try {
            verifyFlatC();
            new ProcessBuilder()
                    .inheritIO()
                    .command("D:/Programs/path/flatc.exe", "--raw-binary", "--defaults-json", "--strict-json", "-o", ".", "-t", schema.toAbsolutePath().toString(), "--", file.toAbsolutePath().toString())
                    .start()
                    .waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void convertToBinary(Path schema, Path file) {
        try {
            verifyFlatC();
            new ProcessBuilder()
                    .inheritIO()
                    .command("D:/Programs/path/flatc.exe", "-b", schema.toAbsolutePath().toString(), file.toAbsolutePath().toString())
                    .start()
                    .waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void verifyFlatC() {
        if (!FLATC_INSTALLED) System.err.println("Please install flatc to your path before using this tool.");
    }

    private static boolean checkPath() {
        try {
            Runtime.getRuntime().exec("flatc");
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
