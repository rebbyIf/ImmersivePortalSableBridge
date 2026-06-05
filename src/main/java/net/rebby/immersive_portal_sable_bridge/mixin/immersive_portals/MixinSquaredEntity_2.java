package net.rebby.immersive_portal_sable_bridge.mixin.immersive_portals;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, priority = 2000)
public class MixinSquaredEntity_2 {
    @Shadow
    private Level level;

    @Shadow
    private Vec3 position;

    @TargetHandler(
            mixin = "qouteall.imm_ptl.core.mixin.common.collision.MixinEntity",
            name = "onSetPos"
    )
    @ModifyExpressionValue(
            method = "@MixinSquared:Handler",
            at = @At(value = "FIELD", target = "Lqouteall/imm_ptl/core/IPGlobal;teleportationDebugEnabled:Z")
    )
    private boolean setPosOnSubLevel(boolean original, double nx, double ny, double nz){
        return original
            && !(SableCompanion.INSTANCE.isInPlotGrid(level, new Vec3(nx, ny, nz))
                || SableCompanion.INSTANCE.isInPlotGrid(level, position));
    }
}
