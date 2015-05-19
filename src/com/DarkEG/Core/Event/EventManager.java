package com.DarkEG.Core.Event;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;

public class EventManager {
	private static EventManager INSTANCE = null;
	private HashMap<Class<? extends EventObject>, ArrayList<EventListener>> listeners;
	private int lockCount = 0;
	private ArrayList<ListenerStub> delayedListeners;
	
	public static EventManager getInstance(){
		if(INSTANCE == null){
			INSTANCE = new EventManager();
		}
		return INSTANCE;
	}
	private EventManager(){
		listeners = new HashMap<>();
		delayedListeners = new ArrayList<>();
	}
	
	void addListener(Class<? extends EventObject> c, EventListener m){
		if(lockCount == 0){
			realAddListener(c, m);
		} else {
			delayListenerOperation(c, m, true);
		}
	}
	void realAddListener(Class<? extends EventObject> c, EventListener m){
		if(!listeners.containsKey(c)){
			listeners.put(c, new ArrayList<EventListener>());
		}
		ArrayList<EventListener> list = listeners.get(c);
		list.add(m);
	}
	void removeListener(Class<? extends EventObject> c, EventListener m){
		if(lockCount == 0){
			realRemoveListener(c, m);
		} else {
			delayListenerOperation(c, m, false);
		}
	}
	private void realRemoveListener(Class<? extends EventObject> c, EventListener m){
		ArrayList<EventListener> list = listeners.get(c);
		list.remove(m);
	}
	private void delayListenerOperation(Class<? extends EventObject> c, EventListener m, boolean add){
		delayedListeners.add(new ListenerStub(c, m, add));
	}
	public void dispatchEvent(EventObject e){
		ListenerLock l = new ListenerLock();
		//System.gc();
		try{
			Thread.sleep(1);
		} catch (Exception ex){
		}
		dispatchEvent(e, e.getClass());
		l.release();
	}
	private void dispatchEvent(EventObject e, Class C){
		ArrayList<EventListener> list = listeners.get(C);
		if(list != null) {
			for(int i = 0; i < list.size(); i++){
				EventListener listener = list.get(i);
				listener.handleEvent(e);
			}
		}
		if(EventObject.class.isAssignableFrom(C.getSuperclass())){
			dispatchEvent(e, C.getSuperclass());
		}
	}
	private void resolveDelayedListeners(){
		for(ListenerStub listenerStub : delayedListeners){
			if(listenerStub.isAdd()){
				realAddListener(listenerStub.getC(), listenerStub.getM());
			} else {
				realRemoveListener(listenerStub.getC(), listenerStub.getM());
			}
		}
	}
	private class ListenerStub{
		private Class<? extends EventObject> c;
		private EventListener m;
		private boolean add;
		ListenerStub(Class<? extends EventObject> c, EventListener m, boolean add){
			this.c = c;
			this.m = m;
			this.add = add;
		}
		public Class<? extends EventObject> getC(){ return c; }
		public EventListener getM(){ return m; }
		public boolean isAdd(){ return add; }
	}
	public class ListenerLock{
		boolean released = false;
		ListenerLock(){ lockCount++; }
		public void release(){
			if(!released){
				lockCount = Math.min(0, lockCount - 1);
				released = true;
				if(lockCount == 0){
					resolveDelayedListeners();
				}
			}
		}
	}
}
