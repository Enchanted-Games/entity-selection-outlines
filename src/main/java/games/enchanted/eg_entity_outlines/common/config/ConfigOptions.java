package games.enchanted.eg_entity_outlines.common.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import games.enchanted.eg_entity_outlines.common.Logging;
import games.enchanted.eg_entity_outlines.common.ModConstants;
import games.enchanted.eg_entity_outlines.common.PlatformHelper;
import games.enchanted.eg_entity_outlines.common.config.option.BoolOption;
import games.enchanted.eg_entity_outlines.common.config.option.ConfigOption;
import games.enchanted.eg_entity_outlines.common.config.option.EntityWhitelistOption;

//? if minecraft: >= 26.2 {
import net.minecraft.world.entity.EntityTypeIds;
//? } else {
/*import net.minecraft.world.entity.EntityType;
*///? }

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigOptions {
    private static final List<ConfigOption<?>> OPTIONS = new ArrayList<>();

    public static final ConfigOption<Boolean> MOD_ENABLED_OPTION = registerOption(new BoolOption(
        true,
        true,
        "mod_enabled"
    ));

    public static final ConfigOption<Boolean> ALWAYS_USE_HIGH_CONTRAST = registerOption(new BoolOption(
        false,
        false,
        "always_use_high_contrast"
    ));

    public static final ConfigOption<Boolean> OUTLINE_EVERYTHING = registerOption(new BoolOption(
        false,
        false,
        "outline_everything"
    ));


    public static final ConfigOption<Boolean> INVERT_WHITELIST = registerOption(new BoolOption(
        false,
        false,
        "invert_whitelist"
    ));

    public static final ConfigOption<EntityWhitelist> ENTITY_WHITELIST = registerOption(new EntityWhitelistOption(
        EntityWhitelist.create(List.of(
            //? if minecraft: >= 26.2 {
            EntityTypeIds.CUSHION,
            EntityTypeIds.ITEM_FRAME,
            EntityTypeIds.GLOW_ITEM_FRAME,
            EntityTypeIds.LEASH_KNOT,
            EntityTypeIds.PAINTING,
            EntityTypeIds.ARMOR_STAND,
            EntityTypeIds.END_CRYSTAL,
            EntityTypeIds.FALLING_BLOCK,
            EntityTypeIds.INTERACTION
            //?} else {
            /*EntityType.ITEM_FRAME,
            EntityType.GLOW_ITEM_FRAME,
            EntityType.LEASH_KNOT,
            EntityType.PAINTING,
            EntityType.ARMOR_STAND,
            EntityType.END_CRYSTAL,
            EntityType.FALLING_BLOCK,
            EntityType.INTERACTION
            *///? }
        )),
        "entity_whitelist"
    ));

    private static <T> ConfigOption<T> registerOption(ConfigOption<T> option) {
        OPTIONS.add(option);
        return option;
    }

    private static final String FILE_NAME = ModConstants.MOD_ID + ".json";

    private static File getConfigFile() {
        return PlatformHelper.getConfigPath().resolve(FILE_NAME).toFile();
    }

    public static boolean hasDirtyOptions() {
        return OPTIONS.stream().anyMatch(ConfigOption::isDirty);
    }

    public static boolean hasDirtyOptionsDifferentFromCurrent() {
        return OPTIONS.stream().anyMatch(ConfigOption::isDirtyDifferentFromCurrent);
    }

    public static void saveIfAnyDirtyOptions() {
        if(OPTIONS.stream().noneMatch(ConfigOption::isDirty)) return;
        for (ConfigOption<?> option : OPTIONS) {
            if(option.isDirty()) option.applyPendingValue();
        }
        saveConfig();
    }

    public static void saveConfig() {
        JsonObject root = new JsonObject();

        for (ConfigOption<?> option : OPTIONS) {
            JsonElement encoded = option.toJson();
            if(encoded == null) {
                Logging.warn("Config value '{}' will not be saved as it couldn't be encoded.", option.getJsonKey());
                continue;
            }
            root.add(option.getJsonKey(), encoded);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String encodedJson = gson.toJson(root);

        try (FileWriter writer = new FileWriter(getConfigFile())) {
            writer.write(encodedJson);
        } catch (IOException e) {
            Logging.error("Failed to write config file '{}', {}", FILE_NAME, e);
        }
    }

    public static void readConfig() {
        Gson gson = new Gson();
        JsonObject decodedConfig = new JsonObject();

        try {
            JsonReader jsonReader = gson.newJsonReader(new FileReader(getConfigFile()));
            jsonReader.setStrictness(Strictness.LENIENT);
            decodedConfig = JsonParser.parseReader(jsonReader).getAsJsonObject();
        } catch (JsonParseException e) {
            Logging.error("Failed to parse config file '{}', {}", FILE_NAME, e);
        } catch (FileNotFoundException e) {
            Logging.info("Config file '{}' not found", FILE_NAME);
        }

        boolean requireResave = false;

        for (ConfigOption<?> option : OPTIONS) {
            try {
                option.fromJson(decodedConfig);
            } catch (Exception e) {
                Logging.warn("An exception occurred while decoding config option '{}'.\n{}", option.getJsonKey(), e);
                option.resetToDefault(true);
                requireResave = true;
            }
        }

        if(requireResave) {
            saveConfig();
        }
    }

    public static void resetAndSaveAllOptions() {
        for (ConfigOption<?> option : OPTIONS) {
            option.resetToDefault(true);
        }
        saveConfig();
    }

    public static void clearAllPendingValues() {
        for (ConfigOption<?> option : OPTIONS) {
            option.clearPendingValue();
        }
    }
}
