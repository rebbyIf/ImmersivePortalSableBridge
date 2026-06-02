package net.rebby.immersive_portal_sable_bridge.mixin.immersive_portals;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.SectionPos;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.Validate;
import org.spongepowered.asm.mixin.Mixin;
import qouteall.imm_ptl.core.McHelper;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * @author Bunting_chj, rebbyIf
 */
@Mixin(McHelper.class)
public class McHelperMixin {

    @WrapMethod(method="traverseEntities")
    private static <T extends Entity, R> R traverseEntities(
            Class<T> entityClass, LevelEntityGetter<Entity> entityLookup, int chunkXStart, int chunkXEnd, int chunkYStart, int chunkYEnd, int chunkZStart, int chunkZEnd, Function<T, R> function, Operation<R> original
    ) {
        Validate.isTrue(chunkXEnd >= chunkXStart);
        Validate.isTrue(chunkYEnd >= chunkYStart);
        Validate.isTrue(chunkZEnd >= chunkZStart);
        Validate.isTrue(chunkXEnd - chunkXStart < 1000, "range too big");
        Validate.isTrue(chunkZEnd - chunkZStart < 1000, "range too big");

        EntityTypeTest<Entity, T> typeFilter = EntityTypeTest.forClass(entityClass);

        AABB boundingBox = new AABB(
                SectionPos.sectionToBlockCoord(chunkXStart),
                SectionPos.sectionToBlockCoord(chunkYStart),
                SectionPos.sectionToBlockCoord(chunkZStart),
                SectionPos.sectionToBlockCoord(chunkXEnd + 1),
                SectionPos.sectionToBlockCoord(chunkYEnd + 1),
                SectionPos.sectionToBlockCoord(chunkZEnd + 1)
        );

        final AtomicReference<R> result = new AtomicReference<>();

        entityLookup.get(typeFilter, boundingBox,entity -> {
            SectionPos sectionPos = SectionPos.of(entity.position());
            if (sectionPos.x() < chunkXStart || sectionPos.x() > chunkXEnd) return AbortableIterationConsumer.Continuation.CONTINUE;
            if (sectionPos.y() < chunkYStart || sectionPos.y() > chunkYEnd) return AbortableIterationConsumer.Continuation.CONTINUE;
            if (sectionPos.z() < chunkZStart || sectionPos.z() > chunkZEnd) return AbortableIterationConsumer.Continuation.CONTINUE;
            R tempResult = function.apply(entityClass.cast(entity));
            if (tempResult != null) {
                result.set(tempResult);
                return AbortableIterationConsumer.Continuation.ABORT;
            }
            return AbortableIterationConsumer.Continuation.CONTINUE;
        });
        return result.get();
    }
}
