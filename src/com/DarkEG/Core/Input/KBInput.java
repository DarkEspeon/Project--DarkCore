package com.DarkEG.Core.Input;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.DarkEG.Core.Event.EventManager;
import com.DarkEG.Core.Event.Object.KeyEvent;

public class KBInput {
	private Map<Integer, Boolean> KeyDown = new HashMap<>();
	int numKeys;
	
	public KBInput(){
		numKeys = Keyboard.getKeyCount();
	}
	public void update(){
		for(int i = 0; i < numKeys; i++){
			boolean temp = Keyboard.isKeyDown(i);
			if(KeyDown.containsKey(i) && KeyDown.get(i) != temp){
				KeyDown.put(i, temp);
				EventManager.getInstance().dispatchEvent(new KeyEvent(this, i, temp));
			} else if(!KeyDown.containsKey(i) && temp){
				KeyDown.put(i, temp);
				EventManager.getInstance().dispatchEvent(new KeyEvent(this, i, temp));
			}
		}
	}
}
