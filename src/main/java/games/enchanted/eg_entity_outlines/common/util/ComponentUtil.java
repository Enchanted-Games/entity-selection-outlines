package games.enchanted.eg_entity_outlines.common.util;

import games.enchanted.eg_entity_outlines.common.config.option.ConfigOption;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class ComponentUtil {
    public static final Component MODIFY_WHITELIST = Component.translatable("gui.eg_entity_outlines.button.modify_whitelist");
    public static final Component UNDO = Component.translatable("gui.eg_entity_outlines.button.undo");
    public static final Component APPLY = Component.translatable("gui.eg_entity_outlines.button.apply");

    public static final Component GENERAL_CATEGORY = Component.translatable("gui.eg_entity_outlines.category.general");
    public static final Component WHITELIST_CATEGORY = Component.translatable("gui.eg_entity_outlines.category.entity_whitelist");

    public static final String OPTION_PREFIX = "gui.eg_entity_outlines.option.";
    public static final String OPTION_TOOLTIP_SUFFIX = ".tooltip";

    public static Component createOptionTooltip(ConfigOption<?> option) {
        return Component.translatable(OPTION_PREFIX + option.getJsonKey() + OPTION_TOOLTIP_SUFFIX);
    }
    public static Component createOptionName(ConfigOption<?> option) {
        return Component.translatable(OPTION_PREFIX + option.getJsonKey());
    }

    public static Component optionMessage(Component optionName, Component value, boolean active, boolean modified) {
        return CommonComponents.optionNameValue(
            optionName,
                value.copy().withStyle(style -> style.withItalic(modified))
            ).withColor(active ? -1 : CommonColors.LIGHT_GRAY);
    }
}
