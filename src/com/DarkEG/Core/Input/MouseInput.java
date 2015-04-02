package com.DarkEG.Core.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;

import com.DarkEG.Core.Util.ReferenceList;

public class MouseInput {
	public static ReferenceList<String, Integer> keyTypes = new ReferenceList<>();
	private boolean[] ButtonDown;
	private static int numButtons;
	private float x, y, scroll, dx, dy;
	
	public static final int X = numButtons + 1;
	public static final int Y = numButtons + 2;
	public static final int SCROLL = numButtons + 3;
	public static final int DX = numButtons + 4;
	public static final int DY = numButtons + 5;
	
	private Map<Integer, List<MouseHandler>> recievers = new HashMap<>();
	private Map<Integer, List<Integer>> outputList = new HashMap<>();
	public MouseInput(){
		if(numButtons < 0) numButtons = Mouse.getButtonCount();
		Mouse.setGrabbed(true);
		ButtonDown = new boolean[numButtons];
	}
	public void register(Integer type, MouseHandler handle){
		if(recievers.containsKey(type)){
			List<MouseHandler> reciever = recievers.get(type);
			reciever.add(handle);
		} else {
			List<MouseHandler> reciever = new ArrayList<>();
			reciever.add(handle);
			recievers.put(type, reciever);
		}
	}
	public void registerData(Integer type, List<Integer> info){
		if(outputList.containsKey(type)){
			List<Integer> temp = outputList.get(type);
			temp.addAll(info);
		} else {
			outputList.put(type, info);
		}
	}
	public void update(){
		for(int i = 0; i < numButtons; i++){
			ButtonDown[i] = Mouse.isButtonDown(i);
		}
		x = Mouse.getX();
		y = Mouse.getY();
		scroll = Mouse.getDWheel();
		dx = Mouse.getDX();
		dy = Mouse.getDY();
		
		for(Integer type : recievers.keySet()){
			List<MouseHandler> handlers = recievers.get(type);
			String result = "";
			int iter = 0;
			for(int i : outputList.get(type)){
				if(i <= numButtons) result += "" + (ButtonDown[i] ? 1 : 0);
				else {
					if(i == X) result += "X:" + x;
					if(i == Y) result += "Y:" + y;
					if(i == SCROLL) result += "S:" + scroll;
					if(i == DX) result += "DX:" + dx;
					if(i == DY) result += "DY:" + dy;
				}
				if(iter < outputList.get(type).size()){
					result += ";";
				}
				iter++;
			}
			for(MouseHandler m : handlers){
				m.mouserecieve(type, result);
			}
		}
	}
}
