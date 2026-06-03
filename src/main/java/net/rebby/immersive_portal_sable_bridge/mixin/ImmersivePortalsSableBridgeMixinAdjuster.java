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

        if (mixinClassName.equals("qouteall.imm_ptl.core.mixin.common.entity_sync.MixinTrackedEntity")){
            if (annotationNode.is(Redirect.class)
                && annotationNode.as(AdjustableRedirectNode.class).getAt().getTarget().equals("Lnet/minecraft/server/network/ServerPlayerConnection;send(Lnet/minecraft/network/protocol/Packet;)V")) {

                return null;
            }

            if (annotationNode.is(Overwrite.class)
                && (handlerNode.name.contains("updatePlayer")
                || handlerNode.name.contains("updatePlayers"))){

                return null;
            }

        }

//        if(mixinClassName.equals("dev.ryanhcode.sable.mixin.entity.entity_sublevel_collision.EntityMixin")) {
//            if(annotationNode.is(Redirect.class)){
//                AdjustableRedirectNode redirectNode = annotationNode.as(AdjustableRedirectNode.class);
//                if(redirectNode.getAt().getTarget().equals("Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;")){
//                    return null;
//                }
//            }
//        }
        if(mixinClassName.equals("dev.ryanhcode.sable.mixin.entity.entity_tracking.TrackedEntityMixin")) return null;

        return annotationNode;
    }
}
