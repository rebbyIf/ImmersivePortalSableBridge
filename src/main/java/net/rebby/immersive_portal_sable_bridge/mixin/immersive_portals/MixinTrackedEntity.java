package net.rebby.immersive_portal_sable_bridge.mixin.immersive_portals;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChunkMap.TrackedEntity.class, priority = 2000)
public class MixinTrackedEntity {

    @TargetHandler(
            mixin = "qouteall.imm_ptl.core.mixin.common.entity_sync.MixinTrackedEntity",
            name = "ip_updateEntityTrackingStatus"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;chunkPosition()Lnet/minecraft/world/level/ChunkPos;")
    )
    private ChunkPos inWorldChunkPos(Entity entity, Operation<ChunkPos> original){
        final Vec3 pos = entity.position();
        final SubLevel subLevel = Sable.HELPER.getContaining(entity.level(), pos);

        if (subLevel != null) {
            ChunkPos chunkPos = new ChunkPos(BlockPos.containing(subLevel.logicalPose().transformPosition(pos)));
            return chunkPos;
        }
        else return original.call(entity);
    }
}
