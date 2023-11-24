/*
	Original Code by: sxmurxy2005 (https://github.com/sxmurxy2005/2D-Render-Util-1.16)
	Ported to 1.8.9 by: MarkGG
 */

package tophat.fun.utilities.render.shader;

import static org.lwjgl.opengl.GL11.GL_QUADS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import tophat.fun.utilities.Methods;

public class Shader implements Methods {
	
	public static final int VERTEX_SHADER;
	private int programId;
	
	static {
		VERTEX_SHADER = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(VERTEX_SHADER, getShaderSource("vertex.vert"));
		GL20.glCompileShader(VERTEX_SHADER);
	}
	
	public Shader(String fragmentShaderName) {
		int programId = GL20.glCreateProgram();
		try {
			int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
			GL20.glShaderSource(fragmentShader, getShaderSource(fragmentShaderName));
			GL20.glCompileShader(fragmentShader);
			
			int isFragmentCompiled = GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS);
			if(isFragmentCompiled == 0) {
				GL20.glDeleteShader(fragmentShader);
				System.err.println("Fragment shader couldn't compile. It has been deleted.");
			}

			GL20.glAttachShader(programId, VERTEX_SHADER);
			GL20.glAttachShader(programId, fragmentShader);
			GL20.glLinkProgram(programId);
		} catch(Exception e) {
			e.printStackTrace();
		}
		this.programId = programId;
	}
	
	public void load() {
		GL20.glUseProgram(programId);
	}
	
	public void unload() {
		GL20.glUseProgram(0);
	}

	public void setUniformf(String name, float... args) {
        int loc = GL20.glGetUniformLocation(programId, name);
        switch (args.length) {
            case 1:
				GL20.glUniform1f(loc, args[0]);
                break;
            case 2:
				GL20.glUniform2f(loc, args[0], args[1]);
                break;
            case 3:
				GL20.glUniform3f(loc, args[0], args[1], args[2]);
                break;
            case 4:
				GL20.glUniform4f(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    public void setUniformi(String name, int... args) {
    	int loc = GL20.glGetUniformLocation(programId, name);
        switch (args.length) {
            case 1:
                GL20.glUniform1i(loc, args[0]);
                break;
            case 2:
				GL20.glUniform2i(loc, args[0], args[1]);
                break;
            case 3:
				GL20.glUniform3i(loc, args[0], args[1], args[2]);
                break;
            case 4:
				GL20.glUniform4i(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

	public void setUniformfb(String name, FloatBuffer buffer) {
		GL20.glUniform1f(GL20.glGetUniformLocation(programId, name), buffer.get());
	}
    
    public static void draw() {
		draw(0, 0, mc.displayWidth, mc.displayHeight);
	}
    
	public static void draw(double x, double y, double width, double height) {
		GL11.glBegin(GL_QUADS);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2d(x, y);
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2d(x, y + height);
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2d(x + width, y + height);
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2d(x + width, y);
		GL11.glEnd();
	}
	
	public static String getShaderSource(String fileName) {
		String source = "";
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Shader.class.getResourceAsStream("/assets/minecraft/tophat/shaders/" + fileName)));
		source = bufferedReader.lines().filter(str -> !str.isEmpty()).map(str -> str = str.replace("\t", "")).collect(Collectors.joining("\n"));
		try {
			bufferedReader.close();
		} catch (IOException ignored) {
			
		}
		return source;
	}
	
}
