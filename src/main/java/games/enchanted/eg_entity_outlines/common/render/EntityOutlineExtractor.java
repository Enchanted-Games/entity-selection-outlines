package games.enchanted.eg_entity_outlines.common.render;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
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
        return entity instanceof BlockAttachedEntity || entity instanceof ArmorStand || entity instanceof Boat || entity instanceof Minecart;
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
