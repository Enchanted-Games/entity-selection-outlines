package games.enchanted.eg_entity_outlines.common.gui.widget.scroll;

import games.enchanted.eg_entity_outlines.common.Logging;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class OptionsList extends VerticalScrollContainerWidget<OptionsList.Entry> {
    public static final Identifier LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/menu_list_background.png");
    public static final Identifier INWORLD_LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
    public static final int LIST_BACKGROUND_TEXTURE_SIZE = 32;

    public static final int DEFAULT_CHILD_HEIGHT = 25;
    public static final int DEFAULT_CHILD_WIDTH = 150;
    public static final int ROW_WIDTH = 310;

    @Nullable private OptionEntry lastEntry = null;

    public OptionsList(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> visitor) {
        this.children().forEach(child -> child.widgetChildren().forEach(visitor));
    }


    public WidgetPosition addOption(AbstractWidget child) {
        if(this.lastEntry != null) {
            this.lastEntry.setSecondChild(child);
            this.lastEntry = null;
            return new WidgetPosition(this.children().size() - 1, true);
        }
        child.setWidth(DEFAULT_CHILD_WIDTH);
        OptionEntry entry = new OptionEntry(child, DEFAULT_CHILD_HEIGHT);
        this.lastEntry = entry;
        this.addChild(entry);
        return new WidgetPosition(this.children().size() - 1, false);
    }

    public WidgetPosition addBigOption(AbstractWidget child) {
        return this.addBigOption(child, DEFAULT_CHILD_HEIGHT);
    }

    public WidgetPosition addBigOption(AbstractWidget child, int height) {
        this.lastEntry = null;
        child.setWidth(ROW_WIDTH);
        this.addChild(new OptionEntry(child, height));
        return new WidgetPosition(this.children().size() - 1, false);
    }

    public void addCategoryHeader(Component header) {
        this.lastEntry = null;
        this.addChild(new CategoryHeaderEntry(header));
    }

    public void addGroupName(Component header) {
        this.lastEntry = null;
        this.addChild(new GroupTitleEntry(header));
    }

    public void addSpacer(int height) {
        this.lastEntry = null;
        this.addChild(new SpacerEntry(height));
    }


    public void setWidgetVisibility(WidgetPosition position, boolean visible) {
        Entry entry = this.children().get(position.entryIndex());
        if(!(entry instanceof OptionEntry optionEntry)) {
            Logging.warn("Cannot set visibility of non-option widget");
            return;
        }
        if(position.secondary() && optionEntry.secondChild != null) {
            optionEntry.secondChild.visible = visible;
        } else {
            optionEntry.child.visible = visible;
        }
    }


    @Override
    public int getRowWidth() {
        return ROW_WIDTH;
    }

    @Override
    public int getRowLeft() {
        return this.getMiddleX() - (ROW_WIDTH / 2);
    }

    @Override
    public int getRowRight() {
        return this.getMiddleX() + (ROW_WIDTH / 2);
    }

    private int getMiddleX() {
        return this.getX() + (this.getWidth() / 2);
    }

    @Override
    protected int scrollBarX() {
        return getRowRight() + 8;
    }

    @Override
    protected void renderBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        boolean notInWorld = Minecraft.getInstance().level == null;

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            notInWorld ? LIST_BACKGROUND : INWORLD_LIST_BACKGROUND,
            this.getX(),
            this.getY(),
            this.getRight(),
            this.getBottom() + (int)this.scrollAmount(),
            this.getWidth(),
            this.getHeight(),
            LIST_BACKGROUND_TEXTURE_SIZE,
            LIST_BACKGROUND_TEXTURE_SIZE
        );

        int separatorTextureWidth = 32;
        int separatorTextureHeight = 2;
        int separatorHeight = 2;

        Identifier headerSeparator = notInWorld ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            headerSeparator,
            this.getX(),
            this.getY() - 2,
            0.0F,
            0.0F,
            this.getWidth(),
            separatorTextureHeight,
            separatorTextureWidth,
            separatorHeight
        );

        Identifier footerSeparator = notInWorld ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            footerSeparator,
            this.getX(),
            this.getBottom(),
            0.0F,
            0.0F,
            this.getWidth(),
            separatorTextureHeight,
            separatorTextureWidth,
            separatorHeight
        );
    }

    @Override
    protected void narrateChildPosition(NarrationElementOutput output, Entry child) {
    }

    public static abstract class Entry extends Child {
        private static final int ACCENT_BOTTOMMOST_OFFSET = 4;

        Entry() {
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
        }

        protected int accentTop() {
            return this.getY();
        }

        protected int accentBottom() {
            return this.getY() + this.getHeight();
        }

        @Override
        protected int height() {
            return DEFAULT_CHILD_HEIGHT;
        }

        @Override
        public List<? extends AbstractWidget> widgetChildren() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratableChildren() {
            return List.of();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        void updateNarration(NarrationElementOutput output) {
            List<? extends NarratableEntry> narratableChildren = this.narratableChildren();
            Screen.NarratableSearchResult result = Screen.findNarratableWidget(narratableChildren, this.lastNarratable);
            if (result == null) return;

            if (result.priority().isTerminal()) {
                this.lastNarratable = result.entry();
            }

            result.entry().updateNarration(output.nest());
        }
    }

    static class OptionEntry extends Entry {
        final AbstractWidget child;
        @Nullable AbstractWidget secondChild;
        boolean lastInCategory = false;
        final int height;

        OptionEntry(AbstractWidget widget, int height) {
            super();
            setMargins(new Margin(0, 0));
            this.child = widget;
            this.height = height;
        }

        void setSecondChild(AbstractWidget child) {
            this.secondChild = child;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            super.extractContent(graphics, mouseX, mouseY, hovered, partialTick);

            this.child.setX(this.getContentX());
            this.child.setY(this.getContentYMiddle() - this.child.getHeight() / 2);
            this.child.extractRenderState(graphics, mouseX, mouseY, partialTick);

            if(this.secondChild == null) return;
            this.secondChild.setX(this.getContentRight() - this.secondChild.getWidth());
            this.secondChild.setY(this.getContentYMiddle() - this.secondChild.getHeight() / 2);
            this.secondChild.extractRenderState(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        protected int accentBottom() {
            if(!this.lastInCategory) return super.accentBottom();
            return super.accentBottom() - Entry.ACCENT_BOTTOMMOST_OFFSET;
        }

        @Override
        public List<? extends AbstractWidget> widgetChildren() {
            if(secondChild != null) return List.of(child, secondChild);
            return List.of(child);
        }

        @Override
        public List<? extends NarratableEntry> narratableChildren() {
            if(secondChild != null) return List.of(child, secondChild);
            return List.of(child);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            if(secondChild != null) return List.of(child, secondChild);
            return List.of(child);
        }

        @Override
        protected int height() {
            if(!this.child.visible && !(this.secondChild != null && this.secondChild.visible)) {
                return 0;
            }
            return this.height;
        }
    }

    static class CategoryHeaderEntry extends Entry {
        private static final int LEFT_TEXT_OFFSET = 1;

        final Font font = Minecraft.getInstance().font;
        final Component title;

        CategoryHeaderEntry(Component header) {
            super();
            setMargins(new Margin(8, 2, LEFT_TEXT_OFFSET, 0));
            this.title = header;
        }

        protected int getTextColour() {
            return CommonColors.WHITE;
        }

        @Override
        protected int height() {
            return this.font.lineHeight;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            super.extractContent(graphics, mouseX, mouseY, hovered, partialTick);

            graphics.centeredText(
                this.font,
                this.title,
                this.getContentX() + (this.getContentWidth() / 2),
                this.getContentY(),
                this.getTextColour()
            );
        }
    }

    static class GroupTitleEntry extends CategoryHeaderEntry {
        GroupTitleEntry(Component header) {
            super(header);
            setMargins(new Margin(4, 0, 14, 0));
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            super.extractContent(graphics, mouseX, mouseY, hovered, partialTick);

            graphics.text(
                this.font,
                "-",
                this.getContentX() - this.font.width("- "),
                this.getContentY(),
                this.getTextColour()
            );
        }

        @Override
        protected int getTextColour() {
            return CommonColors.LIGHTER_GRAY;
        }
    }

    static class SpacerEntry extends Entry {
        final int height;

        SpacerEntry(int height) {
            super();
            this.setMargins(new Margin(0, 0));
            this.height = height;
        }

        @Override
        protected int height() {
            return this.height;
        }
    }

    public record WidgetPosition(int entryIndex, boolean secondary) {
    }
}
