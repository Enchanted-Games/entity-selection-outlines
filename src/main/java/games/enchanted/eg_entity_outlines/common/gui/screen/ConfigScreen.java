package games.enchanted.eg_entity_outlines.common.gui.screen;

import games.enchanted.eg_entity_outlines.common.config.ConfigOptions;
import games.enchanted.eg_entity_outlines.common.gui.widget.option.OnOffWidget;
import games.enchanted.eg_entity_outlines.common.gui.widget.option.OptionWidget;
import games.enchanted.eg_entity_outlines.common.gui.widget.scroll.OptionsList;
import games.enchanted.eg_entity_outlines.common.util.ScreenUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public class ConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable("options.videoTitle");
    protected static final int FOOTER_BUTTON_WIDTH = 98;

    protected final Screen parent;

    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    protected @Nullable OptionsList optionsList;

    protected @Nullable AbstractWidget undoButton;
    protected @Nullable AbstractWidget applyButton;
    protected @Nullable AbstractWidget doneButton;

    protected final ArrayList<OptionWidget<?>> optionWidgets = new ArrayList<>();

    protected ConfigScreen(Screen parent, Component title) {
        super(title);
        this.parent = parent;
    }
    protected ConfigScreen(Screen parent) {
        this(parent, TITLE);
    }

    public static Screen create(Screen parent) {
        return new ConfigScreen(parent);
    }

    @Override
    protected void init() {
        this.createSingleColumnLayout();

        this.visitOptionsAndAddListeners();
        this.layout.visitWidgets(this::addRenderableWidget);

        this.updateFooterButtonState();
        this.repositionElements();
    }

    protected void createSingleColumnLayout() {
        this.layout.addTitleHeader(this.title, this.font);
        int headerHeight = this.layout.getHeaderHeight();

        this.createFooterWidgets();

        this.optionsList = new OptionsList(
            0,
            headerHeight,
            this.width,
            this.height - headerHeight - this.layout.getFooterHeight()
        );
        this.addRenderableWidget(this.optionsList);

        this.buildOptionWidgets(this.optionsList);
    }

    protected void createFooterWidgets() {
        LinearLayout footerLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        this.undoButton = footerLayout.addChild(
            Button.builder(Component.literal("undo"), button -> this.undoChanges()).width(FOOTER_BUTTON_WIDTH).build()
        );
        this.applyButton = footerLayout.addChild(
            Button.builder(Component.literal("apply"), button -> this.saveChanges()).width(FOOTER_BUTTON_WIDTH).build()
        );
        this.doneButton = footerLayout.addChild(
            this.buildDoneButtonWidget()
        );
    }

    protected AbstractWidget buildDoneButtonWidget() {
        return Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(FOOTER_BUTTON_WIDTH).build();
    }

    protected void buildOptionWidgets(OptionsList optionsList) {
        optionsList.addBigOption(
            new OnOffWidget(0, 0, ConfigOptions.MOD_ENABLED_OPTION)
        );
    }

    private void visitOptionsAndAddListeners() {
        if(!this.optionWidgets.isEmpty()) {
            throw new IllegalStateException("visitOptionsAndAddListeners was called while optionWidgets list was not empty");
        }
        if(this.optionsList != null) {
            this.visitOptionList(this.optionsList);
        }
    }

    private void visitOptionList(OptionsList optionsList) {
        optionsList.visitChildren(widget -> {
            if(!(widget instanceof OptionWidget<?> optionWidget)) return;
            optionWidget.onChange(this::refreshOptionWidgetVisuals);
            this.optionWidgets.add(optionWidget);
        });
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !this.hasPendingChanges();
    }

    private void undoChanges() {
        ConfigOptions.clearAllPendingValues();
        this.refreshOptionWidgetValues();
    }

    private void saveChanges() {
        ConfigOptions.saveIfAnyDirtyOptions();
        this.refreshOptionWidgetValues();
    }

    protected boolean hasPendingChanges() {
        return ConfigOptions.hasDirtyOptions();
    }

    protected void refreshOptionWidgetValues() {
        this.optionWidgets.forEach(OptionWidget::refreshValue);
        this.updateFooterButtonState();
    }

    protected void refreshOptionWidgetVisuals() {
        this.optionWidgets.forEach(OptionWidget::refreshVisual);
        this.updateFooterButtonState();
    }

    protected void updateFooterButtonState() {
        if(this.undoButton == null || this.applyButton == null || this.doneButton == null) return;
        if(this.hasPendingChanges()) {
            this.undoButton.active = true;
            this.applyButton.active = true;
            this.doneButton.active = false;
        } else {
            this.undoButton.active = false;
            this.applyButton.active = false;
            this.doneButton.active = true;
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        boolean val = super.mouseClicked(event, doubleClick);
        this.updateFooterButtonState();
        return val;
    }

    @Override
    public void onClose() {
        ScreenUtil.setScreen(this.minecraft, parent);
    }


    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        final int headerHeight = this.layout.getHeaderHeight();

        if(optionsList != null) {
            this.optionsList.setRectangle(
                this.width,
                this.height - headerHeight - this.layout.getFooterHeight(),
                0,
                headerHeight
            );
            this.optionsList.repositionElements();
        }

        this.refreshOptionWidgetVisuals();
    }
}
