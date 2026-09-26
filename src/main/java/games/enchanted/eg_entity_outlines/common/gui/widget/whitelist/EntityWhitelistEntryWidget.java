package games.enchanted.eg_entity_outlines.common.gui.widget.whitelist;

import games.enchanted.eg_entity_outlines.common.gui.screen.EntityWhitelistScreen;
import games.enchanted.eg_entity_outlines.common.util.ComponentUtil;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;

public class EntityWhitelistEntryWidget extends Button {
    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
        Identifier.withDefaultNamespace("widget/button"),
        Identifier.withDefaultNamespace("widget/button_disabled"),
        Identifier.withDefaultNamespace("widget/button_highlighted")
    );
    private static final WidgetSprites CHECKBOX_SPRITES = new WidgetSprites(
        Identifier.withDefaultNamespace("widget/checkbox"),
        Identifier.withDefaultNamespace("widget/checkbox"),
        Identifier.withDefaultNamespace("widget/checkbox_highlighted")
    );
    private static final WidgetSprites CHECKBOX_SELECTED_SPRITES = new WidgetSprites(
        Identifier.withDefaultNamespace("widget/checkbox_selected"),
        Identifier.withDefaultNamespace("widget/checkbox_selected"),
        Identifier.withDefaultNamespace("widget/checkbox_selected_highlighted")
    );

    protected final EntityWhitelistScreen.EntityTypeNameAndId type;

    protected boolean initialEntryInWhitelist;
    protected boolean entryInWhitelist;
    protected final WhitelistEntryProxy whitelistEntryProxy;

    public EntityWhitelistEntryWidget(boolean initialEntryInWhitelist, boolean entryInWhitelist, EntityWhitelistScreen.EntityTypeNameAndId type, WhitelistEntryProxy whitelistEntryProxy) {
        super(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.literal(type.name()), (button) -> {}, DEFAULT_NARRATION);
        this.type = type;
        this.initialEntryInWhitelist = initialEntryInWhitelist;
        this.entryInWhitelist = entryInWhitelist;
        this.whitelistEntryProxy = whitelistEntryProxy;
        updateMessage();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    protected void updateMessage() {
        this.message = ComponentUtil.optionMessage(
            Component.literal(type.name()),
            this.entryInWhitelist ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF,
            this.isActive(),
            this.hasPendingChange()
        );
        this.setTooltip(Tooltip.create(
            Component.literal(this.type.id().toString()).withStyle(style -> style.withColor(CommonColors.LIGHT_GRAY))
        ));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        ActiveTextCollector textCollector = graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE);
        int margin = 2;
        int rightPadding = this.getHeight();

        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            BUTTON_SPRITES.get(this.active, this.isHoveredOrFocused()),
            this.getX(),
            this.getY(),
            this.getWidth() - rightPadding,
            this.getHeight(),
            ARGB.white(this.alpha)
        );

        int left = this.getX() + margin;
        int right = this.getX() + this.getWidth() - margin - rightPadding + 1;
        int top = this.getY();
        int bottom = this.getY() + this.getHeight();
        textCollector.acceptScrollingWithDefaultCenter(
            Component.literal(this.type.name()).withStyle(style -> style.withItalic(this.hasPendingChange())),
            left,
            right,
            top,
            bottom
        );

        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            (this.entryInWhitelist ? CHECKBOX_SELECTED_SPRITES : CHECKBOX_SPRITES).get(this.active, this.isHoveredOrFocused()),
            right,
            this.getY(),
            rightPadding,
            this.getHeight(),
            ARGB.white(this.alpha)
        );
    }

    public void resetPendingChanges() {
        this.entryInWhitelist = this.initialEntryInWhitelist;
        this.updateProxyValue();
    }

    public void applyPendingChanges() {
        this.initialEntryInWhitelist = this.entryInWhitelist;
    }

    public boolean hasPendingChange() {
        return this.initialEntryInWhitelist != this.entryInWhitelist;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.entryInWhitelist = !this.entryInWhitelist;
        this.updateProxyValue();
        this.updateMessage();
    }

    protected void updateProxyValue() {
        this.whitelistEntryProxy.setWhitelistEntryEnabled(this.type.id(), this.entryInWhitelist);
    }
}

