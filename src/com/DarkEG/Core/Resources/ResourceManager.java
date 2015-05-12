package com.DarkEG.Core.Resources;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import com.DarkEG.Core.Model.Mesh;

public class ResourceManager {
	public TextureManager tm = new TextureManager();
	public ShaderManager sm = new ShaderManager();
	public VAOManager vm = new VAOManager();
	
	private static List<Mesh> meshes = new ArrayList<>();
	
	public static int addMesh(Mesh m){
		int retval = meshes.size();
		meshes.add(m);
		return retval;
	}
	public static Mesh getMesh(int MeshID){
		return meshes.get(MeshID);
	}
	
	public static FloatBuffer createFloatBuffer(float[] data){
		return (FloatBuffer) BufferUtils.createFloatBuffer(data.length).put(data).flip();
	}
	public static IntBuffer createIntBuffer(int[] data){
		return (IntBuffer) BufferUtils.createIntBuffer(data.length).put(data).flip();
	}
	public static ByteBuffer createByteBuffer(byte[] data){
		return (ByteBuffer) BufferUtils.createByteBuffer(data.length).put(data).flip();
	}
	
	public void cleanUp(){
		tm.cleanUp();
		sm.cleanUp();
		vm.cleanUp();
	}
}
