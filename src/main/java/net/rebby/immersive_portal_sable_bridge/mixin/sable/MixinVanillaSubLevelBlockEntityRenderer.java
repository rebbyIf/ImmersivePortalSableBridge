package net.rebby.immersive_portal_sable_bridge.mixin.sable;

import dev.ryanhcode.sable.mixinhelpers.sublevel_render.vanilla.VanillaSubLevelBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(VanillaSubLevelBlockEntityRenderer.class)
public class MixinVanillaSubLevelBlockEntityRenderer {
    @Redirect(
            method = {"renderSingleBE", "lambda$renderSingleBE$0"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderBuffers;bufferSource()Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;")
    )
    private MultiBufferSource.BufferSource bufferSource(RenderBuffers instance) {
        return ((LevelRendererAccessor) Minecraft.getInstance().levelRenderer).getRenderBuffers().bufferSource();
    }
}
