package wtf.tophat.client.modules.impl.player;

import com.mojang.authlib.GameProfile;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings;
import org.lwjgl.util.vector.Vector2f;
import wtf.tophat.client.events.impl.CollisionBoxesEvent;
import wtf.tophat.client.events.impl.PacketEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.utilities.Methods;

import java.util.UUID;

@ModuleInfo(name = "Freecam",desc = "move out of your body", category = Module.Category.PLAYER)
public class Freecam extends Module {

    private Vec3 pos;
    private Vector2f rots;

    @Listen
    public void onCollision(CollisionBoxesEvent event) {
        if(getPlayer() == null || getWorld() == null)
            return;

        event.setCancelled(true);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if(getPlayer() == null || getWorld() == null)
            return;

        if(event.getPacket() instanceof C03PacketPlayer) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {
        if(getPlayer() == null || getDead()){
            setEnabled(false);
        }

        pos = new Vec3(mc.player.posX, mc.player.posY, mc.player.posZ);
        rots = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);

        mc.player.capabilities.allowFlying = true;
        mc.player.capabilities.isFlying = true;

        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(Methods.mc.world, new GameProfile(UUID.fromString("9b7f28c2-98ea-4d70-b2db-48e6c78a4a9d"), Methods.mc.session.getUsername()));
        clonedPlayer.copyLocationAndAnglesFrom(Methods.mc.player);
        clonedPlayer.rotationYawHead = Methods.mc.player.rotationYawHead;
        clonedPlayer.rotationYaw = Methods.mc.player.rotationYaw;
        clonedPlayer.rotationPitch = Methods.mc.player.rotationPitch;
        clonedPlayer.setGameType(WorldSettings.GameType.SURVIVAL);
        clonedPlayer.setHealth(20);
        Methods.mc.world.addEntityToWorld(-4200, clonedPlayer);
        clonedPlayer.onLivingUpdate();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (getPlayer() == null || getWorld() == null) {
            return;
        }

        mc.world.removeEntityFromWorld(-4200);

        mc.player.setPositionAndRotation(pos.xCoord, pos.yCoord, pos.zCoord, rots.getX(), rots.getY());

        mc.player.capabilities.allowFlying = false;
        mc.player.capabilities.isFlying = false;
        super.onDisable();
    }
}
