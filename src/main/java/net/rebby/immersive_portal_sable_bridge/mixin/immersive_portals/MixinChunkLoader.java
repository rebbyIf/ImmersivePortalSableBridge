package net.rebby.immersive_portal_sable_bridge.mixin.immersive_portals;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import qouteall.imm_ptl.core.chunk_loading.ChunkLoader;

@Mixin(ChunkLoader.class)
public abstract class MixinChunkLoader {
    @Shadow
    @Final
    private ResourceKey<Level> dimension;

    @Shadow
    @Final
    private int x;

    @Shadow
    @Final
    private int radius;

    @Shadow
    @Final
    private int z;

    @WrapMethod(
            method = "foreachChunkPos"
    )
    private void forSublevelChunkPos(ChunkLoader.ChunkPosConsumer func, Operation<Void> original){
        original.call(func);
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        Level level = server.getLevel(dimension);
        if (level == null) return;
        int minBlockX = SectionPos.sectionToBlockCoord(x - radius);
        int minBlockZ = SectionPos.sectionToBlockCoord(z - radius);
        int maxBlockX = SectionPos.sectionToBlockCoord(x + radius + 1);
        int maxBlockZ = SectionPos.sectionToBlockCoord(z + radius + 1);
        BoundingBox3d bounds = new BoundingBox3d(minBlockX, level.getMinBuildHeight(), minBlockZ, maxBlockX, level.getMaxBuildHeight(), maxBlockZ);
        for (SubLevelAccess subLevel : SableCompanion.INSTANCE.getAllIntersecting(level, bounds)) {
            if (!(subLevel instanceof SubLevel subLevelImpl)) continue;
            Vec3 sublevelPos = JOMLConversion.toMojang(subLevelImpl.logicalPose().position());
            int distX = SectionPos.posToSectionCoord(sublevelPos.x) - x;
            int distZ = SectionPos.posToSectionCoord(sublevelPos.z) - z;
            int dist = Math.max(Math.abs(distX), Math.abs(distZ));
            subLevelImpl.getPlot().getLoadedChunks().forEach(
                    it ->  {
                        ChunkPos pos = it.getPos();
                        func.consume(dimension, pos.x, pos.z, dist);
                    }
            );
        }
    }

    @WrapMethod(
            method = "getChunkNum"
    )
    private int addSubLevelChunks(Operation<Integer> original){
        int result = original.call();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return result;
        Level level = server.getLevel(dimension);
        if (level == null) return result;
        int minBlockX = SectionPos.sectionToBlockCoord(x - radius);
        int minBlockZ = SectionPos.sectionToBlockCoord(z - radius);
        int maxBlockX = SectionPos.sectionToBlockCoord(x + radius + 1);
        int maxBlockZ = SectionPos.sectionToBlockCoord(z + radius + 1);
        BoundingBox3d bounds = new BoundingBox3d(minBlockX, level.getMinBuildHeight(), minBlockZ, maxBlockX, level.getMaxBuildHeight(), maxBlockZ);
        for (SubLevelAccess subLevel : SableCompanion.INSTANCE.getAllIntersecting(level, bounds)) {
            if (!(subLevel instanceof ServerSubLevel serverSubLevel)) continue;
            result += serverSubLevel.getPlot().getLoadedChunks().size();
        }
        return result;
    }
}
