package net.aeronica.mods.fourteen.handlers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.aeronica.mods.fourteen.caps.stages.ServerStageAreaProvider;
import net.aeronica.mods.fourteen.render.StageAreaRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class RenderHandler
{
    static final Minecraft mc = Minecraft.getInstance();

    private RenderHandler() { /* NOP */ }

    public static void renderLast(MatrixStack pMatrixStack, IRenderTypeBuffer.Impl pBuffer, double pCamX, double pCamY, double pCamZ)
    {
        final PlayerEntity player = mc.player;
        World level;
        if (player == null ||(level = player.level) == null) return;

        Vector3d camera = mc.gameRenderer.getMainCamera().getPosition();
        double camX = camera.x;
        double camY = camera.y;
        double camZ = camera.z;

        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();

        ServerStageAreaProvider.getServerStageAreas(level).ifPresent(
                areas -> {
                    if (GraphicsFanciness.FABULOUS == mc.options.graphicsMode)
                    areas.getStageAreas().forEach(
                            (area) -> {
                                IVertexBuilder vertexBuilder1 = pBuffer.getBuffer(RenderType.lightning());
                                StageAreaRenderer.renderFaces(pMatrixStack, vertexBuilder1, area.getAreaAABB(), camX, camY, camZ, 1F, 0F, 1F, 0.1F);
                            });

                    areas.getStageAreas().forEach(
                            (area) ->
                            {
                                IVertexBuilder vertexBuilder2 = pBuffer.getBuffer(RenderType.lines());
                                VoxelShape cubeShape = VoxelShapes.create(area.getAreaAABB());
                                StageAreaRenderer.renderEdges(pMatrixStack, vertexBuilder2, cubeShape, camX, camY, camZ, 1F, 0F, 1F, 1F);
                            });

                    areas.getStageAreas().forEach(
                            (area) ->
                            {
                                StageAreaRenderer.renderFloatingText(new StringTextComponent(area.getTitle()),
                                                                     area.getAreaAABB().getCenter(),
                                                                     pMatrixStack,
                                                                     pBuffer, mc.gameRenderer.getMainCamera(), -1);

                                StageAreaRenderer.renderFloatingText(new StringTextComponent("Audience Spawn"),
                                                                     new Vector3d(area.getAudienceSpawn().getX() + 0.5, area.getAudienceSpawn().getY() + 1.5, area.getAudienceSpawn().getZ() + 0.5),
                                                                     pMatrixStack,
                                                                     pBuffer, mc.gameRenderer.getMainCamera(), -1);

                                StageAreaRenderer.renderFloatingText(new StringTextComponent("Performer Spawn"),
                                                                     new Vector3d(area.getPerformerSpawn().getX() + 0.5, area.getPerformerSpawn().getY() + 1.5, area.getPerformerSpawn().getZ() + 0.5),
                                                                     pMatrixStack,
                                                                     pBuffer, mc.gameRenderer.getMainCamera(), -1);
                            });
                });
    }
}
