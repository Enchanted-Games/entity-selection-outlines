package games.enchanted.eg_entity_outlines.common.gui.widget.option;

import games.enchanted.eg_entity_outlines.common.config.option.BoolOption;
import games.enchanted.eg_entity_outlines.common.config.option.ConfigOption;
import games.enchanted.eg_entity_outlines.common.util.ComponentUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class OnOffWidget extends Button implements OptionWidget<ConfigOption<Boolean>> {
    protected final ConfigOption<Boolean> option;
    protected boolean value;

    protected @Nullable OnChange onChange = null;

    public OnOffWidget(int x, int y, ConfigOption<Boolean> option) {
        super(x, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.literal(option.getJsonKey()), (button) -> {}, DEFAULT_NARRATION);
        this.option = option;
        this.value = option.getValue();
        updateMessage();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    protected void updateMessage() {
        this.message = ComponentUtil.optionMessage(
            ComponentUtil.createOptionName(this.option),
            this.value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF,
            this.isActive(),
            this.option.isDirty()
        );
        this.setTooltip(Tooltip.create(ComponentUtil.createOptionTooltip(this.option)));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.extractDefaultSprite(graphics);
        this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.value = !this.value;
        this.option.setPendingValue(this.value);
        if(this.onChange != null) {
            this.onChange.changed();
        }
        updateMessage();
    }

    @Override
    public ConfigOption<Boolean> getOption() {
        return this.option;
    }

    @Override
    public void refreshValue() {
        this.value = this.option.getPendingOrCurrentValue();
        updateMessage();
    }

    @Override
    public void onChange(OnChange changeCallback) {
        this.onChange = changeCallback;
    }

    @Override
    public void refreshVisual() {
        this.updateMessage();
    }
}
