package wtf.tophat.client.modules.impl.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.WorldSettings;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.utilities.Methods;

import java.util.UUID;

@ModuleInfo(name = "Fake Player",desc = "hides your name client side", category = Module.Category.MISC)
public class FakePlayer extends Module {

    @Override
    public void onEnable() {
        if(getPlayer() == null || getDead()){
            setEnabled(false);
        }

        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(Methods.mc.world, new GameProfile(UUID.fromString("9b7f28c2-98ea-4d70-b2db-48e6c78a4a9d"), Methods.mc.session.getUsername()));
        clonedPlayer.copyLocationAndAnglesFrom(Methods.mc.player);
        clonedPlayer.rotationYawHead = Methods.mc.player.rotationYawHead;
        clonedPlayer.rotationYaw = Methods.mc.player.rotationYaw;
        clonedPlayer.rotationPitch = Methods.mc.player.rotationPitch;
        clonedPlayer.setGameType(WorldSettings.GameType.SURVIVAL);
        clonedPlayer.setHealth(20);
        getWorld().addEntityToWorld(-4200, clonedPlayer);
        clonedPlayer.onLivingUpdate();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (getWorld() != null) {
            getWorld().removeEntityFromWorld(-4200);
        }
        super.onDisable();
    }
}