package games.enchanted.eg_entity_outlines.common.gui.screen;

import games.enchanted.eg_entity_outlines.common.config.EntityWhitelist;
import games.enchanted.eg_entity_outlines.common.config.option.ConfigOption;
import games.enchanted.eg_entity_outlines.common.gui.widget.scroll.OptionsList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

public class EntityWhitelistScreen extends ConfigScreen {
    protected EntityWhitelistScreen(Screen parent, ConfigOption<EntityWhitelist> whitelistOption) {
        super(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    protected AbstractWidget buildDoneButtonWidget() {
        return Button.builder(CommonComponents.GUI_BACK, button -> this.onClose()).width(FOOTER_BUTTON_WIDTH).build();
    }

    @Override
    protected void buildOptionWidgets(OptionsList optionsList) {
    }

    @Override
    protected void updateFooterButtonState() {
        super.updateFooterButtonState();
        if(this.doneButton == null) return;
        this.doneButton.active = true;
    }
}
