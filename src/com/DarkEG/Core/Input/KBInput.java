package com.DarkEG.Core.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.DarkEG.Core.Util.ReferenceList;

public class KBInput {
	public static ReferenceList<String, Integer> keyTypes = new ReferenceList<>();
	private boolean[] KeyDown;
	private int numKeys;
	
	private Map<Integer, List<KBHandler>> recievers = new HashMap<>();
	private Map<Integer, List<Integer>> keyList = new HashMap<>();
	
	public KBInput(){
		numKeys = Keyboard.getKeyCount();
		KeyDown = new boolean[numKeys];
	}
	public void register(Integer type, KBHandler handle){
		if(recievers.containsKey(type)){
			List<KBHandler> reciever = recievers.get(type);
			reciever.add(handle);
		} else {
			List<KBHandler> reciever = new ArrayList<>();
			reciever.add(handle);
			recievers.put(type, reciever);
		}
	}
	public void registerKeys(Integer type, List<Integer> keys){
		if(keyList.containsKey(type)){
			List<Integer> temp = keyList.get(type);
			temp.addAll(keys);
		} else {
			keyList.put(type, keys);
		}
	}
	public List<Integer> getKeySet(Integer type){
		return keyList.get(type);
	}
	public void update(){
		for(int i = 0; i < numKeys; i++){
			KeyDown[i] =  Keyboard.isKeyDown(i);
		}
		for(Integer type : recievers.keySet()){
			List<KBHandler> handlers = recievers.get(type);
			String result = "";
			int iter = 0;
			for(Integer i : keyList.get(type)){
				result += "" + (KeyDown[i] ? 1 : 0);
				if(iter < keyList.get(type).size()){
					result += ";";
				}
				iter++;
			}
			for(KBHandler k : handlers){
				k.kbrecieve(type, result);
			}
		}
	}
}
