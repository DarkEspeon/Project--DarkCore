package com.DarkEG.Core;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL30;

import com.DarkEG.Core.Model.Mesh;
import com.DarkEG.Core.Shader.Shader;
import com.DarkEG.Core.Shader.Shader.SubShader;
import com.DarkEG.Core.Texture.Texture;

public class ResourceManager {
	private static List<Integer> vaos = new ArrayList<>();
	private static List<Integer> vbos = new ArrayList<>();
	private static List<Texture> textures = new ArrayList<>();
	private static List<Shader> shaders = new ArrayList<>();
	private static List<Mesh> meshes = new ArrayList<>();
	
	//start Texture creation
	public static int createTexture(){
		return glGenTextures();
	}
	public static int addTexture(Texture t){
		int addID = textures.size();
		textures.add(t);
		return addID;
	}
	public static Texture getTexture(int id){
		return textures.get(id);
	}
	//end Texture creation
	//start Shader creation
	public static int createShader(int type){
		return glCreateShader(type);
	}
	public static int createProgram(List<SubShader> shaders){
		int programID = glCreateProgram();
		for(SubShader sub : shaders){
			glAttachShader(programID, sub.getID());
		}
		return programID;
	}
	public static void finalizeProgram(int progID){
		glLinkProgram(progID);
		glValidateProgram(progID);
	}
	public static int addShader(Shader s){
		int ret = shaders.size();
		shaders.add(s);
		return ret;
	}
	public static Shader getShader(int id){
		return shaders.get(id);
	}
	//end Shader creation
	//start VAO + VBO creation
	public static int createVBO(){
		int buf = glGenBuffers();
		vbos.add(buf);
		return buf;
	}
	public static int createVAO(){
		int vao = -1;
		if(Core.OGL30) vao = ResourceManager.createOGLVAO();
		else if(Core.ARBVAO) vao = ResourceManager.createARBVAO();
		return vao;
	}
	//end VAO + VBO creation
	//start VBO Binding
	private static int bindIndicesBuffer(int[] indices){
		int vboID = createVBO();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = createIntBuffer(indices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		return vboID;
	}
	private static void storeDataInAttributeList(int attributeNumber, int coordSize, float[] data){
		int vboID = createVBO();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = createFloatBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(attributeNumber, coordSize, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	public static boolean loadVAO(int[] indices, float[] position, float[] norms, float[] uv, float[] bID, float[] bW, Mesh m){
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
	//end VBO Binding
	//start VAO Creation
	private static int createOGLVAO(){
		int vao = GL30.glGenVertexArrays();
		vaos.add(vao);
		return vao;
	}
	private static int createARBVAO(){
		int vao = ARBVertexArrayObject.glGenVertexArrays();
		vaos.add(vao);
		return vao;
	}
	public static int addMesh(Mesh m){
		int retval = meshes.size();
		meshes.add(m);
		return retval;
	}
	public static Mesh getMesh(int MeshID){
		return meshes.get(MeshID);
	}
	//end VAO Creation
	//start VAO Binding
	public static boolean bindVAO(int vao){
		if(Core.OGL30){
			GL30.glBindVertexArray(vao);
			return true;
		}
		else if(Core.ARBVAO){
			ARBVertexArrayObject.glBindVertexArray(vao);
			return true;
		}
		return false;
	}
	public static void bindVBO(int vbo){
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
	}
	public static void unbindVAO(){
		if(Core.OGL30) GL30.glBindVertexArray(0);
		else if(Core.ARBVAO) ARBVertexArrayObject.glBindVertexArray(0);
	}
	//end VAO Binding
	//start Buffer Stuff
	public static FloatBuffer createFloatBuffer(float[] data){
		return (FloatBuffer) BufferUtils.createFloatBuffer(data.length).put(data).flip();
	}
	public static IntBuffer createIntBuffer(int[] data){
		return (IntBuffer) BufferUtils.createIntBuffer(data.length).put(data).flip();
	}
	public static ByteBuffer createByteBuffer(byte[] data){
		return (ByteBuffer) BufferUtils.createByteBuffer(data.length).put(data).flip();
	}
	//end Buffer Stuff
	
	public static void cleanUp(){
		for(int vao : vaos){
			if(Core.OGL30) GL30.glDeleteVertexArrays(vao);
			else if(Core.ARBVAO) ARBVertexArrayObject.glDeleteVertexArrays(vao);
		}
		for(int vbo : vbos){
			glDeleteBuffers(vbo);
		}
		for(int i = 0; i < textures.size(); i++){
			glDeleteTextures(i);
		}
		for(Shader s : shaders){
			s.cleanUp();
		}
	}
}
