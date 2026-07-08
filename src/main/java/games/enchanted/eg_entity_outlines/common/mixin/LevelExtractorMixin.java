package games.enchanted.eg_entity_outlines.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import games.enchanted.eg_entity_outlines.common.render.EntityOutlineExtractor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
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

        Entity entity = entityHitResult.getEntity();
        if(!EntityOutlineExtractor.shouldEntityHaveOutline(entity)) return;

        levelRenderState.blockOutlineRenderState = EntityOutlineExtractor.extractRenderState(camera, levelRenderState, this.minecraft, entity);
    }
}
