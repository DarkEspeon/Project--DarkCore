package com.DarkEG.Core.Util;

import java.util.ArrayList;
import java.util.List;

public class ReferenceList<X, Y> {
	private List<X> l1;
	private List<Y> l2;
	
	public ReferenceList(){
		l1 = new ArrayList<X>();
		l2 = new ArrayList<Y>();
	}
	public X getX(Y key){
		int index = 0;
		boolean matchFound = false;
		for(Y test : l2){
			if(test.equals(key)){
				matchFound = true;
				break;
			}
			index++;
		}
		if(matchFound) return l1.get(index);
		else return null;
	}
	public Y getY(X key){
		int index = 0;
		boolean matchFound = false;
		for(X test : l1){
			if(test.equals(key)){
				matchFound = true;
				break;
			}
			index++;
		}
		if(matchFound) return l2.get(index);
		else return null;
	}
	public List<X> getXList(){ return l1;}
	public List<Y> getYList(){ return l2;}
	public void add(X xkey, Y ykey){
		l1.add(xkey);
		l2.add(ykey);
	}
	public void add(List<X> xkeys, List<Y> ykeys){
		int smallest = 0;
		if(xkeys.size() < ykeys.size()) smallest = xkeys.size();
		else if(ykeys.size() < xkeys.size()) smallest = ykeys.size();
		else if(xkeys.size() == ykeys.size()) smallest = xkeys.size();
		for(int x = 0; x < smallest; x++){
			this.add(xkeys.get(x), ykeys.get(x));
		}
	}
}
