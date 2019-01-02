package net.pyraetos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3i;

import net.pyraetos.objects.NormalMesh;
import net.pyraetos.util.Sys;

public abstract class ObjLoader{

	public static NormalMesh load(String path) {
		File file = new File(path);
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
				if(line.startsWith("v  ")) {
					line = line.substring(3);
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
				if(line.startsWith("f  ")){
					line = line.substring(3);
					String rawInts[] = line.split(" ");
					Vector3i vFace = new Vector3i(Integer.parseInt(rawInts[0].split("//")[0]),
													Integer.parseInt(rawInts[1].split("//")[0]),
													Integer.parseInt(rawInts[2].split("//")[0]));
					Vector3i nFace = new Vector3i(Integer.parseInt(rawInts[0].split("//")[1]),
							Integer.parseInt(rawInts[1].split("//")[1]),
							Integer.parseInt(rawInts[2].split("//")[1]));
					indexList.add(vFace.x-1);
					indexList.add(vFace.y-1);
					indexList.add(vFace.z-1);
					normalIndexList.add(nFace.x-1);
					normalIndexList.add(nFace.y-1);
					normalIndexList.add(nFace.z-1);
				}
			}
			reader.close();
			Vector3f[] vertexArray = vertexList.toArray(new Vector3f[vertexList.size()]);
			int indexArray[] = new int[indexList.size()];
			for(int i = 0; i < indexList.size(); i++)
				indexArray[i] = indexList.get(i);
			Vector3f[] normalArray = new Vector3f[normalIndexList.size()];
			for(int i = 0; i < normalIndexList.size(); i++) {
				normalArray[i] = normalList.get(normalIndexList.get(i));
			}
			return new NormalMesh(vertexArray, indexArray, normalArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
