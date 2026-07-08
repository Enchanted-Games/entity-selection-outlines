package games.enchanted.eg_entity_outlines.common.render;

import games.enchanted.eg_entity_outlines.common.config.ConfigOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class EntityOutlineExtractor {
    public static boolean shouldEntityHaveOutline(Entity entity) {
        if(!ConfigOptions.MOD_ENABLED_OPTION.getValue()) return false;

        if(entity instanceof BlockAttachedEntity && ConfigOptions.OUTLINE_BLOCK_ATTACHED.getValue()) {
            return true;
        } else if(entity instanceof ArmorStand && ConfigOptions.OUTLINE_ARMOUR_STANDS.getValue()) {
            return true;
        } else if(entity instanceof Boat && ConfigOptions.OUTLINE_BOATS.getValue()) {
            return true;
        } else if(entity instanceof Minecart && ConfigOptions.OUTLINE_MINECARTS.getValue()) {
            return true;
        } else if(entity instanceof EndCrystal && ConfigOptions.OUTLINE_END_CRYSTALS.getValue()) {
            return true;
        }

        return ConfigOptions.OUTLINE_EVERYTHING.getValue();
    }

    public static @Nullable BlockOutlineRenderState extractRenderState(Camera camera, LevelRenderState levelRenderState, Minecraft minecraft, Entity entity) {
        if(minecraft.level == null) return null;

        BlockPos entityBlockPos = entity.blockPosition();
        boolean highContrast = minecraft.options.highContrastBlockOutline().get();
        float partialTicks = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(!minecraft.level.tickRateManager().isEntityFrozen(entity));

        Vec3 latestPosition = entity.position();
        Vec3 currentPosition = entity.getPosition(partialTicks);
        Vec3 offset = currentPosition.subtract(latestPosition);
        AABB bb = entity.getBoundingBox().move(offset);
        VoxelShape shape = Shapes.box(
            bb.minX - entityBlockPos.getX(),
            bb.minY - entityBlockPos.getY(),
            bb.minZ - entityBlockPos.getZ(),
            bb.maxX - entityBlockPos.getX(),
            bb.maxY - entityBlockPos.getY(),
            bb.maxZ - entityBlockPos.getZ()
        );

        return new BlockOutlineRenderState(entityBlockPos, true, highContrast, shape);
    }
}
