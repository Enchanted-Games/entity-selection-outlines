//? if fabric {
package games.enchanted.eg_entity_outlines.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import games.enchanted.eg_entity_outlines.common.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> new ConfirmScreen(
            b -> {
                //? if <= 26.1 {
                /*Minecraft.getInstance().setScreen(parent);
                *///? } else {
                Minecraft.getInstance().gui.setScreen(parent);
                //? }
            },
            Component.literal(ModConstants.MOD_NAME + " Config Placeholder"),
            Component.empty()
        );
    }
}
//?}