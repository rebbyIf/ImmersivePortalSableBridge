package net.rebby.immersive_portal_sable_bridge.mixin.immersive_portals;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.companion.SableCompanion;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import qouteall.imm_ptl.core.chunk_loading.ImmPtlChunkTickets;

@Mixin(ImmPtlChunkTickets.class)
public class MixinChunkTickets {
    @WrapOperation(
            method = "flushThrottling",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;containsKey(J)Z"),
            remap = false
    )
    private static boolean cancelSubLevel(Long2ObjectOpenHashMap<ImmPtlChunkTickets.ChunkTicketInfo> chunkMap, long chunkPos, Operation<Boolean> original,
                                          @Local(argsOnly = true)ServerLevel level){
        if (!original.call(chunkMap, chunkPos)) return false;
        return !SableCompanion.INSTANCE.isInPlotGrid(level, new ChunkPos(chunkPos));
    }
}
