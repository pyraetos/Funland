package net.pyraetos;

import org.joml.Vector3f;

import net.pyraetos.util.Config;
import net.pyraetos.util.Sys;

public abstract class Options{

	private static Config conf;
	
	public static Vector3f LIGHT_DIR;
	public static int SHADOW_MAP_DIM;
	public static boolean LINEAR_INTERP;
	public static boolean V_SYNC;
	public static boolean CULL_BACK;
	public static boolean SRGB;
	public static float ENTROPY;
	public static long SEED;
	public static Color TERRAIN_COLOR;
	public static double TREE_CHANCE;
	public static int TERRAIN_DISTANCE;
	public static float SHADOW_ORTHO_SIDE;
	public static Color DEFAULT_MESH_COLOR;
	
	static {
		conf = new Config("config.txt");
		LIGHT_DIR = parseVector(conf.getString("lightDirection", "(0.3015, 0.9045, 0.3015)"));
		SHADOW_MAP_DIM = conf.getInt("shadowMapSideLength", 4096);
		LINEAR_INTERP = conf.getBoolean("shadowMapLinearInterpolation", false);
		SHADOW_ORTHO_SIDE = conf.getFloat("shadowMapOrthoSideLength", 60f);
		V_SYNC = conf.getBoolean("verticalSync", false);
		CULL_BACK = conf.getBoolean("backFaceCulling", false);
		SRGB = conf.getBoolean("colorSpaceSRGB", false);
		DEFAULT_MESH_COLOR = parseColor(conf.getString("defaultMeshColor", "(1.0, 0.5, 1.0)"));
		ENTROPY = conf.getFloat("terrainEntropy", 4f);
		conf.comment("terrainSeed", "Put 'random' for random seed");
		String seed = conf.getString("terrainSeed", "random");
		if(seed.equals("random")) SEED = Sys.randomSeed();
		else {
			SEED = seed.hashCode();
			SEED = SEED << 32 | String.valueOf(SEED).hashCode();
		}
		TERRAIN_COLOR = parseColor(conf.getString("terrainColor", "(0.5, 0.9, 0.4)"));
		TREE_CHANCE = conf.getDouble("terrainTreeChance", 0.01d);
		TERRAIN_DISTANCE = conf.getInt("terrainViewDistance", 20);
		conf.save();
	}
	
	private static Vector3f parseVector(String string) {
		try {
			string = string.replace("(", "").replace(")", "").replace(",", "");
			String[] split = string.split(" ");
			float x = Float.parseFloat(split[0]);
			float y = Float.parseFloat(split[1]);
			float z = Float.parseFloat(split[2]);
			return new Vector3f(x, y, z);
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(1);//Need to do more gracefully
			return null;
		}
	}
	
	private static Color parseColor(String string) {
		Vector3f v = parseVector(string);
		return new Color(v.x, v.y, v.z);
	}
}
