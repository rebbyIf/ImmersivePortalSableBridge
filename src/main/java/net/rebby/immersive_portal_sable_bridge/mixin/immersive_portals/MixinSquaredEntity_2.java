package net.rebby.immersive_portal_sable_bridge.mixin.immersive_portals;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.EntityMovementExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, priority = 2000)
public class MixinSquaredEntity_2 {
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
}
