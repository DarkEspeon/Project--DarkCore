package com.DarkEG.Core.Util;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGB32F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import com.DarkEG.Core.Core;

public class FBO {
	private int id;
	private List<Integer> textures = new ArrayList<>();
	private int depthTexture;
	public FBO(){
		id = glGenFramebuffers();
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, id);
		
		depthTexture = Core.rm.tm.createTexture();
		glBindTexture(GL_TEXTURE_2D, depthTexture);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, Core.WIDTH, Core.HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (java.nio.FloatBuffer)null);
		glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	public void addColorAttachment(){
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, id);
		int texTemp = Core.rm.tm.createTexture();
		glBindTexture(GL_TEXTURE_2D, texTemp);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, Core.WIDTH, Core.HEIGHT, 0, GL_RGB, GL_FLOAT, (java.nio.FloatBuffer)null);
		glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + textures.size(), GL_TEXTURE_2D, texTemp, 0);
		textures.add(texTemp);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	public void finalizeBuffer(){
		bindDrawBuff();
		int[] temp = new int[textures.size()];
		for(int i = 0; i < textures.size(); i++){
			temp[i] = i;
		}
		setDrawBuffers(temp);
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
			System.out.println("FRAMEBUFFER ERROR");
		}
		bindDefaultBuffer();
	}
	public void bindDrawBuff(){ glBindFramebuffer(GL_DRAW_FRAMEBUFFER, id); }
	public void unbindDrawBuff(){ glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0); }
	public void bindReadBuff(){ glBindFramebuffer(GL_READ_FRAMEBUFFER, id); }
	public void unbindReadBuff(){ glBindFramebuffer(GL_READ_FRAMEBUFFER, 0); }
	public static void bindDefaultBuffer(){ glBindFramebuffer(GL_FRAMEBUFFER, 0); }
	public void setDrawBuffer(int attachmentnum){ glDrawBuffer(GL_COLOR_ATTACHMENT0 + attachmentnum); }
	public void setDrawBuffers(int[] attachments){
		for(int i = 0; i < attachments.length; i++){
			attachments[i] += GL_COLOR_ATTACHMENT0;
		}
		IntBuffer temp = BufferUtils.createIntBuffer(attachments.length);
		temp.put(attachments);
		temp.flip();
		glDrawBuffers(temp);
	}
	public void setReadBuffer(int attachmentnum){ glReadBuffer(GL_COLOR_ATTACHMENT0 + attachmentnum); }
	public void bindAttachTex(int activeTex, int attachnum){
		glActiveTexture(GL_TEXTURE0 + activeTex);
		glBindTexture(GL_TEXTURE_2D, textures.get(attachnum));
	}
	public void bindDepthTex(int activeTex){
		glActiveTexture(GL_TEXTURE0 + activeTex);
		glBindTexture(GL_TEXTURE_2D, depthTexture);
	}
}
