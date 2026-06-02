package net.rebby.immersive_portal_sable_bridge.mixin.immersive_portals;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import qouteall.imm_ptl.core.ducks.IETrackedEntity;
import qouteall.imm_ptl.core.network.PacketRedirection;

@SuppressWarnings({"JavadocReference", "resource"})
@Mixin(value = ChunkMap.TrackedEntity.class, priority = 1001)
public abstract class MixinSquaredTrackedEntity implements IETrackedEntity {

    @Shadow
    @Final
    Entity entity;

    @WrapOperation(
            method = "broadcast(Lnet/minecraft/network/protocol/Packet;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerConnection;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void onSendToOtherNearbyPlayers(
            ServerPlayerConnection entityTrackingListener, Packet<?> packet, Operation<Void> original
    ) {
        PacketRedirection.withForceRedirect(
                ((ServerLevel) entity.level()),
                () -> {
                    entityTrackingListener.send(packet);
                    original.call(entityTrackingListener, packet);
                }
        );
    }
}
