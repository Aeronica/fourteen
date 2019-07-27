package net.aeronica.mods.fourteen.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestScreen extends Screen
{
    private static int depth;
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen lastScreen;

    public TestScreen(Screen lastScreen)
    {
        super(new TranslationTextComponent("screen.fourteen.test.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    public void init()
    {
        super.init();
        this.addButton(new Button(this.width / 2 - 100, (this.height / 6 + 168) - 20, 200, 20, I18n.format("gui.open"), (done) -> {
            this.minecraft.displayGuiScreen(new TestScreen(this));
            ++depth;
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.format("gui.done"), (done) -> {
            this.minecraft.displayGuiScreen(this.lastScreen);
            if (depth >= 1)
                depth--;
        }));

    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_)
    {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 15, 16777215);
        this.drawCenteredString(this.font, String.format("Depth %d", depth + 1), this.width / 2, 25, 16777215);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_)
    {
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public boolean shouldCloseOnEsc()
    {
        return false;
    }

    // Call on ESC key force close! Ignores chained GUI's?
    // Override "shouldCloseOnEsc and return false" to prevent closing on ESC.
    @Override
    public void onClose()
    {
        depth = 0;
        LOGGER.debug("TestScreen onClose");
        super.onClose();
    }

    // Called on ESC key and minecraft.displayGuiScreen(this.lastScreen);
    @Override
    public void removed()
    {
        LOGGER.debug("TestScreen removed {}", depth);
        super.removed();
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
