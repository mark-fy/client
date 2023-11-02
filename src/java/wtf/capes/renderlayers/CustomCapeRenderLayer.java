package wtf.capes.renderlayers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import wtf.capes.CapeHolder;
import wtf.capes.sim.StickSimulation;
import wtf.capes.utils.Mth;
import wtf.tophat.Client;
import wtf.tophat.modules.impl.render.CustomCape;

public class CustomCapeRenderLayer implements LayerRenderer<AbstractClientPlayer> {

	private ModelRenderer[] customCape = new ModelRenderer[16];

	private final RenderPlayer playerRenderer;

	private final SmoothCapeRenderer smoothCapeRenderer = new SmoothCapeRenderer();

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

        if (!abstractClientPlayer.hasPlayerInfo() || abstractClientPlayer.isInvisible() || !abstractClientPlayer.isWearing(EnumPlayerModelParts.CAPE) || abstractClientPlayer.getLocationCape() == null) {
            return;
        }

		abstractClientPlayer.updateSimulation(abstractClientPlayer, 16);

		this.playerRenderer.bindTexture(new ResourceLocation("tophat/capes/TopHat.png"));

		if (Client.moduleManager.getByClass(CustomCape.class).isEnabled() && Client.moduleManager.getByClass(CustomCape.class).waveStyle.is("Smooth")) {
			this.smoothCapeRenderer.renderSmoothCape(this, abstractClientPlayer, deltaTick);
		} else if (Client.moduleManager.getByClass(CustomCape.class).waveStyle.is("Blocky")){
			ModelRenderer[] parts = this.customCape;
			for (int part = 0; part < 16; part++) {
				ModelRenderer model = parts[part];
				GlStateManager.pushMatrix();
				modifyPoseStack(abstractClientPlayer, deltaTick, part);
				model.render(0.0625F);
				GlStateManager.popMatrix();
			}
		} else {
			this.smoothCapeRenderer.renderSmoothCape(this, abstractClientPlayer, deltaTick);
		}

	}

	private void modifyPoseStack(AbstractClientPlayer abstractClientPlayer, float h, int part) {
		modifyPoseStackSimulation(abstractClientPlayer, h, part);
	}

	private void modifyPoseStackSimulation(AbstractClientPlayer abstractClientPlayer, float delta, int part) {
		StickSimulation simulation = abstractClientPlayer.getSharedSimulation();
		GlStateManager.translate(0.0D, 0.0D, 0.125D);
		float z = simulation.points.get(part).getLerpX(delta)
				- simulation.points.get(0).getLerpX(delta);
		if (z > 0.0F)
			z = 0.0F;
		float y = simulation.points.get(0).getLerpY(delta) - part
				- simulation.points.get(part).getLerpY(delta);
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
		GlStateManager.translate(0.0F, part * 1.0F / 16.0F, ((float) (0) / 16));
		GlStateManager.translate(0.0F, -part * 1.0F / 16.0F, ((float) (0) / 16));
		GlStateManager.translate(0.0D, -0.03D, 0.03D);
	}

	float getNatrualWindSwing(int part) {
		if (Client.moduleManager.getByClass(CustomCape.class).isEnabled() && Client.moduleManager.getByClass(CustomCape.class).windMode.is("Waves")) {
			long highlightedPart = System.currentTimeMillis() / 3L % 360L;
			float relativePart = (part + 1) / 16.0F;
			return (float) (Math.sin(Math.toRadians((relativePart * 360.0F - (float) highlightedPart))) * 3.0D);
		}
		return 0.0F;
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
