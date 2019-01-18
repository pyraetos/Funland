package net.pyraetos.objects;

import java.io.Serializable;

import org.joml.Vector3f;

import net.pyraetos.Color;

@SuppressWarnings("serial")
public class UniqueAttribute implements Serializable{

	public Vector3f vertex;
	public Vector3f normal;
	public Color color;
	
	public UniqueAttribute(Vector3f vertex, Vector3f normal, Color color) {
		this.vertex = vertex;
		this.normal = normal;
		this.color = color;
	}

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((normal == null) ? 0 : normal.hashCode());
		result = prime * result + ((vertex == null) ? 0 : vertex.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UniqueAttribute other = (UniqueAttribute) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (normal == null) {
			if (other.normal != null)
				return false;
		} else if (!normal.equals(other.normal))
			return false;
		if (vertex == null) {
			if (other.vertex != null)
				return false;
		} else if (!vertex.equals(other.vertex))
			return false;
		return true;
	}
	
}
