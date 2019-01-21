package net.pyraetos.objects;

import org.joml.Vector3f;
import net.pyraetos.Color;

@SuppressWarnings("serial")
public class TestQuad extends BasicMesh{

	public static final Vector3f[] VERTICES = {
			new Vector3f(-.5f, -.5f, .5f),
			new Vector3f(-.5f, .5f, .5f),
			new Vector3f(0f, 0f, .5f),
			new Vector3f(.5f, .5f, .5f),
			new Vector3f(.5f, -.5f, .5f),
	};
	
	public static final int[] INDICES = {0,1,2,1,2,3,3,2,4,4,2,0};
	
	public static final Vector3f[] NORMALS = {
			new Vector3f(-1f, -1f, 1f).normalize(),
			new Vector3f(-1f, 1f, 1f).normalize(),
			new Vector3f(-1f, 1f, 1f).normalize(),
			new Vector3f(1f, 1f, 1f).normalize(),
			new Vector3f(1f, -1f, 1f).normalize()
	};
	
	public static final Color[] COLORS = {
			new Color(0f, 0f, 1f),
			new Color(1f, 0f, 0f),
			new Color(.25f, .25f, .5f),
			new Color(0f, 1f, 0f),
			new Color(0f, 0f, 0f)
	};
	
	public static final UniqueAttribute[] UNIQUE_ATTRIBUTES;
	
	static {
		UNIQUE_ATTRIBUTES = new UniqueAttribute[INDICES.length];
		for(int i = 0; i < INDICES.length; i++) {
			int index = INDICES[i];
			UNIQUE_ATTRIBUTES[i] = new UniqueAttribute(new Vector3f(VERTICES[index]), new Vector3f(NORMALS[index]), new Color(COLORS[index]));
			INDICES[i] = i;
		}
	}
	
	public TestQuad() {
		super(UNIQUE_ATTRIBUTES, INDICES);
	}
}
