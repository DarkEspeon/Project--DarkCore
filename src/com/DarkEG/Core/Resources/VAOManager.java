package com.DarkEG.Core.Resources;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.DarkEG.Core.Model.Mesh;

public class VAOManager {
	
	private List<Integer> vaos = new ArrayList<>();
	private List<Integer> vbos = new ArrayList<>();
	
	public int createVAO(){
		int vao = glGenVertexArrays();
		vaos.add(vao);
		return vao;
	}
	public boolean bindVAO(int vao){
		glBindVertexArray(vao);
		return true;
	}
	public void unbindVAO(){
		glBindVertexArray(0);
	}
	private int bindIndicesBuffer(int[] indices){
		int vboID = createVBO();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = ResourceManager.createIntBuffer(indices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		return vboID;
	}
	private void storeDataInAttributeList(int attributeNumber, int coordSize, float[] data){
		int vboID = createVBO();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = ResourceManager.createFloatBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(attributeNumber, coordSize, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	public boolean loadVAO(int[] indices, float[] position, float[] norms, float[] uv, float[] bID, float[] bW, Mesh m){
		int vaoID = createVAO();
		
		bindVAO(vaoID);
		int vboID = bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, position);
		storeDataInAttributeList(1, 2, uv);
		storeDataInAttributeList(2, 3, norms);
		storeDataInAttributeList(3, 3, bID);
		storeDataInAttributeList(4, 3, bW);
		bindVAO(0);
		m.setIDs(vaoID, vboID);
		return (vaoID != -1);
	}
	public int createVBO(){
		int buf = glGenBuffers();
		vbos.add(buf);
		return buf;
	}
	public void bindVBO(int vbo){
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
	}
	public void cleanUp(){
		for(int i : vaos){
			glDeleteVertexArrays(i);
		}
		for(int i : vbos){
			glDeleteBuffers(i);
		}
	}
}
