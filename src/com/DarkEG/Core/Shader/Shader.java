package com.DarkEG.Core.Shader;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.ResourceManager;

public class Shader {
	public static class SubShader{
		private int shaderID, type;
		public SubShader(String file, int type){
			this.type = type;
			StringBuilder shaderSource = new StringBuilder();
			try{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				while((line = reader.readLine()) != null){
					shaderSource.append(line).append("\n");
				}
				reader.close();
			} catch (Exception e){
				System.err.println("Cound not read file! " + file);
				e.printStackTrace();
				System.exit(-1);
			}
			shaderID = ResourceManager.createShader(type);
			glShaderSource(shaderID, shaderSource);
			glCompileShader(shaderID);
			if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE){
				System.out.println(glGetShaderInfoLog(shaderID, 500));
				System.err.println("Could not compile shader. FILENAME: " + file);
				System.exit(-1);
			}
		}
		public int getID(){return shaderID;}
		public int getType(){return type;}
	}
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	protected int programID;
	protected int ID;
	protected List<SubShader> shaders = new ArrayList<>();
	protected Map<String, Integer> uniforms = new HashMap<>();
	public Shader(){}
	public void start(){ glUseProgram(programID); }
	public void stop(){ glUseProgram(0); }
	public int getID(){return ID;}
	public void cleanUp(){
		stop();
		for(SubShader s : shaders){
			glDetachShader(programID, s.getID());
		}
		for(SubShader s : shaders){
			glDeleteShader(s.getID());
		}
		glDeleteProgram(programID);
	}
	public Shader loadUniform(String name, Matrix4f mat){
		mat.store(matrixBuffer);
		matrixBuffer.flip();
		glUniformMatrix4(uniforms.get(name), false, matrixBuffer);
		return this;
	}
	public Shader loadUniform(String name, float data){
		glUniform1f(uniforms.get(name), data);
		return this;
	}
	public Shader loadUniform(String name, Vector3f data){
		glUniform3f(uniforms.get(name), data.x, data.y, data.z);
		return this;
	}
	public Shader createProgram(){ programID = ResourceManager.createProgram(shaders); ID = ResourceManager.addShader(this); return this; }
	public Shader addSubShader(String file, int type){ shaders.add(new SubShader(file, type)); return this; }
	public Shader getUniform(String uniform){ uniforms.put(uniform, glGetUniformLocation(programID, uniform)); return this; }
	public Shader bindAttribute(int attribute, String variable){ glBindAttribLocation(programID, attribute, variable); return this; }
	public Shader finalizeProgram() { ResourceManager.finalizeProgram(programID); return this; }
}