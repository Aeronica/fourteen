package net.aeronica.mods.fourteen.network.messages;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import static net.aeronica.mods.fourteen.gui.Handler.openTestScreen;

public class OpenScreenMessage extends AbstractMessage<OpenScreenMessage>
{
    private final SM screen;

    public OpenScreenMessage()
    {
        this(null);
    }

    public OpenScreenMessage(SM screen)
    {
        this.screen = screen;
    }

    public OpenScreenMessage decode(final PacketBuffer buffer)
    {
        final SM screen = buffer.readEnum(SM.class);
        return new OpenScreenMessage(screen);
    }

    public void encode(final OpenScreenMessage message, final PacketBuffer buffer)
    {
        buffer.writeEnum(message.screen);
    }

    public void handle(final OpenScreenMessage message, final Supplier<NetworkEvent.Context> ctx)
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
