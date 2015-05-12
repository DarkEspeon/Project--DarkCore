package com.DarkEG.Core.Resources;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.DarkEG.Core.Texture.Texture;

public class TextureManager {
	private List<Texture> textures = new ArrayList<>();
	
	public static final int TEX2D = GL_TEXTURE_2D;
	
	public int createTexture(){
		return glGenTextures();
	}
	public int addTexture(Texture t){
		int addID = textures.size();
		textures.add(t);
		return addID;
	}
	public Texture loadTexture(int target, String filename){
		int id = createTexture();
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + filename + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] temp = new byte[image.getWidth() * image.getHeight() * 4];
		int pixnum = 0;
		for(int y = 0; y < image.getHeight(); y++){
			for(int x = 0; x < image.getWidth(); x++){
				int pixel = image.getRGB(x, y);
				temp[pixnum * 4 + 0] = (byte) ((pixel >> 16) & 0xFF);
				temp[pixnum * 4 + 1] = (byte) ((pixel >> 8) & 0xFF);
				temp[pixnum * 4 + 2] = (byte) (pixel & 0xFF);
				temp[pixnum * 4 + 3] = (byte) ((pixel >> 24) & 0xFF);
				pixnum++;
			}
		}
		ByteBuffer buff = ResourceManager.createByteBuffer(temp);
		bindTexture(target, id);
		glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(target, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buff);
		unbindTexture(target);
		Texture t = new Texture(id, target);
		addTexture(t);
		return t;
	}
	public Texture getTexture(int id){
		return textures.get(id);
	}
	public void bindTexture(int target, int id){
		glBindTexture(target, id);
	}
	public void unbindTexture(int target){
		glBindTexture(target, 0);
	}
	public void bindTexture(int target, int id, int texSlot){
		glActiveTexture(GL_TEXTURE0 + texSlot);
		glBindTexture(target, id);
	}
	public void unbindTexture(int target, int texSlot){
		glActiveTexture(GL_TEXTURE0 + texSlot);
		glBindTexture(target, 0);
	}
	public void cleanUp(){
		for(int i = 0; i < textures.size(); i++){
			GL11.glDeleteTextures(i);
		}
	}
}
