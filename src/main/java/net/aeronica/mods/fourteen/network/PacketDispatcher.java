package net.aeronica.mods.fourteen.network;

import net.aeronica.mods.fourteen.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;

public class PacketDispatcher
{
    private static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Reference.MOD_ID, "network");
    private static final String NETWORK_VERSION = new ResourceLocation(Reference.MOD_ID, "1").toString();
    private static SimpleChannel modChannel;
    private static final Map<Class<?>, Integer> packets = new HashMap<>();
    private static int packetId = 1;

    public static SimpleChannel getNetworkChannel()
    {
        final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .clientAcceptedVersions(version -> true)
                .serverAcceptedVersions(version -> true)
                .networkProtocolVersion(() -> NETWORK_VERSION)
                .simpleChannel();

        registerMessages(channel);
        modChannel = channel;

        return channel;
    }

    private static void registerMessages(SimpleChannel channel)
    {
        channel.messageBuilder(LivingEntityModCapSync.class, packetId++)
                .decoder(LivingEntityModCapSync::decode)
                .encoder(LivingEntityModCapSync::encode)
                .consumer(LivingEntityModCapSync::handle)
                .add();
    }

    private PacketDispatcher() { /* NOP */ }

    // ========================================================//
    // The following methods are the 'wrapper' methods; again,
    // this just makes sending a message slightly more compact
    // and is purely a matter of stylistic preference
    // ========================================================//

    /**
     * Send this message to the specified player's client-side counterpart. See
     * {@link SimpleChannel#send(PacketDistributor.PacketTarget, Object)}
     */
    public static void sendTo(IMessage message, ServerPlayerEntity player)
    {
        PacketDispatcher.modChannel.send(PacketDistributor.PLAYER.with(()->player), message);
    }

    /**
     * Send this message to everyone. See
     * {@link SimpleChannel#send(PacketDistributor.PacketTarget, Object)}
     */
    public static void sendToAll(IMessage message)
    {
        PacketDispatcher.modChannel.send(PacketDistributor.ALL.with(null), message);
    }

    /**
     * Send this message to everyone within a certain range of a point. See
     * {@link SimpleChannel#send(PacketDistributor.PacketTarget, Object)}
     */
    public static void sendToAllAround(IMessage message, PacketDistributor.TargetPoint point)
    {
        PacketDispatcher.modChannel.send(PacketDistributor.NEAR.with(()->point), message);
    }

    /**
     * Sends a message to everyone within a certain range of the coordinates in
     * the same dimension. Shortcut to
     * {@link PacketDispatcher#sendToAllAround(IMessage, PacketDistributor.TargetPoint)}
     */
    public static void sendToAllAround(IMessage message, Dimension dimension, double x, double y, double z, double range)
    {
        PacketDispatcher.sendToAllAround(message, new PacketDistributor.TargetPoint(x, y, z, range, dimension.getType()));
    }

    /**
     * Sends a message to everyone within a certain range of the player
     * provided. Shortcut to
     * {@link PacketDispatcher#sendToAllAround(IMessage, Dimension, double, double, double, double)}
     */
    public static void sendToAllAround(IMessage message, PlayerEntity player, double range)
    {
        PacketDispatcher.sendToAllAround(message, player.getEntityWorld().getDimension(), player.posX, player.posY, player.posZ, range);
    }

    /**
     * Send this message to everyone within the supplied dimension. See
     * {@link SimpleChannel#send(PacketDistributor.PacketTarget, Object)}
     */
    public static void sendToDimension(IMessage message, Dimension dimension)
    {
        PacketDispatcher.modChannel.send(PacketDistributor.DIMENSION.with(dimension::getType), message);
    }

    /**
     * Send this message to the server. See
     * {@link SimpleChannel#sendToServer(Object)}
     */
    public static void sendToServer(IMessage message)
    {
        PacketDispatcher.modChannel.sendToServer(message);
    }

}
