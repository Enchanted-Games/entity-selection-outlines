package games.enchanted.eg_entity_outlines.common.gui.widget.whitelist;

import net.minecraft.resources.Identifier;

public interface WhitelistEntryProxy {
    void setWhitelistEntryEnabled(Identifier identifier, boolean enabled);

    boolean isEntryEnabled(Identifier identifier);
}
