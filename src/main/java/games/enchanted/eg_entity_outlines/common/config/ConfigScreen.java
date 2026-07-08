package games.enchanted.eg_entity_outlines.common.config;

import games.enchanted.eg_entity_outlines.common.ModConstants;
import games.enchanted.eg_entity_outlines.common.config.option.ConfigOption;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigScreen extends Screen {
    private static final List<ConfigOption<Boolean>> TOGGLES = List.of(
        ConfigOptions.MOD_ENABLED_OPTION,
        ConfigOptions.OUTLINE_BLOCK_ATTACHED,
        ConfigOptions.OUTLINE_ARMOUR_STANDS,
        ConfigOptions.OUTLINE_BOATS,
        ConfigOptions.OUTLINE_MINECARTS,
        ConfigOptions.OUTLINE_END_CRYSTALS,
        ConfigOptions.OUTLINE_EVERYTHING
    );

    private final Screen parentScreen;

    private HeaderAndFooterLayout layout;
    private GridLayout grid;

    ConfigScreen(Screen parent) {
        super(Component.translatableWithFallback("%s.configScreen.title".formatted(ModConstants.MOD_ID), ModConstants.MOD_NAME + " Config"));
        this.parentScreen = parent;
    }

    @Override
    protected void init() {
        this.layout = new HeaderAndFooterLayout(this, HeaderAndFooterLayout.DEFAULT_HEADER_AND_FOOTER_HEIGHT, HeaderAndFooterLayout.DEFAULT_HEADER_AND_FOOTER_HEIGHT);
        this.grid = new GridLayout(0, this.layout.getHeaderHeight());
        this.layout.addToContents(this.grid);

        // mod enabled button
        for (int i = 0; i < TOGGLES.size(); i++) {
            ConfigOption<Boolean> option = TOGGLES.get(i);
            int column = i % 2;
            int row = i - column;

            this.grid.addChild(
                makeBooleanValueButton(option)
                    .bounds((this.width / 2) - (Button.DEFAULT_WIDTH / 2), (this.height / 2), Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT)
                .build(),
                row,
                column
            );
        }

        this.grid.newCellSettings().alignHorizontallyCenter();
        this.grid.columnSpacing(8);
        this.grid.rowSpacing(2);
        this.grid.arrangeElements();
        this.grid.visitWidgets(this::addRenderableWidget);

        // done button
        AbstractWidget doneButton = (
            Button.builder(
                CommonComponents.GUI_DONE,
                button -> onClose()
            )
            .bounds((this.width / 2) - (Button.DEFAULT_WIDTH / 2), this.height - 40, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT)
        .build());

        this.layout.addToFooter(doneButton);
        this.addRenderableWidget(doneButton);

        this.layout.arrangeElements();
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.textRenderer().accept(TextAlignment.CENTER, this.width / 2, this.layout.getHeaderHeight(), this.title);
    }

    @Override
    public void onClose() {
        saveConfigOptions();
        //? if minecraft: >= 26.2 {
        this.minecraft.gui.setScreen(this.parentScreen);
        //? } else {
        /*this.minecraft.setScreen(this.parentScreen);
        *///? }
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        this.layout.arrangeElements();
        this.grid.arrangeElements();
    }

    private void saveConfigOptions() {
        ConfigOptions.saveIfAnyDirtyOptions();
    }

    private Button.Builder makeBooleanValueButton(ConfigOption<Boolean> value) {
        String configKey = value.getJsonKey();
        Component buttonContent = Component.translatableWithFallback(getTranslationKeyForOption(configKey), configKey);
        Component buttonTooltipText = Component.translatable(getTranslationKeyForOption(configKey) + ".tooltip");

        return Button.builder(CommonComponents.optionNameValue(buttonContent, trueFalseOptionStatus(value.getPendingOrCurrentValue())),
            button -> {
                boolean newValue = !value.getPendingOrCurrentValue();
                value.setPendingValue(newValue);

                Component buttonValue = trueFalseOptionStatus(newValue);

                button.setMessage(CommonComponents.optionNameValue(buttonContent, buttonValue));
            })
        .tooltip(Tooltip.create(buttonTooltipText));
    }

    public static Component trueFalseOptionStatus(boolean value) {
        return value ? CommonComponents.GUI_YES : CommonComponents.GUI_NO;
    }

    public static String getTranslationKeyForOption(String configKey) {
        return "%s.option.%s".formatted(ModConstants.MOD_ID, configKey);
    }

    public static Screen createConfigScreen(Screen parentScreen) {
        return new ConfigScreen(parentScreen);
    }
}