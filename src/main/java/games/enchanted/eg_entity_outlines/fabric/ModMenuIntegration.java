//? if fabric {
package games.enchanted.eg_entity_outlines.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import games.enchanted.eg_entity_outlines.common.config.ConfigScreen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::createConfigScreen;
    }
}
//?}