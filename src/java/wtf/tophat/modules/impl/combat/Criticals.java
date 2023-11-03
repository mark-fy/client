package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import org.apache.commons.lang3.RandomUtils;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.player.PlayerUtil;

@ModuleInfo(name = "Criticals", desc = "criticals the enemy", category = Module.Category.COMBAT)
public class Criticals extends Module {

    public StringSetting mode;

    public Criticals(){
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Old NCP", "Old NCP")
        );
    }

    private int stage;

    @Listen
    public void onMotion(MotionEvent event){
        switch (mode.get()){
            case "Old NCP":
                if (PlayerUtil.isMathGround()) {
                    event.setOnGround(false);

                    switch (this.stage) {
                        case 0:
                            event.setY(event.getY() + 0.0012412948712);
                            break;
                        case 1:
                            event.setY(event.getY() + 0.024671798242);
                            break;
                        case 2:
                            event.setY(event.getY() + 0.00742167842112);
                            break;
                        case 3:
                            event.setY(event.getY() + 0.02124121242);
                            break;
                    }

                    event.setY(event.getY() + RandomUtils.nextFloat(1.0E-8F, 1.0E-6F));

                    if (this.stage++ >= 3) {
                        this.stage = 0;

                        event.setOnGround(true);
                    }
                }
                break;
        }
    }
}
