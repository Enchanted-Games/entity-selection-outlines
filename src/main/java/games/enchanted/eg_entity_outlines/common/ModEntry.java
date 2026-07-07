package games.enchanted.eg_entity_outlines.common;

/**
 * This is the entry point for your mod's common code, called by each modloader specific entrypoint.
 */
public class ModEntry {
    public static void init() {
        Logging.info("Mod is loading on a {} environment!", ModConstants.TARGET_PLATFORM);
    }
}
