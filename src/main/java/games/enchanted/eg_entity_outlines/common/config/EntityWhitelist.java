package games.enchanted.eg_entity_outlines.common.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public record EntityWhitelist(List<Identifier> whitelistedTypes, List<Identifier> unregisteredWhitelistedTypes) {
    public static final Codec<EntityWhitelist> CODEC = RecordCodecBuilder.create(i -> i
        .group(
            Identifier.CODEC.listOf().fieldOf("types").forGetter(EntityWhitelist::whitelistedTypes),
            Identifier.CODEC.listOf().fieldOf("unknown_types_during_last_session").forGetter(EntityWhitelist::unregisteredWhitelistedTypes)
        )
        .apply(
            i,
            EntityWhitelist::createAndVerify
        )
    );

    public static EntityWhitelist createAndVerify(List<Identifier> whitelistedTypes, List<Identifier> unregisteredWhitelistedTypes) {
        List<Identifier> verifiedWhitelistedTypes = new ArrayList<>();
        List<Identifier> verifiedUnregisteredWhitelistedTypes = new ArrayList<>();

        for (Identifier type : whitelistedTypes) {
            if(isValidEntityType(type)) {
                verifiedWhitelistedTypes.add(type);
                continue;
            }
            verifiedUnregisteredWhitelistedTypes.add(type);
        }
        for (Identifier type : unregisteredWhitelistedTypes) {
            if(isValidEntityType(type)) {
                verifiedWhitelistedTypes.add(type);
                continue;
            }
            verifiedUnregisteredWhitelistedTypes.add(type);
        }

        return new EntityWhitelist(verifiedWhitelistedTypes, verifiedUnregisteredWhitelistedTypes);
    }

    private static boolean isValidEntityType(Identifier id) {
        return BuiltInRegistries.ENTITY_TYPE.containsKey(id);
    }

    public static EntityWhitelist create(
        //? if minecraft: >= 26.3 {
        List<ResourceKey<EntityType<?>>> whitelistedTypes
        //? } else {
        /*List<EntityType<?>> whitelistedTypes
        *///? }
    ) {
        return new EntityWhitelist(
            whitelistedTypes.stream()
                //? if minecraft: >= 26.3 {
                .map(ResourceKey::identifier)
                //? } else {
                /*.map(BuiltInRegistries.ENTITY_TYPE::getKey)
                *///? }
                .toList(),
            List.of()
        );
    }

    public boolean containsEntity(EntityType<?> type) {
        Identifier identifier = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        return containsEntity(identifier);
    }

    public boolean containsEntity(ResourceKey<EntityType<?>> typeKey) {
        Identifier identifier = typeKey.identifier();
        return containsEntity(identifier);
    }

    public boolean containsEntity(Identifier entityId) {
        return this.whitelistedTypes.contains(entityId) || this.unregisteredWhitelistedTypes.contains(entityId);
    }

    public List<Identifier> allEntries() {
        List<Identifier> list = new ArrayList<>();
        list.addAll(this.whitelistedTypes);
        list.addAll(this.unregisteredWhitelistedTypes);
        return list;
    }
}
