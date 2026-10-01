package games.enchanted.eg_entity_outlines.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModConstants {
    public static final String MOD_NAME = "Entity Selection Outlines";
    public static final String MOD_ID = "eg_entity_outlines";

    public static final String TARGET_PLATFORM =
    //? if fabric {
        "fabric"
    //?} else {
        /*"neoforge"
     *///?}
    ;

    private static @Nullable List<Identifier> KNOWN_ENTITY_TYPES;

    public static List<Identifier> knownEntityTypes() {
        if(KNOWN_ENTITY_TYPES != null) return KNOWN_ENTITY_TYPES;

        List<Identifier> entityTypes = new ArrayList<>();
        BuiltInRegistries.ENTITY_TYPE.listElementIds().forEach(key -> entityTypes.add(key.identifier()));
        KNOWN_ENTITY_TYPES = entityTypes;
        return entityTypes;
    }
}
