/*
	Original Code by: sxmurxy2005 (https://github.com/sxmurxy2005/2D-Render-Util-1.16)
	Ported to 1.8.9 by: MarkGG
 */

package tophat.fun.utilities.render.shader;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

import com.sun.prism.Texture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;
import tophat.fun.utilities.Methods;

public class ShaderUtil implements Methods {
	
	private static final HashMap<Integer, FloatBuffer> kernelCache = new HashMap<>();
 
 	public static FloatBuffer getKernel(int radius) {
 		FloatBuffer buffer = kernelCache.get(radius);
 		if(buffer == null) {
		 	buffer = BufferUtils.createFloatBuffer(radius);
		 	float[] kernel = new float[radius];
		 	float sigma = radius / 2.0F;
		 	float total = 0.0F;
		 	for(int i = 0; i < radius; i++) {
		 		float multiplier = i / sigma;
		 		kernel[i] = 1.0F / (Math.abs(sigma) * 2.50662827463F) * (float) Math.exp(-0.5 * multiplier * multiplier);
		 		total += i > 0 ? kernel[i] * 2 : kernel[0];
		 	}
		 	for (int i = 0; i < radius; i++) {
		 		kernel[i] /= total;
		 	}
		 	buffer.put(kernel);
		 	buffer.flip();
		 	kernelCache.put(radius, buffer);
 		}
 		return buffer;
 	}

	public static int loadTexture(BufferedImage image) throws Exception {
		int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length * 4);

		for (int pixel : pixels) {
			buffer.put((byte) ((pixel >> 16) & 0xFF));
			buffer.put((byte) ((pixel >> 8) & 0xFF));
			buffer.put((byte) (pixel & 0xFF));
			buffer.put((byte) ((pixel >> 24) & 0xFF));
		}
		buffer.flip();

		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		return textureID;
	}

	public static int getTextureId(ResourceLocation identifier, TextureManager textureManager) {
		ITextureObject textureObject = textureManager.getTexture(identifier);

		if (textureObject == null) {
			textureObject = new SimpleTexture(identifier);
			textureManager.loadTexture(identifier, textureObject);
		}

		if (textureObject instanceof SimpleTexture) {
			return textureObject.getGlTextureId();
		}

		return 0;
	}

	public static void initStencilReplace() {
		Framebuffer framebuffer = mc.getFramebuffer();
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		GL11.glColorMask(false, false, false, false);
	}
	
	public static void uninitStencilReplace() {
		GL11.glColorMask(true, true, true, true);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
	}
	
}
	