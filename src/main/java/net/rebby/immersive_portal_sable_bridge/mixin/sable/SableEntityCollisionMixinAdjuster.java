package net.rebby.immersive_portal_sable_bridge.mixin.sable;

import com.bawnorton.mixinsquared.adjuster.tools.AdjustableAnnotationNode;
import com.bawnorton.mixinsquared.adjuster.tools.AdjustableRedirectNode;
import com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * @author Bunting_chj
 * Credit this code to the author provided!
 */
public class SableEntityCollisionMixinAdjuster implements MixinAnnotationAdjuster {
    @Override
    public AdjustableAnnotationNode adjust(List<String> targetClassNames, String mixinClassName, MethodNode handlerNode, AdjustableAnnotationNode annotationNode) {
        if(mixinClassName.equals("dev.ryanhcode.sable.mixin.entity.entity_sublevel_collision.EntityMixin")) {
            if(annotationNode.is(Redirect.class)){
                AdjustableRedirectNode redirectNode = annotationNode.as(AdjustableRedirectNode.class);
                if(redirectNode.getAt().getTarget().equals("Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;")){
                    return null;
                }
            }
        }
        if(mixinClassName.equals("dev.ryanhcode.sable.mixin.entity.entity_tracking.TrackedEntityMixin")) return null;
        return annotationNode;
    }


}
