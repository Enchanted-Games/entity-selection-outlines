//? if fabric {
package games.enchanted.eg_entity_outlines.fabric;

import games.enchanted.eg_entity_outlines.common.ModEntry;
import net.fabricmc.api.ModInitializer;

public class FabricEntry implements ModInitializer {
    @Override
    public void onInitialize() {
        ModEntry.init();
    }
}
//?}