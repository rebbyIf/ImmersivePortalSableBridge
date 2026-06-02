package net.rebby.immersive_portal_sable_bridge.mixin.sable;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.sublevel.system.SubLevelTrackingSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import qouteall.imm_ptl.core.chunk_loading.ImmPtlChunkTracking;

import java.util.List;
import java.util.UUID;

/**
 * @author Bunting_chj
 */
@Mixin(SubLevelTrackingSystem.class)
public class SublevelTrackingSystemMixin {

    @Shadow
    @Final
    private ServerLevel level;

    @WrapOperation(
            method = "*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getPlayerByUUID(Ljava/util/UUID;)Lnet/minecraft/world/entity/player/Player;")
    )
    private Player playerFromPortal(ServerLevel serverLevel, UUID uuid, Operation<Player> original){
        Player result;
        if((result = original.call(serverLevel, uuid)) != null) return result;
        result = serverLevel.getServer().getPlayerList().getPlayer(uuid);
        return result;
    }

    @WrapMethod(
            method = "shouldLoad"
    )
    private boolean shouldLoadPortal(Player player, Vector3dc entityPosition, Operation<Boolean> original){
        if(original.call(player, entityPosition)) return true;
        if(!(player instanceof ServerPlayer serverPlayer)) return false;
        ChunkPos chunkPos = new ChunkPos(BlockPos.containing(entityPosition.x(), entityPosition.y(), entityPosition.z()));
        return ImmPtlChunkTracking.isPlayerWatchingChunk(serverPlayer, level.dimension(), chunkPos.x, chunkPos.z);
    }

    @WrapOperation(
            method = "collectPlayers",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;players()Ljava/util/List;")
    )
    private List<?> collectAllPlayers(ServerLevel serverLevel, Operation<List<ServerPlayer>> original){
        original.call(serverLevel);
        return serverLevel.getServer().getPlayerList().getPlayers();
    }
}
