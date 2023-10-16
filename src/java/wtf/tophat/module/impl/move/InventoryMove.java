package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;

@ModuleInfo(name = "Inventory Move",desc = "move in guis", category = Module.Category.MOVE)
public class InventoryMove extends Module {

    private final BooleanSetting noOpenPacket, xCarry;

    public InventoryMove() {
        Client.settingManager.add(
                noOpenPacket = new BooleanSetting(this, "No Open Packet", false),
                xCarry = new BooleanSetting(this, "Extra Storage", false)
        );
    }


    @Listen
    public void onPacket(PacketEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        if(event.getType() == PacketEvent.Type.OUTGOING) {
            if(noOpenPacket.get() && event.getPacket() instanceof S2DPacketOpenWindow || event.getPacket() instanceof S2EPacketCloseWindow) {
                event.setCancelled(true);
            }

            if(xCarry.get() && event.getPacket() instanceof C0DPacketCloseWindow) {
                event.setCancelled(true);
            }

            if(event.getPacket() instanceof C16PacketClientStatus && ((C16PacketClientStatus) event.getPacket()).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
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
                    moveKeys = new KeyBinding[]{mc.settings.keyBindForward, mc.settings.keyBindLeft, mc.settings.keyBindBack, mc.settings.keyBindRight, mc.settings.keyBindJump, mc.settings.keyBindSneak, mc.settings.keyBindSprint};
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
