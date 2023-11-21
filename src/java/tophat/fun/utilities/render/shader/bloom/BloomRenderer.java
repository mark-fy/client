package tophat.fun.utilities.render.shader.bloom;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import tophat.fun.utilities.Methods;

import static org.lwjgl.opengl.GL11.*;

public class BloomRenderer implements Methods {

    private final BloomShader bloomShader;
    private int sceneFbo;
    private int sceneTextureId;

    public BloomRenderer() {
        bloomShader = new BloomShader();
        setupSceneFbo();
    }

    private void setupSceneFbo() {
        // Create FBO
        sceneFbo = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, sceneFbo);

        // Create texture to render scene
        sceneTextureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sceneTextureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, mc.displayWidth, mc.displayHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, sceneTextureId, 0);

        // Check for framebuffer completeness
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete!");
        }

        // Unbind FBO
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void renderScene() {
        // Bind the framebuffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, sceneFbo);

        // Render the scene to the framebuffer

        // Unbind the framebuffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        // Render the bloom effect
        renderBloom();
    }

    private void renderBloom() {
        GL20.glUseProgram(bloomShader.getProgramId());

        // Bind the texture containing the scene
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sceneTextureId);

        // Set the baseTexture uniform in the shader
        int baseTextureUniform = GL20.glGetUniformLocation(bloomShader.getProgramId(), "baseTexture");
        GL20.glUniform1i(baseTextureUniform, 0);

        // Render a fullscreen quad
        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 1.0f);
        glVertex2f(-1.0f, -1.0f);

        glTexCoord2f(1.0f, 1.0f);
        glVertex2f(1.0f, -1.0f);

        glTexCoord2f(1.0f, 0.0f);
        glVertex2f(1.0f, 1.0f);

        glTexCoord2f(0.0f, 0.0f);
        glVertex2f(-1.0f, 1.0f);
        glEnd();

        // Stop using the bloom shader program
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        GL30.glDeleteFramebuffers(sceneFbo);
        GL11.glDeleteTextures(sceneTextureId);
        bloomShader.cleanup();
    }

}
