/*
	Original Code by: sxmurxy2005 (https://github.com/sxmurxy2005/2D-Render-Util-1.16)
	Ported to 1.8.9 by: MarkGG
 */
package tophat.fun.utilities.render.shader;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import tophat.fun.utilities.Methods;

public class BloomHelper implements Methods {

	private static final Shader bloom = new Shader("bloom.frag");
	private static final Framebuffer inFrameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    private static final Framebuffer outFrameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    
    public static void draw(int radius) {
    	setupBuffer(inFrameBuffer);
    	setupBuffer(outFrameBuffer);
    	
    	inFrameBuffer.bindFramebuffer(true);
    	
    	outFrameBuffer.bindFramebuffer(true);
    	
    	bloom.load();
    	bloom.setUniformf("radius", radius);
    	bloom.setUniformi("sampler1", 0);
    	bloom.setUniformi("sampler2", 20);
    	bloom.setUniformfb("kernel", ShaderUtil.getKernel(radius));
    	bloom.setUniformf("texelSize", 1.0F / (float)mc.displayWidth, 1.0F / (float)mc.displayHeight);
    	bloom.setUniformf("direction", 2.0F, 0.0F);
    	
    	GlStateManager.enableBlend();
    	GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0001f);

		inFrameBuffer.bindFramebuffer(true);
	    Shader.draw();

		mc.getFramebuffer().bindFramebuffer(true);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    
	    bloom.setUniformf("direction", 0.0F, 2.0F);
	    
	    outFrameBuffer.bindFramebuffer(true);
		GL13.glActiveTexture(GL13.GL_TEXTURE20);
	    inFrameBuffer.bindFramebuffer(true);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
	    Shader.draw();
	    
	    bloom.unload();
		inFrameBuffer.unbindFramebuffer();
	    GlStateManager.disableBlend();
    }
    
    private static Framebuffer setupBuffer(Framebuffer frameBuffer) {
		if(frameBuffer.framebufferWidth != mc.displayWidth || frameBuffer.framebufferHeight != mc.displayHeight) {
			if(Minecraft.isRunningOnMac) {
				return null;
			}
		} else {
			frameBuffer.bindFramebuffer(true);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		}

		frameBuffer.bindFramebuffer(true);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		return frameBuffer;
	}
    
}
