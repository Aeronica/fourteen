package net.aeronica.mods.fourteen.setup;

import net.aeronica.mods.fourteen.Reference;
import net.aeronica.mods.fourteen.network.LivingEntityModCapSync;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModNetwork
{
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Reference.MOD_ID, "network");

    public static final String NETWORK_VERSION = new ResourceLocation(Reference.MOD_ID, "1").toString();

    public static SimpleChannel getNetworkChannel()
    {
        final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .clientAcceptedVersions(version -> true)
                .serverAcceptedVersions(version -> true)
                .networkProtocolVersion(() -> NETWORK_VERSION)
                .simpleChannel();

        channel.messageBuilder(LivingEntityModCapSync.class, 1)
                .decoder(LivingEntityModCapSync::decode)
                .encoder(LivingEntityModCapSync::encode)
                .consumer(LivingEntityModCapSync::handle)
                .add();

        return channel;
    }
}
