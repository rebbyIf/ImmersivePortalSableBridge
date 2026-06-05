package net.rebby.immersive_portal_sable_bridge.mixin.immersive_portals;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import qouteall.imm_ptl.core.chunk_loading.EntitySync;

@Mixin(EntitySync.class)
public class MixinEntitySync {
    @WrapOperation(
            method = "lambda$tick$2",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;chunkPosition()Lnet/minecraft/world/level/ChunkPos;")
    )
    private static ChunkPos projectedChunkPos(Entity entity, Operation<ChunkPos> original){
        if (Sable.HELPER.isInPlotGrid(entity)) {
            BlockPos pos = BlockPos.containing(Sable.HELPER.projectOutOfSubLevel(entity.level(), entity.position()));
            return new ChunkPos(pos);
        }
        return original.call(entity);
    }
}
