package net.pyraetos;

import org.joml.Vector3f;

public abstract class Vectors{

	public static Vector3f cross(Vector3f a, Vector3f b) {
		float x = a.y * b.z - a.z * b.y;
		float y = a.z * b.x - a.x * b.z;
		float z = a.x * b.y - a.y * b.x;
		return new Vector3f(x, y, z);
	}

	public static Vector3f to(Vector3f a, Vector3f b) {
		Vector3f dest = new Vector3f(0, 0, 0);
		b.sub(a, dest);
		return dest;
	}

	public static Vector3f average(Vector3f... vecs) {
		Vector3f dest = new Vector3f(0, 0, 0);
		for(Vector3f vec : vecs) {
			dest.add(vec, dest);
		}
		dest.div(vecs.length, dest);
		return dest.normalize();
	}
	
}
