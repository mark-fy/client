package wtf.tophat.client.modules.impl.render;

import java.awt.Color;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.network.PacketEvent;
import wtf.tophat.client.events.impl.render.Render3DEvent;
import wtf.tophat.client.events.impl.world.RunTickEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.math.MathUtil;
import wtf.tophat.client.utilities.render.shaders.RenderUtil;
import wtf.tophat.client.utilities.math.time.TimeUtil;

@ModuleInfo(name = "Superhero FX", desc = "text particles", category = Module.Category.RENDER)
public class SuperheroFX extends Module {
    private static final String[] texts = new String[]{"SO BAD", "L", "BOZO", "EZ", "SKILLS ISSUES", "NO BRAIN", "KYS"};
    private static final Random rand = new Random();
    private final TimeUtil timer = new TimeUtil();

    private final NumberSetting delay ;
    private final BooleanSetting randomColors;

    private final List<PopupText> popTexts = new CopyOnWriteArrayList<>();

    public SuperheroFX(){
        TopHat.settingManager.add(
                delay = new NumberSetting(this,"Delay", 0.0, 10.0,1.0, 1),
                randomColors = new BooleanSetting(this,"Random Colors", true)
        );
    }

    @Listen
    public void onTick(RunTickEvent event) {
        this.popTexts.forEach(PopupText::update);
    }

    @Listen
    public void onRender3D(Render3DEvent event) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        this.popTexts.forEach(pop -> {
            RenderUtil.glBillboardDistanceScaled((pop).vec3.xCoord, (pop).vec3.yCoord, (pop).vec3.zCoord, mc.player, 5);
            GlStateManager.translate(-((double)mc.fontRenderer.getStringWidth(pop.displayName) / 2.0), 0.0, 0.0);
            mc.fontRenderer.drawStringWithShadow(pop.displayName, 0.0F, 0.0F, pop.color);
        });
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if(getPlayer() == null || getWorld() == null)
            return;
        try {
            if (event.getPacket() instanceof S19PacketEntityStatus) {
                S19PacketEntityStatus packet = (S19PacketEntityStatus)event.getPacket();
                Entity e = packet.getEntity(mc.world);
                if (packet.getOpCode() != 2) return;
                if (!(mc.player.getDistanceToEntity(e) < 20.0f & e != mc.player)) return;
                if (!this.timer.elapsed((long)(this.delay.get().doubleValue() * 1000.0))) return;
                this.timer.reset();
                this.registerPopUpText(e);
                return;
            }
            if (!(event.getPacket() instanceof S13PacketDestroyEntities)) return;
            S13PacketDestroyEntities packet = (S13PacketDestroyEntities)event.getPacket();
            int[] array = packet.getEntityIDs();
            int i = 0;
            while (i < array.length - 1) {
                block9: {
                    int id;
                    block8: {
                        id = array[i];
                        try {
                            if (mc.world.getEntityByID(id) != null) break block8;
                            break block9;
                        }
                        catch (ConcurrentModificationException exception) {
                            return;
                        }
                    }
                    Entity e = mc.world.getEntityByID(id);
                    if (e != null && e.isDead && mc.player.getDistanceToEntity(e) < 20.0f & e != mc.player && e instanceof EntityPlayer) {
                        this.registerPopUpText(e);
                    }
                }
                ++i;
            }
        }
        catch (NullPointerException ignored) {}
    }

    private void registerPopUpText(Entity entity) {
        IntStream.rangeClosed(0, rand.nextInt(5)).mapToObj(i -> MathUtil.getCenter(entity.getEntityBoundingBox()).addVector(rand.nextInt(2) - 1, (double)rand.nextInt(2) - 0.8, rand.nextInt(2) - 1)).map(pos -> new PopupText(texts[rand.nextInt(texts.length)], (Vec3)pos)).forEach(this.popTexts::add);
    }

    private double random() {
        return MathHelper.clamp_double(0.011 + rand.nextDouble() * 0.025, 0.011, 0.025);
    }

    class PopupText {
        public final int color;
        private final long start;
        private final double yIncrease;
        public final String displayName;
        private Vec3 vec3;

        public PopupText(String displayName, Vec3 vec3) {
            this.color = !randomColors.get() ? -1 : Color.getHSBColor(rand.nextFloat(), 1.0f, 0.9f).getRGB();
            this.start = System.currentTimeMillis();
            this.yIncrease = random();
            this.displayName = EnumChatFormatting.ITALIC + displayName;
            this.vec3 = vec3;
        }

        public void update() {
            this.vec3 = this.vec3.addVector(0.0, this.yIncrease, 0.0);
            if (System.currentTimeMillis() - this.start <= 1000L) return;
            SuperheroFX.this.popTexts.remove(this);
        }
    }
}