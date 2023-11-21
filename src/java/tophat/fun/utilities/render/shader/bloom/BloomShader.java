package tophat.fun.utilities.render.shader.bloom;

import tophat.fun.utilities.Methods;
import org.lwjgl.opengl.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

public class BloomShader implements Methods {

    private int programId;
    private int vertShaderId;
    private int fragShaderId;

    public BloomShader() {
        programId = GL20.glCreateProgram();
        vertShaderId = loadShader("bloom.vert", GL20.GL_VERTEX_SHADER);
        fragShaderId = loadShader("bloom.frag", GL20.GL_FRAGMENT_SHADER);
        GL20.glAttachShader(programId, vertShaderId);
        GL20.glAttachShader(programId, fragShaderId);
        GL20.glLinkProgram(programId);
        GL20.glValidateProgram(programId);
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
            // Handle the exception if one occurs
            return null;
        }
    }

    public void cleanup() {
        GL20.glUseProgram(0);
        GL20.glDetachShader(programId, vertShaderId);
        GL20.glDetachShader(programId, fragShaderId);

        GL20.glDeleteShader(vertShaderId);
        GL20.glDeleteShader(fragShaderId);
        GL20.glDeleteProgram(programId);
    }

    public int getProgramId() {
        return programId;
    }

}
