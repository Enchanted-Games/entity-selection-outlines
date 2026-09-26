package games.enchanted.eg_entity_outlines.common.gui.widget.option;

import games.enchanted.eg_entity_outlines.common.config.option.ConfigOption;

public interface OptionWidget<T extends ConfigOption<?>> {
    T getOption();
    void refreshValue();
    default void refreshVisual() {}
    void onChange(OnChange changeCallback);

    @FunctionalInterface
    interface OnChange {
        void changed();
    }
}
