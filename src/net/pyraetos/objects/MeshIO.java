package net.pyraetos.objects;

import static net.pyraetos.Options.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3i;

import net.pyraetos.Color;
import net.pyraetos.util.Sys;

public abstract class MeshIO{
	
	public static BasicMesh loadOBJ(String path) {
		return loadOBJ(path, null, null);
	}
	
	public static BasicMesh loadOBJ(String path, Color[] colors, String[] coloredObjects) {
		File file = new File(path + ".obj");
		List<Vector3f> vertexList = new ArrayList<Vector3f>();
		List<Integer> indexList = new ArrayList<Integer>();
		List<Vector3f> normalList = new ArrayList<Vector3f>();
		List<Integer> normalIndexList = new ArrayList<Integer>();
		List<ColorAssignment> colorAssignments = new ArrayList<ColorAssignment>();
		if(!file.exists()) {
			Sys.error("OBJ " + path + " not found!");
			System.exit(1);
		}
		if(colors == null) colors = new Color[0];
		if(coloredObjects == null) coloredObjects = new String[0];
		if(colors.length != coloredObjects.length) {
			Sys.error("Error loading " + path + "! Number of colors must match number of colored objects!");
			System.exit(1);
		}
		try {
			colorAssignments.add(new ColorAssignment(DEFAULT_MESH_COLOR));
			int curCA = 0;
			boolean usingDefaultColor = true;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("o ")) {
					line = line.substring(2);
					int index = -1;
					for(int i = 0; i < colors.length; i++) {
						if(coloredObjects[i].equals(line)) {
							index = i;
							break;
						}
					}
					if(index != -1) { 
						if(usingDefaultColor) {
							colorAssignments.get(curCA).color = colors[index];
							usingDefaultColor = false;
						}else{
							colorAssignments.add(new ColorAssignment(colors[index]));
							curCA++;
						}
					}
				}else
				if(line.startsWith("v ")) {
					line = line.substring(2);
					String rawFloats[] = line.split(" ");
					Vector3f vertex = new Vector3f(Float.parseFloat(rawFloats[0]), Float.parseFloat(rawFloats[1]), Float.parseFloat(rawFloats[2]));
					vertexList.add(vertex);
				}else
				if(line.startsWith("vn ")) {
					line = line.substring(3);
					String rawFloats[] = line.split(" ");
					Vector3f normal = new Vector3f(Float.parseFloat(rawFloats[0]), Float.parseFloat(rawFloats[1]), Float.parseFloat(rawFloats[2]));
					normalList.add(normal);
				}else
				if(line.startsWith("f ")){
					line = line.substring(2);
					String rawInts[] = line.split(" ");
					Vector3i vFace = new Vector3i(Integer.parseInt(rawInts[0].split("//")[0]),
													Integer.parseInt(rawInts[1].split("//")[0]),
													Integer.parseInt(rawInts[2].split("//")[0]));
					Vector3i nFace = new Vector3i(Integer.parseInt(rawInts[0].split("//")[1]),
							Integer.parseInt(rawInts[1].split("//")[1]),
							Integer.parseInt(rawInts[2].split("//")[1]));
					indexList.add(vFace.z-1);
					indexList.add(vFace.y-1);
					indexList.add(vFace.x-1);
					normalIndexList.add(nFace.z-1);
					normalIndexList.add(nFace.y-1);
					normalIndexList.add(nFace.x-1);
					ColorAssignment ca = colorAssignments.get(curCA);
					int min = Sys.min(vFace.z-1, vFace.y-1, vFace.x-1);
					int max = Sys.max(vFace.z-1, vFace.y-1, vFace.x-1);
					if(ca.beginIndex == -1 || ca.beginIndex > min) ca.beginIndex = min;
					if(ca.endIndex == -1 || ca.endIndex < max) ca.endIndex = max;
				}
			}
			reader.close();
			
			Map<UniqueAttribute, Integer> uaMap = new HashMap<UniqueAttribute, Integer>();
			List<UniqueAttribute> uaList = new ArrayList<UniqueAttribute>();
			int indexArray[] = new int[indexList.size()];
			
			for(int i = 0; i < indexList.size(); i++) {
				int index = indexList.get(i);
				int normalIndex = normalIndexList.get(i);
				Vector3f vertex = vertexList.get(index);
				Vector3f normal = normalList.get(normalIndex);
				Color c = DEFAULT_MESH_COLOR;
				for(ColorAssignment ca : colorAssignments) {
					if(ca.color.r == 0.0) {
						Sys.debug(ca.beginIndex + " " + ca.endIndex + " " + index);
					}
					if(index < ca.beginIndex) continue;
					if(index > ca.endIndex) continue;
					c = ca.color;
				}
				UniqueAttribute ua = new UniqueAttribute(vertex, normal, c);
				if(!uaMap.containsKey(ua)) {
					uaMap.put(ua, uaMap.size());
					uaList.add(ua);
				}
				indexArray[i] = uaMap.get(ua);
			}
			
			UniqueAttribute uaArray[] = uaList.toArray(new UniqueAttribute[uaList.size()]);
			
			return new BasicMesh(uaArray, indexArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static BasicMesh loadDAT(String path) {
		File file = new File(path + ".dat");
		if(!file.exists()) {
			Sys.error("DAT " + path + " not found!");
			System.exit(1);
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Object o = ois.readObject();
			ois.close();
			if(!(o instanceof BasicMesh)) throw new Exception("Unable to load " + path + "!");
			BasicMesh mesh = (BasicMesh)o;
			return mesh;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void saveDAT(BasicMesh mesh, String path) {
		File file = new File(path + ".dat");
		if(file.exists()) 
			file.delete();
		try {
			file.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(mesh);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ColorAssignment{
	
	int beginIndex;
	int endIndex;
	Color color;
	
	ColorAssignment(Color c){
		this.beginIndex = -1;
		this.endIndex = -1;
		this.color = c;
	}
}
