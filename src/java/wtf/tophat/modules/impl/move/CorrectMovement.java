package wtf.tophat.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.TopHat;
import wtf.tophat.events.handler.PlayerHandler;
import wtf.tophat.events.impl.RunTickEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.StringSetting;

@ModuleInfo(name = "Correct Movement",desc = "correct your movement", category = Module.Category.MOVE)
public class CorrectMovement extends Module {

    private final StringSetting mode;

    public CorrectMovement() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Strict", "Strict", "Silent", "Aggressive")
        );
    }

    @Listen
    public void onTick(RunTickEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        PlayerHandler.moveFix = isEnabled();
        if(isEnabled()) {
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
