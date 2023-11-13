package wtf.tophat.client.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.Vec3;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.events.impl.world.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.modules.impl.move.LongJump;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.misc.BlinkUtil;
import wtf.tophat.client.utilities.network.PacketUtil;
import wtf.tophat.client.utilities.player.FallDistanceUtil;
import wtf.tophat.client.utilities.player.PlayerUtil;

@ModuleInfo(name = "Anti Void", desc = "save you from the void", category = Module.Category.PLAYER)
public class AntiVoid extends Module {

    private final NumberSetting distance;
    private Vec3 position, motion;
    private boolean wasVoid, setBack;
    private int overVoidTicks;
    private ScaffoldWalk scaffold;
    private LongJump longJump;

    public AntiVoid(){
        TopHat.settingManager.add(
                distance = new NumberSetting(this,"Distance",  0, 10, 5, 0)
        );
    }

    @Override
    public void onDisable() {
        BlinkUtil.blinking = false;
        super.onDisable();
    }

    @Listen
    public void onMotion(MotionEvent event){
        if(event.getState().equals(Event.State.PRE)){
            if (mc.player.ticksExisted <= 50) return;

            if (scaffold == null) {
                scaffold = TopHat.moduleManager.getByClass(ScaffoldWalk.class);
            }

            if (longJump == null) {
                longJump = TopHat.moduleManager.getByClass(LongJump.class);
            }

            boolean overVoid = !mc.player.onGround && !PlayerUtil.isBlockUnder(30, true);

            if (overVoid) {
                overVoidTicks++;
            } else if (mc.player.onGround) {
                overVoidTicks = 0;
            }

            if (overVoid && position != null && motion != null && overVoidTicks < 30 + distance.get().doubleValue() * 20) {
                if (!setBack) {
                    wasVoid = true;

                    BlinkUtil.blinking = true;
                    BlinkUtil.setExempt(C0FPacketConfirmTransaction.class, C00PacketKeepAlive.class, C01PacketChatMessage.class);

                    if (FallDistanceUtil.distance > distance.get().doubleValue() || setBack) {
                        PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(position.xCoord, position.yCoord - 0.1 - Math.random(), position.zCoord, false));

                        BlinkUtil.packets.clear();

                        FallDistanceUtil.distance = 0;

                        setBack = true;
                    }
                } else {
                    BlinkUtil.blinking = false;
                }
            } else {
                setBack = false;

                if (wasVoid) {
                    BlinkUtil.blinking = false;
                    wasVoid = false;
                }

                motion = new Vec3(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
                position = new Vec3(mc.player.posX, mc.player.posY, mc.player.posZ);
            }
        }

    }
}