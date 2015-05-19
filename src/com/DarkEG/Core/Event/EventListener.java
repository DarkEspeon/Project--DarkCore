package com.DarkEG.Core.Event;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventObject;

public class EventListener {
	Object source;
	Method handler = null;
	Class<? extends EventObject> E;
	WeakReference<Object> sourceReference;
	public EventListener(Class<? extends EventObject> E, Object source, String handlingMethod, boolean weakReference){
		if(weakReference){
			this.source = null;
			sourceReference = new WeakReference<Object>(source);
		} else {
			this.source = source;
			sourceReference = null;
		}
		@SuppressWarnings("rawtypes")
		Class C = E;
		while(EventObject.class.isAssignableFrom(C) && handler == null){
			try{
				this.handler = source.getClass().getMethod(handlingMethod, C);
			} catch (NoSuchMethodException e){
				C = C.getSuperclass();
			}
		}
		if(handler == null) throw new IllegalArgumentException("No method with the signature: " + handlingMethod + "(" + E.getSimpleName() + ") found.");
		this.E = E;
	}
	public EventListener(Class<? extends EventObject> E, Object source, String handlingMethod){
		this(E, source, handlingMethod, true);
	}
	void handleEvent(EventObject event){
		if(source == null){
			if(sourceReference.get() == null){
				deregisterListener();
				return;
			}
			try{
				handler.invoke(sourceReference.get(), event);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
				throw new IllegalArgumentException(e.getMessage(), e.getCause());
			}
		} else {
			try{
				handler.invoke(source, event);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
				throw new IllegalArgumentException(e.getMessage(), e.getCause());
			}
		}
	}
	public void registerListener(){
		EventManager.getInstance().addListener(E,  this);
	}
	public void deregisterListener(){
		EventManager.getInstance().removeListener(E, this);
	}
}
