package wtf.tophat.client.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.PacketEvent;
import wtf.tophat.client.events.impl.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.NumberSetting;

@ModuleInfo(name = "Ambience", desc = "change the weather", category = Module.Category.RENDER)
public class Ambiance extends Module {
    private final NumberSetting time;
    private final NumberSetting timeSpeed;
    private final NumberSetting rainStrength;
    private final NumberSetting thunderStrength;
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
        this.counter = 0.0;
        if (getWorld()  != null) {
            this.rainStrengthf = getWorld().getRainStrength(1.0f);
            this.thunderStrengthf = getWorld().getThunderStrength(1.0f);
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.counter = 0.0;
        if (getWorld() != null) {
            getWorld() .setRainStrength(this.rainStrengthf);
            getWorld() .setThunderStrength(this.thunderStrengthf);
        }
        super.onDisable();
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
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
        Packet<?> packet = event.getPacket();
        if (packet instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
        }
    }
}