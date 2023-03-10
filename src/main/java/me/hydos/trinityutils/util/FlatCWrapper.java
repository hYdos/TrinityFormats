package me.hydos.trinityutils.util;

import java.io.IOException;
import java.nio.file.Path;

public class FlatCWrapper {
    private static final boolean FLATC_INSTALLED = checkPath();
    public static Path FLATC_LOCATION = null;

    private static String flatc() {
        if (FLATC_LOCATION == null) {
            verifyFlatC();
            return "flatc";
        } else return FLATC_LOCATION.toAbsolutePath().toString();
    }

    public static void convertToJson(Path schema, Path file) {
        try {
            new ProcessBuilder()
                    .inheritIO()
                    .command(flatc(), "--raw-binary", "--defaults-json", "--strict-json", "-o", ".", "-t", schema.toAbsolutePath().toString(), "--", file.toAbsolutePath().toString())
                    .start()
                    .waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void convertToBinary(Path workingDir, Path schema, Path file) {
        try {
            new ProcessBuilder()
                    .inheritIO()
                    .directory(workingDir.toFile())
                    .command(flatc(), "-b", schema.toAbsolutePath().toString(), file.toAbsolutePath().toString())
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
