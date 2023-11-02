package wtf.capes.renderlayers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import wtf.capes.sim.StickSimulation;
import wtf.capes.utils.*;

public class SmoothCapeRenderer {
	public void renderSmoothCape(CustomCapeRenderLayer layer, AbstractClientPlayer abstractClientPlayer, float delta) {
        final WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        final PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        Matrix4f oldPositionMatrix = null;
        for (int part = 0; part < 16; ++part) {
            this.modifyPoseStack(layer, poseStack, abstractClientPlayer, delta, part);
            if (oldPositionMatrix == null) {
                oldPositionMatrix = poseStack.last().pose();
            }
            if (part == 0) {
                addTopVertex(worldrenderer, poseStack.last().pose(), oldPositionMatrix, 0.3f, 0.0f, 0.0f, -0.3f, 0.0f, -0.06f, part);
            }
            if (part == 15) {
                addBottomVertex(worldrenderer, poseStack.last().pose(), poseStack.last().pose(), 0.3f, (part + 1) * 0.06f, 0.0f, -0.3f, (part + 1) * 0.06f, -0.06f, part);
            }
            addLeftVertex(worldrenderer, poseStack.last().pose(), oldPositionMatrix, -0.3f, (part + 1) * 0.06f, 0.0f, -0.3f, part * 0.06f, -0.06f, part);
            addRightVertex(worldrenderer, poseStack.last().pose(), oldPositionMatrix, 0.3f, (part + 1) * 0.06f, 0.0f, 0.3f, part * 0.06f, -0.06f, part);
            addBackVertex(worldrenderer, poseStack.last().pose(), oldPositionMatrix, 0.3f, (part + 1) * 0.06f, -0.06f, -0.3f, part * 0.06f, -0.06f, part);
            addFrontVertex(worldrenderer, oldPositionMatrix, poseStack.last().pose(), 0.3f, (part + 1) * 0.06f, 0.0f, -0.3f, part * 0.06f, 0.0f, part);
            oldPositionMatrix = poseStack.last().pose();
            poseStack.popPose();
        }
        Tessellator.getInstance().draw();
	}

	void modifyPoseStack(CustomCapeRenderLayer layer, PoseStack poseStack, AbstractClientPlayer abstractClientPlayer,
			float h, int part) {

		modifyPoseStackSimulation(layer, poseStack, abstractClientPlayer, h, part);
	}

	private void modifyPoseStackSimulation(CustomCapeRenderLayer layer, PoseStack poseStack,
			AbstractClientPlayer abstractClientPlayer, float delta, int part) {
		StickSimulation simulation = abstractClientPlayer.getSharedSimulation();
		poseStack.pushPose();
		poseStack.translate(0.0D, 0.0D, 0.125D);
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
			poseStack.translate(0.0D, 0.15000000596046448D, 0.0D);
		}
		float naturalWindSwing = layer.getNatrualWindSwing(part);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(6.0F + height + naturalWindSwing));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
		poseStack.translate(0.0D, (y / 16.0F), (z / 16.0F));
		poseStack.translate(0.0D, 0.03D, -0.03D);
		poseStack.translate(0.0D, (part * 1.0F / 16.0F), ((double) (0) / 16));
		poseStack.mulPose(Vector3f.XP.rotationDegrees(-partRotation));
		poseStack.translate(0.0D, (-part * 1.0F / 16.0F), ((double) (0) / 16));
		poseStack.translate(0.0D, -0.03D, 0.03D);
	}

	private static void addBackVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1,
			float y1, float z1, float x2, float y2, float z2, int part) {
		if (x1 < x2) {
			float i = x1;
			x1 = x2;
			x2 = i;
		}
		if (y1 < y2) {
			float i = y1;
			y1 = y2;
			y2 = i;
			Matrix4f k = matrix;
			matrix = oldMatrix;
			oldMatrix = k;
		}
		float minU = 0.015625F;
		float maxU = 0.171875F;
		float minV = 0.03125F;
		float maxV = 0.53125F;
		float deltaV = maxV - minV;
		float vPerPart = deltaV / 16.0F;
		maxV = minV + vPerPart * (part + 1);
		minV += vPerPart * part;
		vertex(worldrenderer, oldMatrix, x1, y2, z1).tex(maxU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, oldMatrix, x2, y2, z1).tex(minU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, matrix, x2, y1, z2).tex(minU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, matrix, x1, y1, z2).tex(maxU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
	}

	private static void addFrontVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1,
			float y1, float z1, float x2, float y2, float z2, int part) {
		if (x1 < x2) {
			float i = x1;
			x1 = x2;
			x2 = i;
		}
		if (y1 < y2) {
			float i = y1;
			y1 = y2;
			y2 = i;
			Matrix4f k = matrix;
			matrix = oldMatrix;
			oldMatrix = k;
		}
		float minU = 0.1875F;
		float maxU = 0.34375F;
		float minV = 0.03125F;
		float maxV = 0.53125F;
		float deltaV = maxV - minV;
		float vPerPart = deltaV / 16.0F;
		maxV = minV + vPerPart * (part + 1);
		minV += vPerPart * part;
		vertex(worldrenderer, oldMatrix, x1, y1, z1).tex(maxU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, oldMatrix, x2, y1, z1).tex(minU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, matrix, x2, y2, z2).tex(minU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, matrix, x1, y2, z2).tex(maxU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
	}

	private static void addLeftVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1,
			float y1, float z1, float x2, float y2, float z2, int part) {
		if (x1 < x2) {
			float i = x1;
			x2 = i;
		}
		if (y1 < y2) {
			float i = y1;
			y1 = y2;
			y2 = i;
		}
		float minU = 0.0F;
		float maxU = 0.015625F;
		float minV = 0.03125F;
		float maxV = 0.53125F;
		float deltaV = maxV - minV;
		float vPerPart = deltaV / 16.0F;
		maxV = minV + vPerPart * (part + 1);
		minV += vPerPart * part;
		vertex(worldrenderer, matrix, x2, y1, z1).tex(maxU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, matrix, x2, y1, z2).tex(minU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, oldMatrix, x2, y2, z2).tex(minU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, oldMatrix, x2, y2, z1).tex(maxU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
	}

	private static void addRightVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1,
			float y1, float z1, float x2, float y2, float z2, int part) {
		if (x1 < x2) {
			float i = x1;
			x2 = i;
		}
		if (y1 < y2) {
			float i = y1;
			y1 = y2;
			y2 = i;
		}
		float minU = 0.171875F;
		float maxU = 0.1875F;
		float minV = 0.03125F;
		float maxV = 0.53125F;
		float deltaV = maxV - minV;
		float vPerPart = deltaV / 16.0F;
		maxV = minV + vPerPart * (part + 1);
		minV += vPerPart * part;
		vertex(worldrenderer, matrix, x2, y1, z2).tex(minU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, matrix, x2, y1, z1).tex(maxU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, oldMatrix, x2, y2, z1).tex(maxU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, oldMatrix, x2, y2, z2).tex(minU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
	}

	private static void addBottomVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1,
			float y1, float z1, float x2, float y2, float z2, int part) {
		if (x1 < x2) {
			float i = x1;
			x1 = x2;
			x2 = i;
		}
		if (y1 < y2) {
			float i = y1;
			y1 = y2;
			y2 = i;
		}
		float minU = 0.171875F;
		float maxU = 0.328125F;
		float minV = 0.0F;
		float maxV = 0.03125F;
		float deltaV = maxV - minV;
		float vPerPart = deltaV / 16.0F;
		maxV = minV + vPerPart * (part + 1);
		minV += vPerPart * part;
		vertex(worldrenderer, oldMatrix, x1, y2, z2).tex(maxU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, oldMatrix, x2, y2, z2).tex(minU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, matrix, x2, y1, z1).tex(minU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
		vertex(worldrenderer, matrix, x1, y1, z1).tex(maxU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
	}

	private static WorldRenderer vertex(WorldRenderer worldrenderer, Matrix4f matrix4f, float f, float g, float h) {
		Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
		vector4f.transform(matrix4f);
		worldrenderer.pos(vector4f.x(), vector4f.y(), vector4f.z());
		return worldrenderer;
	}

	private static void addTopVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1,
			float y1, float z1, float x2, float y2, float z2, int part) {
		if (x1 < x2) {
			float i = x1;
			x1 = x2;
			x2 = i;
		}
		if (y1 < y2) {
			float i = y1;
			y1 = y2;
			y2 = i;
		}
		float minU = 0.015625F;
		float maxU = 0.171875F;
		float minV = 0.0F;
		float maxV = 0.03125F;
		float deltaV = maxV - minV;
		float vPerPart = deltaV / 16.0F;
		maxV = minV + vPerPart * (part + 1);
		minV += vPerPart * part;
		vertex(worldrenderer, oldMatrix, x1, y2, z1).tex(maxU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
		vertex(worldrenderer, oldMatrix, x2, y2, z1).tex(minU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
		vertex(worldrenderer, matrix, x2, y1, z2).tex(minU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
		vertex(worldrenderer, matrix, x1, y1, z2).tex(maxU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
	}

}
