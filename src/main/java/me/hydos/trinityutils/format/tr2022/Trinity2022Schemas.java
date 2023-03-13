package me.hydos.trinityutils.format.tr2022;

import me.hydos.trinityutils.format.FileSchema;
import me.hydos.trinityutils.format.tr2022.skeleton.TrinitySkeleton;

/**
 * Built in supported formats for Scarlet and Violet's trinity version
 */
public class Trinity2022Schemas {
    private static final FileSchema<TrinitySkeleton> TRSKL = new FileSchema<>(
            "Trinity Skeleton",
            "trskl",
            TrinitySkeleton::read,
            TrinitySkeleton::exportToBinary,
            TrinitySkeleton::exportToJson
    );

    public FileSchema<TrinitySkeleton> getSkeletonSchema() {
        return TRSKL;
    }
}
