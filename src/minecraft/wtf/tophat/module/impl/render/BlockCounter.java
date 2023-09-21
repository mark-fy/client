package wtf.tophat.module.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.util.BlockPos;
import wtf.tophat.Client;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;

import static wtf.tophat.utilities.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.Colors.WHITE_COLOR;

@ModuleInfo(name = "BlockCounter",desc = "counts how much blocks you have walked", category = Module.Category.RENDER)
public class BlockCounter extends Module {

    private final BooleanSetting fontShadow;

    public BlockCounter() {
        Client.settingManager.add(
                fontShadow = new BooleanSetting(this, "Font Shadow", true)
        );
    }

    private int blocksWalked = 0;
    private BlockPos lastPos;

    @Listen
    public void onMotion(MotionEvent event) {
        BlockPos currentPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        if (lastPos != null && !currentPos.equals(lastPos)) {
            double distance = currentPos.distanceSq(lastPos.getX(), lastPos.getY(), lastPos.getZ());

            if (distance >= 1.0) {
                blocksWalked++;
                lastPos = currentPos;
            }
        } else {
            lastPos = currentPos;
        }
    }

    @Listen
    public void on2D(Render2DEvent event) {
        if(Methods.mc.player == null || Methods.mc.world == null)
            return;

        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();

        String text = "Blocks: " + blocksWalked;

        int counter = 0;
        int color = ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L);

        int x = event.getScaledResolution().getScaledWidth();
        int y = event.getScaledResolution().getScaledHeight();

        int strWidth = fr.getStringWidth(text);

        int xOffset = 15;
        int yOffset = 25;

        int textX = x - strWidth - xOffset;
        int textY = y - yOffset;

        DrawingUtil.rectangle(textX - 1, textY - 1, strWidth + 11, 20, true, new Color(5,5,5));
        DrawingUtil.rectangle(textX, textY, strWidth + 9, 18, true, new Color(60,60,60));
        DrawingUtil.rectangle(textX + 1, textY + 1, strWidth + 7, 16, true, new Color(40,40,40));
        DrawingUtil.rectangle(textX + 3, textY + 3, strWidth + 3, 12, true, new Color(60,60,60));
        DrawingUtil.rectangle(textX + 4, textY + 4, strWidth + 1, 10, true, new Color(22,22,22));
        DrawingUtil.rectangle(textX + 4, textY + 4, strWidth + 1, 1, true, new Color(color));

        fr.drawStringChoose(fontShadow.getValue(), text, textX + 5, textY + 5, Color.WHITE);
        counter++;
    }

    @Override
    public void onEnable() {
        blocksWalked = 0;
        lastPos = null;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        blocksWalked = 0;
        lastPos = null;
        super.onDisable();
    }

}
