package me.hydos.trinityutils.format;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Represents what's needed to read and export a Trinity format.
 *
 * @param name         the full name of the file type
 * @param extension    the extension of the file. For example for a file with the name "binary.trmdl" it would be <b>trmdl</b>
 * @param reader       the method to read this type of file. If not supported, Can throw a {@link RuntimeException}
 * @param binaryWriter the method to write this type of file to binary. If not supported, Can throw a {@link RuntimeException}
 * @param jsonWriter   the method to write this type of file to json. If not supported, Can throw a {@link RuntimeException}
 * @param <T>          The type of class to be exported
 */
public record FileSchema<T>(
        String name,
        String extension,
        Function<Path, T> reader,
        BiConsumer<T, Path> binaryWriter,
        BiConsumer<T, Path> jsonWriter
) {

    public T read(Path fileLocation) {
        if (reader == null) throw new RuntimeException("Schema does not support reading files (reader == null)");

        return reader.apply(fileLocation);
    }

    public void exportToBinary(T value, Path outputLocation) {
        if (binaryWriter == null)
            throw new RuntimeException("Schema does not support binary writing (binaryWriter == null)");

        binaryWriter.accept(value, outputLocation);
    }

    public void exportToJson(T value, Path outputLocation) {
        if (jsonWriter == null)
            throw new RuntimeException("Schema does not support json writing (jsonWriter == null)");

        jsonWriter.accept(value, outputLocation);
    }
}
