package me.hydos.trinityutils.format;

import me.hydos.trinityutils.format.tr2022.Trinity2022Schemas;

/**
 * Will extend the latest version of Trinity this tool supports.
 */
public class TrinitySchemas {
    private static final Trinity2022Schemas INSTANCE = new Trinity2022Schemas();

    public static Trinity2022Schemas latest() {
        return INSTANCE;
    }
}
