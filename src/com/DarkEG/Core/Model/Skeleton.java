package com.DarkEG.Core.Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.Util.Maths;

public class Skeleton {
	public static class Bone{
		private static int MaxChildren = 8;
		
		private Bone parent = null;
		private Bone[] child = new Bone[MaxChildren];
		
		private int currentChildren = 0;
		
		private Vector3f rot;
		private Vector3f minRot;
		private Vector3f maxRot;
		private Vector3f pos;
		private float length;
		
		public Bone(Vector3f pos, Vector3f minRot, Vector3f maxRot, float length){
			this.pos = pos;
			float nrotx = 0, nroty = 0, nrotz = 0;
			if(minRot.x > 0){
				nrotx = minRot.x;
			} else if(maxRot.x < 0){
				nrotx = maxRot.x;
			}
			if(minRot.y > 0){
				nroty = minRot.y;
			} else if(maxRot.y < 0){
				nroty = maxRot.y;
			}
			if(minRot.z > 0){
				nrotz = minRot.z;
			} else if(maxRot.z < 0){
				nrotz = maxRot.z;
			}
			this.rot = new Vector3f(nrotx, nroty, nrotz);
			this.minRot = minRot;
			this.maxRot = maxRot;
			this.length = length;
		}
		
		public Bone setParent(Bone b){
			this.parent = b;
			return this;
		}
		
		public Bone setChild(Bone b){
			if(currentChildren < MaxChildren && (b.parent == null)){
				this.child[currentChildren] = b;
				currentChildren++;
				b.setParent(this);
			}
			return this;
		}
		public void move(){
			float dx = 0, dy = 0, dz = 0;
			Vector3f deltadir = new Vector3f(dx, dy, dz);
			//Vector3f newDeltaDir = Maths.mul(deltadir, rot.x, rot.y, rot.z);
			Vector3f childPos = new Vector3f();
			//Vector3f.add(pos, newDeltaDir, childPos);
			for(int i = 0; i < currentChildren; i++){
				if(child[i] != null){
					child[i].changePos(childPos);
					child[i].move();
				} else {
					continue;
				}
			}
		}
		public void changePos(Vector3f pos){
			this.pos = pos;
		}
	}
	private List<Bone> bones = new ArrayList<>();
	public Skeleton(List<Bone> bones){
		this.bones = bones;
	}
	public Skeleton(Skeleton s){
		this.bones = s.bones;
	}
	public void update(){
		
	}
	public static Skeleton loadSkeleton(String file){
		FileReader isr = null;
		File objFile = new File("res/" + file + ".skel");
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;
		List<Skeleton.Bone> bones = new ArrayList<>();
		Map<Skeleton.Bone, List<Integer>> boneChildIDs = new HashMap<>();
		try {
			while((line = reader.readLine()) != null){
				if(line.startsWith("B ")){
					String[] currentLine = line.split(" ");
					Skeleton.Bone bone = new Skeleton.Bone(new Vector3f(Float.valueOf(currentLine[1]), Float.valueOf(currentLine[2]), Float.valueOf(currentLine[3])), new Vector3f(Float.valueOf(currentLine[5]), Float.valueOf(currentLine[6]), Float.valueOf(currentLine[7])), new Vector3f(Float.valueOf(currentLine[8]),Float.valueOf(currentLine[9]),Float.valueOf(currentLine[10])), Float.valueOf(currentLine[4]));
					bones.add(bone);
					List<Integer> ids = new ArrayList<>();
					for(int i = 0; i < 8; i++){
						int temp = Integer.valueOf(currentLine[i + 11]);
						if(temp != -1){
							ids.add(temp);
						}
					}
					boneChildIDs.put(bone, ids);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < bones.size(); i++){
			Skeleton.Bone b = bones.get(i);
			List<Integer> childs = boneChildIDs.get(b);
			for(int child : childs){
				b.setChild(bones.get(child));
			}
		}
		return new Skeleton(bones);
	}
}
