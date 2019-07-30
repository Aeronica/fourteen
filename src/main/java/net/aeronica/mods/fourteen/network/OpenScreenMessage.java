package net.aeronica.mods.fourteen.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import static net.aeronica.mods.fourteen.gui.Handler.openTestScreen;

public class OpenScreenMessage
{
    private final SM screen;

    public OpenScreenMessage(SM screen)
    {
        this.screen = screen;
    }

    public static OpenScreenMessage decode(final PacketBuffer buffer)
    {
        final SM screen = buffer.readEnumValue(SM.class);
        return new OpenScreenMessage(screen);
    }

    public static void encode(final OpenScreenMessage message, final PacketBuffer buffer)
    {
        buffer.writeEnumValue(message.screen);
    }

    public static void handle(final OpenScreenMessage message, final Supplier<NetworkEvent.Context> ctx)
    {
        if (ctx.get().getDirection().getReceptionSide().isClient())
            ctx.get().enqueueWork(() ->
                {
                    switch (message.screen)
                    {
                        case TEST_ONE:
                            openTestScreen();
                            break;
                        case TEST_TWO:
                            openTestScreen();
                            break;
                    }
                });
        ctx.get().setPacketHandled(true);
    }

    public enum SM
    {
        TEST_ONE, TEST_TWO;
    }
}
