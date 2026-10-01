//? if neoforge {
/*package games.enchanted.eg_entity_outlines.neoforge;

import games.enchanted.eg_entity_outlines.common.ModConstants;
import games.enchanted.eg_entity_outlines.common.ModEntry;
import games.enchanted.eg_entity_outlines.common.gui.screen.ConfigScreen;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/^*
 * This is the entry point for your mod's neoforge side.
 ^/
@Mod(ModConstants.MOD_ID)
public class NeoForgeEntry {
    public NeoForgeEntry() {
        ModEntry.init();

        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class, () -> (client, parent) -> ConfigScreen.create(parent)
        );
    }
}
*///?}