package net.pyraetos.objects;

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
		File file = new File(path + ".obj");
		List<Vector3f> vertexList = new ArrayList<Vector3f>();
		List<Integer> indexList = new ArrayList<Integer>();
		List<Vector3f> normalList = new ArrayList<Vector3f>();
		List<Integer> normalIndexList = new ArrayList<Integer>();
		if(!file.exists()) {
			Sys.error("OBJ " + path + " not found!");
			System.exit(1);
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
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
				UniqueAttribute ua = new UniqueAttribute(vertex, normal, new Color(1f, 0f, 0f));
				if(!uaMap.containsKey(ua)) {
					uaMap.put(ua, uaMap.size());
					uaList.add(ua);
				}
				indexArray[i] = uaMap.get(ua);
			}
			
			UniqueAttribute uaArray[] = uaList.toArray(new UniqueAttribute[uaList.size()]);
			
			//Vector3f[] vertexArray = vertexList.toArray(new Vector3f[vertexList.size()]);
			
			/*int indexArray[] = new int[indexList.size()];
			for(int i = 0; i < indexList.size(); i++)
				indexArray[i] = indexList.get(i);*/
			
			/*Vector3f[] normalArray = new Vector3f[normalList.size()];
			normalList.toArray(normalArray);
			Vector3f[] normalArray = new Vector3f[vertexList.size()];
			for(int i = 0; i < normalIndexList.size(); i++) {
				int vIndex = indexList.get(i);
				int nIndex = normalIndexList.get(i);
				if(normalArray[vIndex] == null)
					normalArray[vIndex] = new Vector3f(normalList.get(nIndex));
				else
					normalArray[vIndex] = normalArray[vIndex].add(normalList.get(nIndex)).normalize();
			}
			for(int i = 0; i < normalArray.length; i++) {
				if(normalArray[i] == null) normalArray[i] = new Vector3f(0f,0f,0f);
			}*/
			
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
