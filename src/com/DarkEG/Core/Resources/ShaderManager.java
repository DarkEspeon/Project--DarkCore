package com.DarkEG.Core.Resources;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.DarkEG.Core.Shader.Shader;

public class ShaderManager {
	public static class SubShader{
		int shaderID, type;
		public SubShader(int id, int t){
			this.shaderID = id;
			this.type = t;
		}
		public int getID() {return shaderID;}
	}
	private List<Shader> shaders = new ArrayList<>();
	
	public static final int FRAG = GL_FRAGMENT_SHADER;
	public static final int VERT = GL_VERTEX_SHADER;
	
	public int createShader(int type){
		return glCreateShader(type);
	}
	public void addSubShader(Shader s, String file, int type){
		int shaderID = -1;
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
		shaderID = createShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE){
			System.out.println(glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader. FILENAME: " + file);
			System.exit(-1);
		}
		SubShader ss = new SubShader(shaderID, type);
		s.addSubShader(ss);
	}
	public int createProgram(List<SubShader> shaders){
		int programID = glCreateProgram();
		for(SubShader sub : shaders){
			glAttachShader(programID, sub.getID());
		}
		return programID;
	}
	public void finalizeProgram(int progID){
		glLinkProgram(progID);
		glValidateProgram(progID);
	}
	public int addShader(Shader s){
		int ret = shaders.size();
		shaders.add(s);
		return ret;
	}
	public Shader getShader(int id){
		return shaders.get(id);
	}
	public void cleanUp(){
		for(Shader s : shaders){
			s.cleanUp();
		}
	}
}
