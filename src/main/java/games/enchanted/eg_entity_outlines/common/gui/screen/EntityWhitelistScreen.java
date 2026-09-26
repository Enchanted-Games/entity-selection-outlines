package games.enchanted.eg_entity_outlines.common.gui.screen;

import games.enchanted.eg_entity_outlines.common.ModConstants;
import games.enchanted.eg_entity_outlines.common.config.EntityWhitelist;
import games.enchanted.eg_entity_outlines.common.config.option.ConfigOption;
import games.enchanted.eg_entity_outlines.common.gui.widget.whitelist.EntityWhitelistEntryWidget;
import games.enchanted.eg_entity_outlines.common.gui.widget.whitelist.WhitelistEntryProxy;
import games.enchanted.eg_entity_outlines.common.gui.widget.scroll.OptionsList;
import games.enchanted.eg_entity_outlines.common.util.ComponentUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class EntityWhitelistScreen extends ConfigScreen {
    private static final Component SEARCH_HINT = Component.translatable("gui.game_rule.search").withStyle(EditBox.SEARCH_HINT_STYLE);

    private static final int SEARCH_BOX_OFFSET = -3;
    private static final int SEARCH_BOX_PADDING = 2;
    private static final int SEARCH_BOX_HEIGHT = 15;

    final ConfigScreen configParent;
    final ConfigOption<EntityWhitelist> whitelistOption;

    final WhitelistEntryProxy whitelistEntryProxy;
    final List<EntityTypeNameAndId> entityTypes;
    @Nullable List<EntityTypeNameAndId> filteredTypes;
    Map<EntityTypeNameAndId, OptionsList.WidgetPosition> typeToPosition = Map.of();

    @Nullable EditBox searchBox;

    protected EntityWhitelistScreen(ConfigScreen parent, WhitelistEntryProxy whitelistEntryProxy, ConfigOption<EntityWhitelist> whitelistOption) {
        super(parent, ComponentUtil.MODIFY_WHITELIST);

        this.configParent = parent;
        this.whitelistOption = whitelistOption;
        this.whitelistEntryProxy = whitelistEntryProxy;

        this.entityTypes = ModConstants.knownEntityTypes().stream().map(identifier -> {
            Optional<Holder.Reference<EntityType<?>>> type = BuiltInRegistries.ENTITY_TYPE.get(identifier);
            if(type.isEmpty()) {
                return new EntityTypeNameAndId(identifier.toString(), identifier);
            }
            Component translatedName = type.get().value().getDescription();
            return new EntityTypeNameAndId(translatedName.getString(), identifier);
        }).toList();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    protected void createHeader(HeaderAndFooterLayout layout) {
        super.createHeader(layout);

        this.searchBox = this.addRenderableWidget(new EditBox(this.font, 200, SEARCH_BOX_HEIGHT, Component.empty()));
        this.searchBox.setPosition((this.width / 2) - (this.searchBox.getWidth() / 2), layout.getHeaderHeight() - SEARCH_BOX_PADDING + SEARCH_BOX_OFFSET);
        this.searchBox.setHint(SEARCH_HINT);
        this.searchBox.setResponder(this::performSearch);
    }

    @Override
    protected AbstractWidget buildDoneButtonWidget() {
        return Button.builder(CommonComponents.GUI_BACK, button -> this.onClose()).width(FOOTER_BUTTON_WIDTH).build();
    }

    @Override
    protected void buildOptionWidgets(OptionsList optionsList) {
        Map<EntityTypeNameAndId, OptionsList.WidgetPosition> map = new HashMap<>();

        for (EntityTypeNameAndId type : this.entityTypes) {
            map.put(
                type,
                optionsList.addBigOption(new EntityWhitelistEntryWidget(
                    this.whitelistOption.getValue().containsEntity(type.id()),
                    this.whitelistEntryProxy.isEntryEnabled(type.id()),
                    type,
                    this.whitelistEntryProxy
                ), 22)
            );
        }

        this.typeToPosition = map;
    }

    protected void performSearch(String query) {
        List<EntityTypeNameAndId> containedInQuery = new ArrayList<>();

        if(query.isEmpty()) {
            this.filteredTypes = null;
            this.updateVisibleElements();
            return;
        }

        String lowercaseQuery = query.toLowerCase(Locale.ROOT);

        for (EntityTypeNameAndId type : this.entityTypes) {
            if(type.name().toLowerCase(Locale.ROOT).contains(lowercaseQuery)) {
                containedInQuery.add(type);
            }
        }

        this.filteredTypes = containedInQuery;
        this.updateVisibleElements();
    }

    protected void updateVisibleElements() {
        if(this.optionsList == null) return;

        for (EntityTypeNameAndId type : this.entityTypes) {
            boolean visible = this.filteredTypes == null || this.filteredTypes.contains(type);
            this.optionsList.setWidgetVisibility(this.typeToPosition.get(type), visible);
        }

        this.optionsList.repositionElements();
        this.optionsList.setScrollAmount(0);
    }

    @Override
    protected void updateFooterButtonState() {
        super.updateFooterButtonState();
        if(this.doneButton == null) return;
        this.doneButton.active = true;
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        final int headerHeight = this.layout.getHeaderHeight();

        if(this.optionsList != null) {
            this.optionsList.setRectangle(
                this.width,
                this.height - headerHeight - this.layout.getFooterHeight() - (SEARCH_BOX_PADDING * 2) + SEARCH_BOX_OFFSET - SEARCH_BOX_HEIGHT,
                0,
                headerHeight + (SEARCH_BOX_PADDING * 2) + SEARCH_BOX_HEIGHT
            );
            this.optionsList.repositionElements();
        }

        if(this.searchBox == null) return;
        this.searchBox.setPosition((this.width / 2) - (this.searchBox.getWidth() / 2), this.layout.getHeaderHeight() - SEARCH_BOX_PADDING + SEARCH_BOX_OFFSET);
    }

    @Override
    protected void undoChanges() {
        if(this.optionsList == null) return;
        this.optionsList.visitChildren(widget -> {
            if(!(widget instanceof EntityWhitelistEntryWidget whitelistWidget)) return;
            whitelistWidget.resetPendingChanges();
        });

        this.configParent.undoChanges();
    }

    @Override
    protected void saveChanges() {
        if(this.optionsList == null) return;
        this.optionsList.visitChildren(widget -> {
            if(!(widget instanceof EntityWhitelistEntryWidget whitelistWidget)) return;
            whitelistWidget.applyPendingChanges();
        });

        this.configParent.saveChanges();
    }

    @Override
    protected boolean hasPendingChanges() {
        return this.configParent.hasPendingChanges();
    }

    public record EntityTypeNameAndId(String name, Identifier id) {
    }
}
