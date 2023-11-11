package wtf.tophat.client.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.handler.PlayerHandler;
import wtf.tophat.client.events.impl.world.TickEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.StringSetting;

@ModuleInfo(name = "Correct Movement",desc = "correct your movement", category = Module.Category.MOVE)
public class CorrectMovement extends Module {

    private final StringSetting mode;

    public CorrectMovement() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Strict", "Strict", "Silent", "Aggressive")
        );
    }

    @Listen
    public void onTick(TickEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        PlayerHandler.moveFix = isEnabled();
        if(isEnabled() && event.getState() == Event.State.PRE) {
            switch (mode.get()) {
                case "Strict":
                    PlayerHandler.currentMode = PlayerHandler.MoveFixMode.STRICT;
                    break;
                case "Aggressive":
                    PlayerHandler.currentMode = PlayerHandler.MoveFixMode.AGGRESSIVE;
                    break;
                case "Silent":
                    PlayerHandler.currentMode = PlayerHandler.MoveFixMode.SILENT;
                    break;
            }
        }
    }
}
