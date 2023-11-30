package tophat.fun.modules.impl.movement;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.lwjgl.input.Keyboard;
import tophat.fun.events.Event;
import tophat.fun.events.impl.network.PacketEvent;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;

@ModuleInfo(name = "InvMove", desc = "move while having a gui open.", category = Module.Category.MOVEMENT)
public class InvMove extends Module {

    private final BooleanSetting openPacket = new BooleanSetting(this, "CancelOpenPackets", false);
    private final BooleanSetting extraStorage = new BooleanSetting(this, "ExtraStorage", false);

    @Listen
    public void onPacket(PacketEvent event) {
        if(mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        Packet<?> packet = event.getPacket();

        if(event.getType() == PacketEvent.Type.OUTGOING) {
            if(openPacket.get() && packet instanceof S2DPacketOpenWindow || packet instanceof S2EPacketCloseWindow) {
                event.setCancelled(true);
            }

            if(extraStorage.get() && packet instanceof C0DPacketCloseWindow) {
                event.setCancelled(true);
            }

            if(packet instanceof C16PacketClientStatus && ((C16PacketClientStatus) packet).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                event.setCancelled(true);
            }
        }
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            block3 : {
                KeyBinding[] moveKeys;
                block2 : {
                    moveKeys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak, mc.gameSettings.keyBindSprint};
                    if (mc.currentScreen == null || (mc.currentScreen instanceof GuiChat))
                        break block2;
                    for (KeyBinding key : moveKeys) {
                        key.pressed = Keyboard.isKeyDown(key.getKeyCode());
                    }
                    break block3;
                }
                for (KeyBinding bind : moveKeys) {
                    if (Keyboard.isKeyDown(bind.getKeyCode()))  continue;
                    KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                }
            }
        }
    }

}
