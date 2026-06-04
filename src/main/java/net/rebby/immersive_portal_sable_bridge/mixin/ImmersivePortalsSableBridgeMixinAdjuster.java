package net.rebby.immersive_portal_sable_bridge.mixin;

import com.bawnorton.mixinsquared.adjuster.tools.AdjustableAnnotationNode;
import com.bawnorton.mixinsquared.adjuster.tools.AdjustableRedirectNode;
import com.bawnorton.mixinsquared.adjuster.tools.AdjustableWrapOperationNode;
import com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

public class ImmersivePortalsSableBridgeMixinAdjuster implements MixinAnnotationAdjuster {
    @Override
    public AdjustableAnnotationNode adjust(List<String> targetClassNames, String mixinClassName, MethodNode handlerNode, AdjustableAnnotationNode annotationNode) {
        if (mixinClassName.equals("qouteall.imm_ptl.core.mixin.common.collision.MixinEntity")
            && annotationNode.is(Redirect.class)
            && handlerNode.name.contains("redirectHandleCollisions")
            ) {
            return null;
        }
        if(mixinClassName.equals("dev.ryanhcode.sable.mixin.entity.entity_tracking.TrackedEntityMixin")) return null;

        return annotationNode;
    }
}
