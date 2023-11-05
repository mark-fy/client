package wtf.tophat.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.time.TimeUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleInfo(name = "Auto Hypixel", desc = "automatically navigate inside hypixel", category = Module.Category.MISC)
public class AutoHypixel extends Module {

    private final BooleanSetting autoReconnect, antiBan, autoPlay, autoBounty, skyWars;
    private final StringSetting skyWarsMode;
    private final NumberSetting skyWarsDelay, minBounty;

    // Sky Wars
    private final TimeUtil timer = new TimeUtil();

    // Auto Bounty
    private final List<Entity> entities = new CopyOnWriteArrayList<>();

    public AutoHypixel() {
        TopHat.settingManager.add(
                autoReconnect = new BooleanSetting(this, "Auto Reconnect", true),
                antiBan = new BooleanSetting(this, "Anti Ban", true),
                autoPlay = new BooleanSetting(this, "Auto Play", true),
                skyWars = new BooleanSetting(this, "SkyWars", true).setHidden(() -> !autoPlay.get()),
                skyWarsMode = new StringSetting(this, "SkyWars Mode", "Solo Normal", "Solo Normal", "Solo Insane").setHidden(() -> !autoPlay.get() || !skyWars.get()),
                skyWarsDelay = new NumberSetting(this, "SkyWars Delay", 0, 5000, 1000, 0).setHidden(() -> !autoPlay.get() || !skyWars.get()),
                autoBounty = new BooleanSetting(this, "Auto Bounty", false),
                minBounty = new NumberSetting(this, "Minimum Bounty", 50, 5000, 500, 0).setHidden(() -> !autoBounty.get())
        );
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if(autoBounty.get()) {
            entities.removeIf(player -> !mc.world.playerEntities.contains(player));

            double maxDistanceSq = 200 * 200;

            for (Entity entity : mc.world.loadedEntityList) {
                if (entity != mc.player && entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;

                    double distanceSq = mc.player.getDistanceSqToEntity(player);

                    if (distanceSq <= maxDistanceSq) {
                        String display = player.getDisplayName().getUnformattedText();
                        String name = player.getName();

                        if (display.contains("§6§l")) {
                            String[] split = display.split(" ");
                            if (split.length > 2) {
                                int bounty = Integer.parseInt(
                                        split[split.length - 1]
                                                .replace("§6§1l", "")
                                                .replace("g", "")
                                );
                                if (bounty >= minBounty.get().intValue()) {
                                    if (!entities.contains(player)) {
                                        entities.add(player);
                                        sendChat(name + " has " + bounty + "g on him!", true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if(getPlayer() == null || getWorld() == null)
            return;

        if(autoReconnect.get()) {
            if(event.getPacket() instanceof S02PacketChat) {
                String unformatted = EnumChatFormatting.getTextWithoutFormattingCodes(((S02PacketChat)event.getPacket()).getChatComponent().getUnformattedText()), text = unformatted.replace(" ", "");
                if(unformatted.contains("Flying or related")) {
                    sendPacketUnlogged(new C01PacketChatMessage("/back"));
                }
            }
        }

        if(autoPlay.get()) {
            if(event.getPacket() instanceof S02PacketChat && skyWars.get()) {
                String unformatted = EnumChatFormatting.getTextWithoutFormattingCodes(((S02PacketChat)event.getPacket()).getChatComponent().getUnformattedText()), text = unformatted.replace(" ", "");

                String victory = "You won! Want to play again? Click here!";
                String death = "You died! Want to play again? Click here!";

                if(unformatted.contains(victory) || unformatted.contains(death) && timer.elapsed(skyWarsDelay.get().longValue())) {
                    sendPacketUnlogged(new C01PacketChatMessage(skyWarsMode.is("Solo Normal") ? "/play solo_normal" : "/play solo_insane"));
                }
            }
        }

        if(antiBan.get()) {
            if(event.getPacket() instanceof S02PacketChat) {
                String unformatted = EnumChatFormatting.getTextWithoutFormattingCodes(((S02PacketChat)event.getPacket()).getChatComponent().getUnformattedText()), text = unformatted.replace(" ", "");
                if (unformatted.contains("A player has been removed from your game.")) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        mc.player.sendChatMessage("/hub");
                        sendChat("Left the game due to someone being banned!", true);
                    }).start();
                }
            }
        }
    }
}