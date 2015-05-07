package com.DarkEG.Core.Model;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.ResourceManager;
import com.DarkEG.Core.Texture.Texture;

public class Mesh {
	public static class Vertex {
		private Vector3f vert;
		private Vector3f norm;
		private Vector2f UV;
		private Vector3f boneID;
		private Vector3f boneWeight;
		public Vertex(Vector3f v, Vector3f n, Vector2f UV, Vector3f bID, Vector3f bWeight){
			vert = v;
			norm = n;
			this.UV = UV;
			boneID = bID;
			boneWeight = bWeight;
		}
		public Vector3f getVerts(){return vert;}
		public Vector3f getNorms(){return norm;}
		public Vector2f getUVs(){return UV;}
		public Vector3f getBoneIDs(){return boneID;}
		public Vector3f getBoneWeights(){return boneWeight;}
	}
	public static class Vertex2 {
		private Vector2f vert;
		public Vertex2(Vector2f pos){
			this.vert = pos;
		}
		public Vector2f getVerts(){return vert;}
	}
	public static class Face {
		private int v1, v2, v3;
		public Face(int v1, int v2, int v3){
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}
		public int getIndice(int indice){
			if(indice > 2) throw new IllegalArgumentException("Indice can't be greater then 3");
			else {
				switch(indice){
				case 0:
					return v1;
				case 1:
					return v2;
				case 2:
					return v3;
				default:
					return -1;
				}
			}
		}
	}
	private List<Vertex> vertices = new ArrayList<>();
	private List<Vertex2> verts = new ArrayList<>();
	private List<Face> faces = new ArrayList<>();
	private Skeleton s;
	private Texture t;
	private boolean hasSkeleton = false;
	private boolean hasVAO = false;
	private boolean hasCreatedVAO = false;
	private boolean is2D = false;
	private boolean hasTexture = false;
	private int ID;
	private int vao = -1, vboI = -1;
	
	public Mesh(List<Vertex> verts, List<Face> faces){
		this.vertices = verts;
		this.faces = faces;
	}
	public Mesh(List<Vertex2> verts){
		this.verts = verts;
		this.is2D = true;
	}
	public int getID(){return ID;}
	public void setID(int nID){ID = nID;}
	public static Mesh HudMesh(){
		List<Vertex2> verts = new ArrayList<>();
		verts.add(new Vertex2(new Vector2f(0, 0)));
		verts.add(new Vertex2(new Vector2f(1, 0)));
		verts.add(new Vertex2(new Vector2f(1, 1)));
		verts.add(new Vertex2(new Vector2f(0, 1)));
		Mesh m = new Mesh(verts);
		m.ID = ResourceManager.addMesh(m);
		m.createVAO();
		return m;
	}
	public static Mesh loadMeshDEG(String fileName){
		FileReader isr = null;
		File objFile = new File("res/" + fileName + ".deg");
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;
		boolean hasSkele = false;
		String skelFile = "";
		List<Vertex> vertices = new ArrayList<>();
		List<Face> faces = new ArrayList<>();
		List<Skeleton.Bone> bones = new ArrayList<>();
		Map<Skeleton.Bone, List<Integer>> boneChildIDs = new HashMap<>();
		try{
			while(true){
				line = reader.readLine();
				if(line.startsWith("B ")){
					String[] currentLine = line.split(" ");
					hasSkele = true;
					skelFile = currentLine[1];
				}
				if(line.startsWith("V ")){
					String[] currentLine = line.split(" ");
					Vector3f vert = new Vector3f(Float.valueOf(currentLine[1]), Float.valueOf(currentLine[2]), Float.valueOf(currentLine[3]));
					Vector3f norm = new Vector3f(Float.valueOf(currentLine[4]), Float.valueOf(currentLine[5]), Float.valueOf(currentLine[6]));
					Vector2f UV = new Vector2f(Float.valueOf(currentLine[7]), Float.valueOf(currentLine[8]));
					Vector3f BoneIDs = new Vector3f(Float.valueOf(currentLine[9]), Float.valueOf(currentLine[11]), Float.valueOf(currentLine[13]));
					Vector3f BoneWeights = new Vector3f(Float.valueOf(currentLine[10]), Float.valueOf(currentLine[12]), Float.valueOf(currentLine[14]));
					Vertex v = new Vertex(vert, norm, UV, BoneIDs, BoneWeights);
					vertices.add(v);
				} else if(line.startsWith("F ")){
					break;
				}
			}
			for(int i = 0; i < bones.size(); i++){
				Skeleton.Bone b = bones.get(i);
				List<Integer> childs = boneChildIDs.get(b);
				for(int child : childs){
					b.setChild(bones.get(child));
				}
			}
			boneChildIDs.clear();
			while(line != null && line.startsWith("F ")){
				String[] currentLine = line.split(" ");
				Face f = new Face(Integer.valueOf(currentLine[1]), Integer.valueOf(currentLine[2]), Integer.valueOf(currentLine[3]));
				faces.add(f);
			}
			reader.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		Mesh m = new Mesh(vertices, faces);
		if(hasSkele){
			m.setSkeleton(Skeleton.loadSkeleton(skelFile));
		}
		m.ID = ResourceManager.addMesh(m);
		return m;
	}
	public Mesh setSkeleton(Skeleton s){
		if(is2D){
			throw new RuntimeException("Cannot set Skeleton on a 2D image plane");
		} else if(hasCreatedVAO){
			throw new RuntimeException("Cannot set Skeleton on already loaded VAO");
		}
		this.s = s;
		return this;
	}
	public Mesh setTexture(Texture t){
		this.t = t;
		this.hasTexture = true;
		return this;
	}
	public Mesh createVAO(){
		if(!is2D){
			float[] verts = new float[vertices.size() * 3];
			float[] norms = new float[vertices.size() * 3];
			float[] UVs = new float[vertices.size() * 2];
			float[] BIDs = new float[vertices.size() * 3];
			float[] BWeights = new float[vertices.size() * 3];
			int[] indices = new int[faces.size() * 3];
			int currentPos = 0;
			for(Vertex v : vertices){
				verts[currentPos * 3] = v.getVerts().x;
				verts[currentPos * 3 + 1] = v.getVerts().y;
				verts[currentPos * 3 + 2] = v.getVerts().z;
				
				norms[currentPos * 3] = v.getNorms().x;
				norms[currentPos * 3 + 1] = v.getNorms().y;
				norms[currentPos * 3 + 2] = v.getNorms().z;

				UVs[currentPos * 2] = v.getUVs().x;
				UVs[currentPos * 2 + 1] = v.getUVs().y;
				if(hasSkeleton){
					BIDs[currentPos * 3] = v.getBoneIDs().x;
					BIDs[currentPos * 3 + 1] = v.getBoneIDs().y;
					BIDs[currentPos * 3 + 2] = v.getBoneIDs().z;
					
					BWeights[currentPos * 3] = v.getBoneWeights().x;
					BWeights[currentPos * 3 + 1] = v.getBoneWeights().y;
					BWeights[currentPos * 3 + 2] = v.getBoneWeights().z;
				} else {
					BIDs[currentPos * 3]
							= BIDs[currentPos * 3 + 1]
							= BIDs[currentPos * 3 + 2]
							= BWeights[currentPos * 3]
							= BWeights[currentPos * 3 + 1]
							= BWeights[currentPos * 3 + 2] = -1;
				}
				currentPos++;
			}
			currentPos = 0;
			for(Face f : faces){
				indices[currentPos * 3] = f.getIndice(0);
				indices[currentPos * 3 + 1] = f.getIndice(1);
				indices[currentPos * 3 + 2] = f.getIndice(2);
				currentPos++;
			}
			hasVAO = ResourceManager.loadVAO(indices, verts, norms, UVs, BIDs, BWeights, this);
		} else {
			
		}
		hasCreatedVAO = true;
		return this;
	}
	public void setIDs(int vao, int vbo){
		this.vao = vao;
		this.vboI = vbo;
	}
	public void update(){
		if(hasSkeleton){
			s.update();
		}
	}
	public void preLoad(){
		if(vao == -1){
			ResourceManager.bindVBO(vboI);
		} else {
			ResourceManager.bindVAO(vao);
			ResourceManager.bindVBO(vboI);
		}
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		if(hasTexture){
			t.bind();
		}
		if(hasSkeleton){
			glEnableVertexAttribArray(3);
			glEnableVertexAttribArray(4);
		}
	}
	public void render(){
		glDrawElements(GL_TRIANGLES, faces.size() * 3, GL_UNSIGNED_INT, 0);
	}
	public void postRender(){
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glDisableVertexAttribArray(4);

		ResourceManager.bindVBO(0);
		ResourceManager.bindVAO(0);
	}
}