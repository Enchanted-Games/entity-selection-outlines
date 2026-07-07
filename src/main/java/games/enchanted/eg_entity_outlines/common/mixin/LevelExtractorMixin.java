package games.enchanted.eg_entity_outlines.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelExtractor.class)
public class LevelExtractorMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/LevelRenderState;blockOutlineRenderState:Lnet/minecraft/client/renderer/state/level/BlockOutlineRenderState;", opcode = Opcodes.PUTFIELD),
        method = "extractBlockOutline"
    )
    private void eg_entity_outlines$extractEntityOutlineState(LevelRenderState instance, BlockOutlineRenderState value, Operation<Void> original, Camera camera, LevelRenderState levelRenderState) {
        original.call(instance, value);

        if(!(this.minecraft.hitResult instanceof EntityHitResult entityHitResult)) return;
        if(this.minecraft.level == null) return;

        Entity entity = entityHitResult.getEntity();
        BlockPos entityBlockPos = entity.blockPosition();
        boolean highContrast = this.minecraft.options.highContrastBlockOutline().get();
        float partialTicks = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(!this.minecraft.level.tickRateManager().isEntityFrozen(entity));

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

        BlockOutlineRenderState state = new BlockOutlineRenderState(entityBlockPos, true, highContrast, shape);
        levelRenderState.blockOutlineRenderState = state;
    }
}
