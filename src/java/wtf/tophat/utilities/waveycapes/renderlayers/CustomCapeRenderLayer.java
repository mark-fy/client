package wtf.tophat.utilities.waveycapes.renderlayers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import wtf.tophat.utilities.waveycapes.CapeHolder;
import wtf.tophat.utilities.waveycapes.sim.StickSimulation;
import wtf.tophat.utilities.waveycapes.utils.Mth;

public class CustomCapeRenderLayer implements LayerRenderer<AbstractClientPlayer> {
	static final int partCount = 16;

	private ModelRenderer[] customCape = new ModelRenderer[16];

	private final RenderPlayer playerRenderer;

	private SmoothCapeRenderer smoothCapeRenderer = new SmoothCapeRenderer();

	public CustomCapeRenderLayer(RenderPlayer playerRenderer, ModelBase model) {
		this.playerRenderer = playerRenderer;
		buildMesh(model);
	}

	private void buildMesh(ModelBase model) {
		this.customCape = new ModelRenderer[16];
		for (int i = 0; i < 16; i++) {
			ModelRenderer base = new ModelRenderer(model, 0, i);
			base.setTextureSize(64, 32);
			this.customCape[i] = base.addBox(-5.0F, i, -1.0F, 10, 1, 1);
		}
	}

	public void doRenderLayer(AbstractClientPlayer abstractClientPlayer, float paramFloat1, float paramFloat2,
			float deltaTick, float animationTick, float paramFloat5, float paramFloat6, float paramFloat7) {
		if (abstractClientPlayer.isInvisible())
			return;
		if (!abstractClientPlayer.hasPlayerInfo() || abstractClientPlayer.isInvisible())
			return;
		
//        if (!abstractClientPlayer.func_152122_n() || abstractClientPlayer.func_82150_aj() || !abstractClientPlayer.func_175148_a(EnumPlayerModelParts.CAPE) || abstractClientPlayer.func_110303_q() == null) {
//            return;
//        } // correct cape check. please use your config method/field files to correctly change the methods/fields being used if any are wrong.
		
//		if (WaveyCapesBase.config.capeMovement == CapeMovement.BASIC_SIMULATION) {
		CapeHolder holder = (CapeHolder) abstractClientPlayer;
		holder.updateSimulation((EntityPlayer) abstractClientPlayer, 16);
//		}
		this.playerRenderer.bindTexture(new ResourceLocation("tophat/cape.png")); // should use abstractClientPlayer.getLocationCape(), i didnt have a way to test this with a real cape so im rendering my own.
//		if (WaveyCapesBase.config.capeStyle == CapeStyle.SMOOTH) {
		this.smoothCapeRenderer.renderSmoothCape(this, abstractClientPlayer, deltaTick); // smooth mode, below is a blocky sort of cut up cape mode.
//		} else {
//			ModelRenderer[] parts = this.customCape;
//			for (int part = 0; part < 16; part++) {
//				ModelRenderer model = parts[part];
//				GlStateManager.pushMatrix();
//				modifyPoseStack(abstractClientPlayer, deltaTick, part);
//				model.render(0.0625F);
//				GlStateManager.popMatrix();
//			}
//		}
	}

	private void modifyPoseStack(AbstractClientPlayer abstractClientPlayer, float h, int part) {
//		if (WaveyCapesBase.config.capeMovement == CapeMovement.BASIC_SIMULATION) {
		modifyPoseStackSimulation(abstractClientPlayer, h, part);
//			return;
//		}
//		modifyPoseStackVanilla(abstractClientPlayer, h, part);
	}

	private void modifyPoseStackSimulation(AbstractClientPlayer abstractClientPlayer, float delta, int part) {
		StickSimulation simulation = ((CapeHolder) abstractClientPlayer).getSimulation();
		GlStateManager.translate(0.0D, 0.0D, 0.125D);
		float z = ((StickSimulation.Point) simulation.points.get(part)).getLerpX(delta)
				- ((StickSimulation.Point) simulation.points.get(0)).getLerpX(delta);
		if (z > 0.0F)
			z = 0.0F;
		float y = ((StickSimulation.Point) simulation.points.get(0)).getLerpY(delta) - part
				- ((StickSimulation.Point) simulation.points.get(part)).getLerpY(delta);
		float sidewaysRotationOffset = 0.0F;
		float partRotation = (float) -Math.atan2(y, z);
		partRotation = Math.max(partRotation, 0.0F);
		if (partRotation != 0.0F)
			partRotation = (float) (Math.PI - partRotation);
		partRotation = (float) (partRotation * 57.2958D);
		partRotation *= 2.0F;
		float height = 0.0F;
		if (abstractClientPlayer.isSneaking()) {
			height += 25.0F;
			GlStateManager.translate(0.0F, 0.15F, 0.0F);
		}
		float naturalWindSwing = getNatrualWindSwing(part);
		GlStateManager.rotate(6.0F + height + naturalWindSwing, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(-sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0.0F, y / 16.0F, z / 16.0F);
		GlStateManager.translate(0.0D, 0.03D, -0.03D);
		GlStateManager.translate(0.0F, part * 1.0F / 16.0F, (part * 0 / 16));
		GlStateManager.translate(0.0F, -part * 1.0F / 16.0F, (-part * 0 / 16));
		GlStateManager.translate(0.0D, -0.03D, 0.03D);
	}

	void modifyPoseStackVanilla(AbstractClientPlayer abstractClientPlayer, float h, int part) {
		GlStateManager.translate(0.0D, 0.0D, 0.125D);
		double d = Mth.lerp(h, abstractClientPlayer.prevChasingPosX, abstractClientPlayer.chasingPosX)
				- Mth.lerp(h, abstractClientPlayer.prevPosX, abstractClientPlayer.posX);
		double e = Mth.lerp(h, abstractClientPlayer.prevChasingPosY, abstractClientPlayer.chasingPosY)
				- Mth.lerp(h, abstractClientPlayer.prevPosY, abstractClientPlayer.posY);
		double m = Mth.lerp(h, abstractClientPlayer.prevChasingPosZ, abstractClientPlayer.chasingPosZ)
				- Mth.lerp(h, abstractClientPlayer.prevPosZ, abstractClientPlayer.posZ);
		float n = abstractClientPlayer.prevRenderYawOffset + abstractClientPlayer.renderYawOffset
				- abstractClientPlayer.prevRenderYawOffset;
		double o = Math.sin((n * 0.017453292F));
		double p = -Math.cos((n * 0.017453292F));
		float height = (float) e * 10.0F;
		height = MathHelper.clamp_float(height, -6.0F, 32.0F);
		float swing = (float) (d * o + m * p) * easeOutSine(0.0625F * part) * 100.0F;
		swing = MathHelper.clamp_float(swing, 0.0F, 150.0F * easeOutSine(0.0625F * part));
		float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
		sidewaysRotationOffset = MathHelper.clamp_float(sidewaysRotationOffset, -20.0F, 20.0F);
		float t = Mth.lerp(h, abstractClientPlayer.prevCameraYaw, abstractClientPlayer.cameraYaw);
		height = (float) (height + Math.sin((Mth.lerp(h, abstractClientPlayer.prevDistanceWalkedModified,
				abstractClientPlayer.distanceWalkedModified) * 6.0F)) * 32.0D * t);
		if (abstractClientPlayer.isSneaking()) {
			height += 25.0F;
			GlStateManager.translate(0.0F, 0.15F, 0.0F);
		}
		float naturalWindSwing = getNatrualWindSwing(part);
		GlStateManager.rotate(6.0F + swing / 2.0F + height + naturalWindSwing, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(-sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
	}

	float getNatrualWindSwing(int part) {
//		if (WaveyCapesBase.config.windMode == WindMode.WAVES) {
		long highlightedPart = System.currentTimeMillis() / 3L % 360L;
		float relativePart = (part + 1) / 16.0F;
		return (float) (Math.sin(Math.toRadians((relativePart * 360.0F - (float) highlightedPart))) * 3.0D);
//		}
//		return 0.0F;
	}

	private static float easeOutSine(float x) {
		return (float) Math.sin(x * Math.PI / 2.0D);
	}

	public boolean func_177142_b() {
		return false;
	}

	@Override
	public boolean shouldCombineTextures() {
		// TODO Auto-generated method stub
		return false;
	}
}
