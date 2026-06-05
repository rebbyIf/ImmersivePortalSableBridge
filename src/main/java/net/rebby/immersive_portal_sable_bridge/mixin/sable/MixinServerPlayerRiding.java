package net.rebby.immersive_portal_sable_bridge.mixin.sable;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import qouteall.imm_ptl.core.ducks.IEPlayerPositionLookS2CPacket;

@Mixin(value = ServerPlayer.class, priority = 2000)
public abstract class MixinServerPlayerRiding {
    @Shadow
    public abstract ServerLevel serverLevel();

    @TargetHandler(
            mixin = "dev.ryanhcode.sable.mixin.entity.entity_rotations_and_riding.ServerPlayerMixin",
            name = "sable$adjustTeleportPacket"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V")
    )
    private void setDimensionBeforeSend(ServerGamePacketListenerImpl connection, Packet packet, Operation<Void> original){
        ((IEPlayerPositionLookS2CPacket) packet).ip_setPlayerDimension(this.serverLevel().dimension());
        original.call(connection, packet);
    }
}
