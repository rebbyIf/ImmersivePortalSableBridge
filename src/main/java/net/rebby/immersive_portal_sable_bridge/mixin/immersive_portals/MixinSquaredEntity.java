package net.rebby.immersive_portal_sable_bridge.mixin.immersive_portals;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.EntityMovementExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.imm_ptl.core.IPGlobal;
import qouteall.imm_ptl.core.api.ImmPtlEntityExtension;
import qouteall.imm_ptl.core.collision.PortalCollisionHandler;
import qouteall.imm_ptl.core.ducks.IEEntity;
import qouteall.q_misc_util.my_util.CountDownInt;

@Mixin(value = Entity.class, priority = 2000)
public abstract class MixinSquaredEntity implements IEEntity, ImmPtlEntityExtension {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Unique
    private static final CountDownInt IMM_PTL_LOG_COUNTER = new CountDownInt(20);

    @TargetHandler(
            mixin = "dev.ryanhcode.sable.mixin.entity.entity_sublevel_collision.EntityMixin",
            name = "sable$moveInject"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipCollisionSetter(final Entity instance, final boolean bl, final Vec3 arg, final Operation<Void> original, CallbackInfo cir){
        if(((EntityMovementExtension) this).sable$getCollisionInfo() == null) {
            original.call(instance, bl, arg);
            cir.cancel();
        }
    }

    @WrapOperation(
            method = "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 redirectHandleCollisions(Entity entity, Vec3 attemptedMove, Operation<Vec3> original) {

        PortalCollisionHandler ip_portalCollisionHandler = ip_getPortalCollisionHandler();

        if (!IPGlobal.enableServerCollision) {
            if (!entity.level().isClientSide()) {
                if (entity instanceof Player) {
                    return attemptedMove;
                }
                else {
                    return Vec3.ZERO;
                }
            }
        }

        if (attemptedMove.lengthSqr() > 60 * 60) {
            // avoid loading too many chunks in collision calculation and lag the server
            if (IMM_PTL_LOG_COUNTER.tryDecrement()) {
                LOGGER.error(
                        "[ImmPtl] Skipping collision calculation because entity moves too fast {} {} {}",
                        entity, attemptedMove, entity.level().getGameTime(),
                        new Throwable()
                );
            }

            return Vec3.ZERO;
        }

        if (!IPGlobal.crossPortalCollision
                || ip_portalCollisionHandler == null
                || !ip_portalCollisionHandler.hasCollisionEntry()
        ) {
            return original.call(entity, attemptedMove);
        }

        Vec3 result = ip_portalCollisionHandler.handleCollision(
                (Entity) (Object) this, attemptedMove
        );

        if (result.lengthSqr() > 20 * 20) {
            if (IMM_PTL_LOG_COUNTER.tryDecrement()) {
                LOGGER.error(
                        "[ImmPtl] cross portal collision result too large {} {} {}",
                        this, attemptedMove, result
                );
            }
            return Vec3.ZERO;
        }

        return result;
    }
}
