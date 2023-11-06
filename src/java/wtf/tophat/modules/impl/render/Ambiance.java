package wtf.tophat.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.events.impl.Render3DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;

@ModuleInfo(name = "Ambience", desc = "change the weather", category = Module.Category.RENDER)
public class Ambiance extends Module {
    public final NumberSetting time;
    public final NumberSetting timeSpeed;
    public final NumberSetting rainStrength;
    public final NumberSetting thunderStrength;
    private double counter;
    private float rainStrengthf;
    private float thunderStrengthf;

    public Ambiance() {
        TopHat.settingManager.add(
            this.time = new NumberSetting(this, "Time", 0.0, 24000.0, 1900.0, 1),
            this.timeSpeed = new NumberSetting(this, "TimeSpeed", 0.0, 100.0, 0.0, 1),
            this.rainStrength = new NumberSetting(this, "Rain", 0.0, 1.0, 0.0, 1),
            this.thunderStrength = new NumberSetting(this, "Thunder", 0.0, 1.0, 0.0, 1)
        );
        this.rainStrengthf = 0.0f;
        this.thunderStrengthf = 0.0f;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.counter = 0.0;
        if (mc.world != null) {
            this.rainStrengthf = mc.world.getRainStrength(1.0f);
            this.thunderStrengthf = mc.world.getThunderStrength(1.0f);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.counter = 0.0;
        if (mc.world != null) {
            mc.world.setRainStrength(this.rainStrengthf);
            mc.world.setThunderStrength(this.thunderStrengthf);
        }
    }

    @Listen
    public void onRender3D(Render3DEvent render3D) {
        this.counter = ((timeSpeed.get().longValue() > 0.0) ? (this.counter + this.timeSpeed.get().floatValue()) : 0.0);
        mc.world.setWorldTime((long) (time.get().longValue() + this.counter));
        if (this.counter > 24000.0) {
            this.counter = 0.0;
        }
        mc.world.setRainStrength(rainStrength.get().floatValue());
        mc.world.setThunderStrength(thunderStrength.get().floatValue());
    }

    @Listen
    public void onPacket(PacketEvent event){
        final Packet packet = event.getPacket();
        if (packet instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
        }
    }
}