package wtf.tophat.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import wtf.tophat.events.impl.OnDeathEvent;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.combat.Killaura;
import wtf.tophat.utilities.network.ServerUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;

@ModuleInfo(name = "Session Info", desc = "displays info about the current session", category = Module.Category.RENDER)
public class SessionInfo extends Module {

    public int deaths = 0;
    public int kills = 0;

    @Override
    public void onDisable() {
        deaths = 0;
        super.onDisable();
    }

    @Listen
    public void onDeath(OnDeathEvent event) {
        deaths++;
    }

    @Listen
    public void onPacket(PacketEvent event) {
        Packet<?> e = event.getPacket();
        if (e instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat)e;
            String cp21 = s02PacketChat.getChatComponent().getUnformattedText();
            if (cp21.contains("was killed by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was slain by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was shot and killed by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was snowballed to death by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was killed with magic by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was killed with an explosion by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was killed with a potion by " + mc.session.getUsername())) {
                kills++;
            }
        }
    }

    @Listen
    public void onRender(Render2DEvent event) {
        int x = 62;
        int y = 62;
        ScaledResolution sr = event.getScaledResolution();
        FontRenderer fr = mc.fontRenderer;

        DrawingUtil.rectangle(60, 60, 120, 52, true, new Color(45,45, 45));
        fr.drawStringWithShadow("Session Info", 62, 62, Color.BLUE);
        fr.drawString("Play Time: " + ServerUtil.getSessionLengthString(), x, y + 30, -1);
        fr.drawString("Kills: " + kills, x, y + 10, -1);
        fr.drawString("Deaths: " + deaths, x, y + 20, -1);
        fr.drawString("KDR: " + Math.max(1, this.kills) / Math.max(1, this.deaths), x, y + 40, -1);
    }
}
