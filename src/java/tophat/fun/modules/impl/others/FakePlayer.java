package tophat.fun.modules.impl.others;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.WorldSettings;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.utilities.Methods;

import java.util.UUID;

@ModuleInfo(name = "FakePlayer", desc = "spawns a fake player.", category = Module.Category.OTHERS)
public class FakePlayer extends Module {

    @Override
    public void onEnable() {
        if(mc.thePlayer == null || !mc.thePlayer.isDead){
            setEnabled(false);
        }

        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(Methods.mc.theWorld, new GameProfile(UUID.fromString("9b7f28c2-98ea-4d70-b2db-48e6c78a4a9d"), "TestSubject"));
        clonedPlayer.copyLocationAndAnglesFrom(Methods.mc.thePlayer);
        clonedPlayer.rotationYawHead = Methods.mc.thePlayer.rotationYawHead;
        clonedPlayer.rotationYaw = Methods.mc.thePlayer.rotationYaw;
        clonedPlayer.rotationPitch = Methods.mc.thePlayer.rotationPitch;
        clonedPlayer.setGameType(WorldSettings.GameType.SURVIVAL);
        clonedPlayer.setHealth(15);
        mc.theWorld.addEntityToWorld(-4200, clonedPlayer);
        clonedPlayer.onLivingUpdate();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.theWorld != null) {
            mc.theWorld.removeEntityFromWorld(-4200);
        }
        super.onDisable();
    }

}
