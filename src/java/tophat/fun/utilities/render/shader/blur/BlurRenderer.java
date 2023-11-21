package tophat.fun.utilities.render.shader.blur;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import tophat.fun.utilities.Methods;

import java.io.*;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;

public class BlurRenderer implements Methods {

    private int blurShaderProgram;

    public BlurRenderer() {
        initShader();
    }

    private void initShader() {
        // Load and compile
        int vertexShader = loadShader("blur.vert", GL20.GL_VERTEX_SHADER);
        int fragmentShader = loadShader("blur.frag", GL20.GL_FRAGMENT_SHADER);

        // Link the shaders into a program
        blurShaderProgram = glCreateProgram();
        glAttachShader(blurShaderProgram, vertexShader);
        glAttachShader(blurShaderProgram, fragmentShader);
        glLinkProgram(blurShaderProgram);

        // Check for linking errors
        if (glGetProgrami(blurShaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader program linking failed");
        }

        // delete the shaders
        glDetachShader(blurShaderProgram, vertexShader);
        glDetachShader(blurShaderProgram, fragmentShader);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }


    private int loadShader(String filename, int type) {
        int shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderId, Objects.requireNonNull(readFile(filename)));
        GL20.glCompileShader(shaderId);
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Shader compilation failed: " + GL20.glGetShaderInfoLog(shaderId, 1000));
            System.exit(-1);
        }
        return shaderId;
    }

    private String readFile(String filename) {
        try (InputStream inputStream = getClass().getResourceAsStream("/assets/minecraft/tophat/shaders/" + filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                return null;
            }

            // Read all lines from the file and join them into a single string
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void renderBlur(int baseTextureId) {
        glUseProgram(blurShaderProgram);

        glActiveTexture(GL13.GL_TEXTURE0);
        glBindTexture(GL11.GL_TEXTURE_2D, baseTextureId);

        // Set the baseTexture uniform in the shader
        int baseTextureUniform = glGetUniformLocation(blurShaderProgram, "baseTexture");
        glUniform1i(baseTextureUniform, 0);

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

        // Stop using the blur shader program
        glUseProgram(0);
    }

    public void cleanup() {
        glDeleteProgram(blurShaderProgram);
    }

    public void renderBlur(int baseTextureId, int x, int y, int width, int height) {
        GL20.glUseProgram(blurShaderProgram);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, baseTextureId);

        // Set the baseTexture uniform in the shader
        int baseTextureUniform = GL20.glGetUniformLocation(blurShaderProgram, "baseTexture");
        GL20.glUniform1i(baseTextureUniform, 0);

        // Render a quad for the specified region
        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 1.0f);
        glVertex2f(x, y);

        glTexCoord2f(1.0f, 1.0f);
        glVertex2f(x + width, y);

        glTexCoord2f(1.0f, 0.0f);
        glVertex2f(x + width, y + height);

        glTexCoord2f(0.0f, 0.0f);
        glVertex2f(x, y + height);
        glEnd();


        GL20.glUseProgram(0);
    }
}
