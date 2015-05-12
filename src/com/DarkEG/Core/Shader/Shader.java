package com.DarkEG.Core.Shader;

import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.Core;
import com.DarkEG.Core.Resources.ShaderManager.SubShader;

public class Shader {
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
	public Shader loadUniform(String name, int data){
		glUniform1i(uniforms.get(name), data);
		return this;
	}
	public Shader createProgram(){ programID = Core.core.rm.sm.createProgram(shaders); ID = Core.core.rm.sm.addShader(this); return this; }
	public Shader addSubShader(SubShader ss){ shaders.add(ss); return this; }
	public Shader getUniform(String uniform){ uniforms.put(uniform, glGetUniformLocation(programID, uniform)); return this; }
	public Shader bindAttribute(int attribute, String variable){ glBindAttribLocation(programID, attribute, variable); return this; }
	public Shader finalizeProgram() { Core.core.rm.sm.finalizeProgram(programID); return this; }
}