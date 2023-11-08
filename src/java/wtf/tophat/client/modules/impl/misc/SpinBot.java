package wtf.tophat.client.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.Render3DEvent;
import wtf.tophat.client.events.impl.RotationEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;

@ModuleInfo(name = "Spin Bot", desc = "client-side spin bot like cs hacks.", category = Module.Category.MISC)
public class SpinBot extends Module {
    private final NumberSetting staticOffsetYaw, staticOffsetPitch, yawJitterTimer, pitchJitterTimer, yawSpinSpeed;
    private final StringSetting pitchMode, yawMode, side;
    private final BooleanSetting lockView;

    public SpinBot(){
        TopHat.settingManager.add(
                side = new StringSetting(this, "Side", "Client", "Client", "Server", "Both"),
                pitchMode = new StringSetting(this, "Pitch", "Offset", "Static", "Offset", "Random", "Jitter", "None"),
                yawMode = new StringSetting(this, "Yaw", "Offset", "Static", "Offset", "Random", "Jitter", "Spin", "None"),
                staticOffsetYaw = new NumberSetting(this, "Static Offset Yaw", -180, 180, 0, 0).setHidden(() -> !yawMode.is("Static")),
                staticOffsetPitch = new NumberSetting(this, "Static Offset Pitch", -90, 90, 0, 0).setHidden(() -> !pitchMode.is("Static")),
                yawJitterTimer = new NumberSetting(this, "Yaw Jitter Timer", 1, 40, 1, 0).setHidden(() -> !yawMode.is("Jitter")),
                pitchJitterTimer = new NumberSetting(this, "Pitch Jitter Timer", 1, 40, 1, 0).setHidden(() -> !pitchMode.is("Jitter")),
                yawSpinSpeed = new NumberSetting(this, "Yaw Spin Speed", -90, 90, 5, 0).setHidden(() -> !yawMode.is("Spin")),
                lockView = new BooleanSetting(this, "Lock View", false)
        );
    }

    private float pitch = 0F;
    private float lastSpin = 0F;
    private int yawTimer = 0;
    private int pitchTimer = 0;

    @Override
    public void onDisable() {
        pitch = -4.9531336E7f;
        lastSpin = 0.0f;
        yawTimer = 0;
        pitchTimer = 0;
    }

    @Listen
    public void onRots(RotationEvent event){
        if (getPlayer() == null)
            return;

        if (!yawMode.get().equalsIgnoreCase("none")) {
            float yaw = 0F;
            switch (yawMode.get().toLowerCase()) {
                case "static":
                    yaw = staticOffsetYaw.get().floatValue();
                    break;
                case "offset":
                    yaw = mc.player.rotationYaw + staticOffsetYaw.get().floatValue();
                    break;
                case "random":
                    yaw = (float) Math.floor(Math.random() * 360.0 - 180.0);
                    break;
                case "jitter":
                    if (yawTimer++ % (yawJitterTimer.get().doubleValue() * 2) >= yawJitterTimer.get().doubleValue())
                        yaw = mc.player.rotationYaw;
                    else
                        yaw = mc.player.rotationYaw - 180F;
                    break;
                case "spin":
                    lastSpin += yawSpinSpeed.get().doubleValue();
                    yaw = lastSpin;
                    break;
            }
            switch (side.get()){
                case "Server":
                    event.setYaw(yaw);
                    break;
                case "Both":
                    event.setYaw(yaw);
                    mc.player.renderYawOffset = yaw;
                    mc.player.rotationYawHead = yaw;
                    lastSpin = yaw;
                    break;
            }
            if(lockView.get()){
                mc.player.rotationYaw = yaw;
            }
        }
    }

    @Listen
    public void onRender3D(Render3DEvent event) {
        if (mc.player == null)
            return;

        if (!yawMode.get().equalsIgnoreCase("none")) {
            float yaw = 0F;
            switch (yawMode.get().toLowerCase()) {
                case "static":
                    yaw = staticOffsetYaw.get().floatValue();
                    break;
                case "offset":
                    yaw = mc.player.rotationYaw + staticOffsetYaw.get().floatValue();
                    break;
                case "random":
                    yaw = (float) Math.floor(Math.random() * 360.0 - 180.0);
                    break;
                case "jitter":
                    if (yawTimer++ % (yawJitterTimer.get().doubleValue() * 2) >= yawJitterTimer.get().doubleValue())
                        yaw = mc.player.rotationYaw;
                    else
                        yaw = mc.player.rotationYaw - 180F;
                    break;
                case "spin":
                    lastSpin += yawSpinSpeed.get().doubleValue();
                    yaw = lastSpin;
                    break;
            }
            switch (side.get()){
                case "Client":
                    mc.player.renderYawOffset = yaw;
                    mc.player.rotationYawHead = yaw;
                    lastSpin = yaw;
                    break;
            }
            if(lockView.get()){
                mc.player.rotationYaw = yaw;
            }
        }
        switch (pitchMode.get().toLowerCase()) {
            case "static":
                pitch = staticOffsetPitch.get().floatValue();
                break;
            case "offset":
                pitch = mc.player.rotationPitch + staticOffsetPitch.get().floatValue();
                break;
            case "random":
                pitch = (float) Math.floor(Math.random() * 180.0 - 90.0);
                break;
            case "jitter":
                if (pitchTimer++ % (pitchJitterTimer.get().doubleValue() * 2) >= pitchJitterTimer.get().doubleValue())
                    pitch = 90F;
                else
                    pitch = -90F;
                break;
        }
    }
}
