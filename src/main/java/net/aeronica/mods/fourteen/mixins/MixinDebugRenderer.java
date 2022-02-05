package net.aeronica.mods.fourteen.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.aeronica.mods.fourteen.handlers.RenderHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer
{

    /**
     * Inserts a hook into the client side DebugRenderer so we can do our render world last and still have access to
     * the render type buffers before they are batch ended.
     * @param pMatrixStack  The provided MatrixStack
     * @param pBuffer       The provided render type buffers
     * @param pCamX         The camera X position
     * @param pCamY         The camera Y position
     * @param pCamZ         The camera Z position
     * @param ci            Ignored
     */
    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;DDD)V", at = @At("HEAD"))
    public void renderCallback(MatrixStack pMatrixStack, IRenderTypeBuffer.Impl pBuffer, double pCamX, double pCamY, double pCamZ, CallbackInfo ci)
    {
        RenderHandler.renderLast(pMatrixStack, pBuffer, pCamX, pCamY, pCamZ);
    }
}
