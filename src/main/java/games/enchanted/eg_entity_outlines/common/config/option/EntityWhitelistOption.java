package games.enchanted.eg_entity_outlines.common.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import games.enchanted.eg_entity_outlines.common.Logging;
import games.enchanted.eg_entity_outlines.common.config.EntityWhitelist;
import org.jspecify.annotations.Nullable;

public class EntityWhitelistOption extends ConfigOption<EntityWhitelist> {
    public EntityWhitelistOption(EntityWhitelist defaultValue, String jsonKey) {
        super(defaultValue, defaultValue, jsonKey);
    }

    @Override
    public @Nullable JsonElement toJson() {
        DataResult<JsonElement> encoded = EntityWhitelist.CODEC.encodeStart(JsonOps.INSTANCE, this.getValue());

        if(encoded.isSuccess() && encoded.result().isPresent()) {
            return encoded.result().get();
        }

        if(encoded.isError() && encoded.error().isPresent()) {
            Logging.error("Failed to encode config option '{}'.\n{}", this.getJsonKey(), encoded.error().get().message());
        } else {
            Logging.error("Failed to encode config option '{}'.\n{}", this.getJsonKey(), "Couldn't retrieve error message");
        }

        return null;
    }

    @Override
    public void fromJson(JsonObject json) {
        if(!json.has(getJsonKey())) {
            this.setValueOrPending(this.getDefaultValue());
            return;
        }

        JsonElement value = json.get(getJsonKey());
        DataResult<Pair<EntityWhitelist, JsonElement>> result = EntityWhitelist.CODEC.decode(JsonOps.INSTANCE, value);

        if(result.isSuccess() && result.result().isPresent()) {
            this.setValueOrPending(result.result().get().getFirst());
            return;
        }

        if(result.isError() && result.error().isPresent()) {
            Logging.error("Failed to decode config option '{}'.\n{}", this.getJsonKey(), result.error().get().message());
        } else {
            Logging.error("Failed to decode config option '{}'.\n{}", this.getJsonKey(), "Couldn't retrieve error message");
        }

        this.setValueOrPending(this.getDefaultValue());
    }
}
