package net.aeronica.mods.fourteen.network.messages;


import net.aeronica.mods.fourteen.caps.stages.ServerStageAreaProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class StageAreaSyncMessage extends AbstractMessage<StageAreaSyncMessage>
{
    private static final Logger LOGGER = LogManager.getLogger(StageAreaSyncMessage.class);
    private final INBT stageAreaNbt;

    public StageAreaSyncMessage()
    {
        this.stageAreaNbt = new CompoundNBT();
    }

    public StageAreaSyncMessage(final INBT stageAreaNbt)
    {
        this.stageAreaNbt = stageAreaNbt;
    }

    @Override
    public StageAreaSyncMessage decode(final PacketBuffer buffer)
    {
        final CompoundNBT compoundNBT = buffer.readNbt();
        return new StageAreaSyncMessage(Objects.requireNonNull(compoundNBT));
    }

    @Override
    public void encode(final StageAreaSyncMessage message, final PacketBuffer buffer)
    {
        buffer.writeNbt((CompoundNBT) message.stageAreaNbt);
    }

    @Override
    public void handle(final StageAreaSyncMessage message, final Supplier<NetworkEvent.Context> ctx)
    {
        if (ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT)
            ctx.get().enqueueWork(() ->
                {
                    final Optional<World> optionalWorld = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
                    optionalWorld.ifPresent(
                        world ->
                        {
                        ServerStageAreaProvider.getServerStageAreas(world).ifPresent(
                            stageAreas ->
                            {
                                stageAreas.deserializeNBT(message.stageAreaNbt);

                            });
                        });
                    ctx.get().setPacketHandled(true);
                });
    }
}