package tophat.fun.modules.impl.design;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import tophat.fun.Client;
import tophat.fun.events.impl.network.PacketEvent;
import tophat.fun.events.impl.player.DeathEvent;
import tophat.fun.events.impl.render.Render2DEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.others.SessionUtil;
import tophat.fun.utilities.render.shader.DrawHelper;

import java.awt.*;

@ModuleInfo(name = "SessionInfo", desc = "displays info about the current session.", category = Module.Category.DESIGN)
public class SessionInfo extends Module {

    private final BooleanSetting gradientOutline = new BooleanSetting(this, "GradientOutline", false);

    public SessionInfo() {
        Client.INSTANCE.settingManager.add(
                gradientOutline
        );
    }

    private int deaths = 0;
    private int kills = 0;

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsMedium 18");
    private final static TTFFontRenderer poppinsR = CFont.FONT_MANAGER.getFont("PoppinsRegular 18");

    @Listen
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            String cp21 = ((S02PacketChat)event.getPacket()).getChatComponent().getUnformattedText();
            String username = mc.session.getUsername();
            if (cp21.matches(".*was (killed|slain|shot and killed|snowballed to death|killed with (magic|an explosion|a potion)) by " + username + ".*")) {
                kills++;
            }
        }
    }

    @Listen
    public void on2D(Render2DEvent event) {
        float x = 6;
        float y = 25;
        float width = 130;
        float height = 52;

        if(gradientOutline.get()) {
            DrawHelper.drawRoundedGradientRect(x - 1, y - 1, width + 2, height + 2, 6, new Color(24, 175, 162), new Color(0, 101, 197), new Color(24, 175, 162).brighter().brighter(), new Color(0, 101, 197).brighter().brighter());
            DrawHelper.drawRoundedRect(x, y, width, height, 6, new Color(25, 25, 25));
        } else {
            DrawHelper.drawRoundedRect(x, y, width, height, 6, new Color(25, 25, 25));
            DrawHelper.drawRoundedRectOutline(x - 1, y - 1, width + 2, height + 2, 6, 2, new Color(24, 175, 162));
        }
        poppins.drawString("Session Info", x + 2, y, -1);
        poppinsR.drawString("Play Time: " + SessionUtil.getSessionLength(), x + 2, y + 30, -1);
        poppinsR.drawString("Kills: " + kills, x + 2, y + 10, -1);
        poppinsR.drawString("Deaths: " + deaths, x + 2, y + 20, -1);
        poppinsR.drawString("KDR: " + Math.max(1, this.kills) / Math.max(1, this.deaths), x + 2, y + 40, -1);
    }

    @Override
    public void onDisable() {
        deaths = 0;
        super.onDisable();
    }

    @Listen
    public void onDeath(DeathEvent event) {
        deaths++;
    }

}
