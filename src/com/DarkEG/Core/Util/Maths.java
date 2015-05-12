package com.DarkEG.Core.Util;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.Entity.Entity;

public class Maths {
	private static float[] sins = new float[180];
	private static float[] coss = new float[180];
	private static float[] tans = new float[180];

	private static Matrix4f projectionMatrix = new Matrix4f();
	private static Matrix4f viewMat = new Matrix4f();
	private static final float FOV = 60;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	public static float sin(float degrees){
		return (float)Math.sin(Math.toRadians(degrees));
	}
	public static float cos(float degrees){
		return (float)Math.cos(Math.toRadians(degrees));
	}
	public static float tan(float degrees){
		return (float)Math.tan(Math.toRadians(degrees));
	}
	public static Matrix4f getViewMatrix(){return viewMat;}
	public static Matrix4f getProjectionMatrix(){ return projectionMatrix; }
	public static Vector3f getDeltaDir(Vector3f delta, float pitch, float yaw, float roll){
		return new Vector3f(0, 0, 0);
	}
	public static Matrix4f createTransformationMatrix(Entity e){
		return e.getModelMatrix();
	}
	public static void createViewMatrix(Entity e){
		viewMat = e.getViewMatrix();
	}
	public static void createProjectionMatrix(){
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = 1 / (float)Math.tan(Math.toRadians(FOV / 2));
		float x_scale = y_scale / aspectRatio;
		float frustem_length = FAR_PLANE - NEAR_PLANE;
		projectionMatrix.setIdentity();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustem_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustem_length);
		projectionMatrix.m33 = 0;
	}
	public static void setUp(){
		for(int i = 0; i < 180; i++){
			sins[i] = (float) Math.sin(Math.toRadians(i));
			coss[i] = (float) Math.cos(Math.toRadians(i));
			tans[i] = (float) Math.tan(Math.toRadians(i));
		}
	}
	public static Quaternion fromAxisAngle(float angle, Vector3f axis){
		axis.normalise();
		float s = Maths.sin(angle / 2);
		Quaternion res = new Quaternion();
		res.x = axis.x * s;
		res.y = axis.y * s;
		res.z = axis.z * s;
		res.w = Maths.cos(angle / 2);
		return res;
	}
	public static Vector3f getForwardAxis(Quaternion q){
		float x = 2 * q.x;
		float y = 2 * q.y;
		float z = 2 * q.z;
		float wx = x * q.w;
		float wy = y * q.w;
		float xx = x * q.x;
		float xz = z * q.x;
		float yy = y * q.y;
		float yz = z * q.y;
		return new Vector3f(xz + wy, yz - wx, 1 - (xx + yy));
	}
	public static Vector3f getUpAxis(Quaternion q){
		float x = 2 * q.x;
		float y = 2 * q.y;
		float z = 2 * q.z;
		float wx = x * q.w;
		float wz = z * q.w;
		float xx = x * q.x;
		float xy = y * q.x;
		float yz = z * q.y;
		float zz = z * q.z;
		return new Vector3f(xy - wz, 1 - (xx + zz), yz + wx);
	}
	public static Vector3f getRightAxis(Quaternion q){
		float y = 2 * q.y;
		float z = 2 * q.z;
		float wy = y * q.w;
		float wz = z * q.w;
		float xy = y * q.x;
		float xz = z * q.x;
		float yy = y * q.y;
		float zz = z * q.z;
		return new Vector3f(1 - (yy + zz), xy + wz, xz - wy);
	}
	public static Quaternion fromEuler(float yaw, float pitch, float roll){
		float c1 = Maths.cos(yaw / 2);
		float c2 = Maths.cos(roll / 2);
		float c3 = Maths.cos(pitch / 2);
		float s1 = Maths.sin(yaw / 2);
		float s2 = Maths.sin(roll / 2);
		float s3 = Maths.sin(pitch / 2);
		Quaternion res = new Quaternion();
		res.w = c1 * c2 * c3 - s1 * s2 * s3;
		res.x = s1 * s2 * c3 + c1 * c2 * s3;
		res.y = s1 * c2 * c3 + c1 * s2 * s3;
		res.z = c1 * s2 * c3 - s1 * c2 * s3;
		return res;
	}
	public static Matrix4f fromQuat(Quaternion q){
		return fromQuat(q, new Vector3f(0, 0, 0), false);
	}
	public static Matrix4f fromQuat(Quaternion q, Vector3f trans, boolean cam){
		if(!(q.lengthSquared() >= 1 - 0.01 && q.lengthSquared() <= 1 + 0.01)) q.normalise();
		float x = q.x + q.x;
		float y = q.y + q.y;
		float z = q.z + q.z;
		float wx = x * q.w;
		float wy = y * q.w;
		float wz = z * q.w;
		float xx = x * q.x;
		float xy = y * q.x;
		float xz = z * q.x;
		float yy = y * q.y;
		float yz = z * q.y;
		float zz = z * q.z;
		Matrix4f res = new Matrix4f();
		res.m00 = 1 - (yy + zz);
		res.m01 = xy - wz;
		res.m02 = xz + wy;
		res.m10 = xy + wz;
		res.m11 = 1 - (xx + zz);
		res.m12 = yz - wx;
		res.m20 = xz - wy;
		res.m21 = yz + wx;
		res.m22 = 1 - (xx + yy);
		Matrix4f temp = new Matrix4f();
		temp.translate(trans);
		if(cam) res = Matrix4f.mul(res, temp, res);
		else res = Matrix4f.mul(temp, res, res);
		return res;
	}
	public static float getLightDist(Vector3f col, Vector3f att){
		float maxCol = Math.max(Math.max(col.x, col.y), col.z);
		float sqrt = att.y * att.y - 4 * att.z * (att.z - 256 * maxCol);
		float dist = (float) ((-att.y + Math.sqrt(sqrt))/(2 * att.z));
		return dist;
	}
}
