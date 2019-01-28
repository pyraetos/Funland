package net.pyraetos.objects;

import static net.pyraetos.Vectors.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.joml.Vector3f;
import net.pyraetos.Color;
import net.pyraetos.pgenerate.PGenerate;
import net.pyraetos.util.Sys;

@SuppressWarnings("serial")
public class RegionMesh extends BasicMesh{
	
	protected boolean useTrees;
	protected long seed;
	protected float x;
	protected float z;
	protected transient Set<Model> trees;
	protected transient Model model;
	
	private static final PGenerate PG;
	
	public static final float ENTROPY = 4f;
	public static final long SEED = Sys.randomSeed();
	public static final Color COLOR = new Color(0.6f, 1f, 0.5f);
	public static final double TREE_CHANCE = 0.01d;
	public static final BasicMesh TREE_MESH = MeshIO.loadOBJ("tree");;
	
	public static final int SIDE = 9;
	public static final int NUM_VERTICES = SIDE * SIDE;
	public static final int NUM_INDICES = 6 * (SIDE - 1) * (SIDE - 1);
	public static final int[] INDICES;
	
	static {
		PG = new PGenerate(1024, 1024, SEED);//Don't like hard size, .generate should return if already generated
		PG.setEntropy(ENTROPY);
		//treeMesh = MeshIO.loadOBJ("tree");
		//treeMesh.setColors(new int[]{514}, new net.pyraetos.Color[] {new net.pyraetos.Color(0.3f, 0.7f, 0.2f), new net.pyraetos.Color(0.8f, 0.4f, 0.2f)});
		//MeshIO.saveDAT(treeMesh,"tree");
		INDICES = new int[NUM_INDICES];
		int arr[] = {1,0,SIDE,1,SIDE,SIDE+1};
		int count = 0;
		for(int i = 0; i < SIDE * (SIDE - 1) - 1; i++) {
			for(int j = 0; j < 6; j++) {
				if((i+1) % SIDE != 0) {
					INDICES[count++] = arr[j];
				}
				arr[j]++;
			}
		}
	}
	
	public RegionMesh(float x, float z, boolean useTrees) {
		super(initUAs(x, z), INDICES);
		this.x = x;
		this.z = z;
		this.useTrees = useTrees;
		this.seed = SEED ^ Float.floatToIntBits(x) ^ Float.floatToIntBits(z);
		init();
	}
	
	private void init() {
		if(useTrees) {
			trees = new HashSet<Model>();
			Random rand = new Random(seed);
			for(UniqueAttribute ua : uas) {
				if(Sys.chance(TREE_CHANCE, rand)) {
					Vector3f vertex = ua.vertex;
					Model tree = TREE_MESH.spawnModel();
					tree.translate(vertex.x, vertex.y + 0.5f, vertex.z);
					trees.add(tree);
				}
			}
		}
		centerMass = new Vector3f(x, 0f, z);
		model = new Model(this);
	}
	
	private static UniqueAttribute[] initUAs(float x, float z) {
		Vector3f[][] verticesEx = new Vector3f[SIDE + 2][SIDE + 2];
		
		float off = -((float)SIDE / 2f) - 0.5f;
		for(int xi = -1; xi < SIDE + 1; xi++) {
			float curX = x + ((float)xi + off);
			int icurX = (int)Math.round(curX) + 512;
			for(int zi = -1; zi < SIDE + 1; zi++) {
				float curZ = z + ((float)zi + off);
				int icurZ = (int)Math.round(curZ) + 512;
				PG.generate(icurX, icurZ);
				float curY = PG.getValue(icurX, icurZ);
				verticesEx[xi + 1][zi + 1] = new Vector3f(curX, curY, curZ);
			}
		}
		
		UniqueAttribute[] uas = new UniqueAttribute[SIDE * SIDE];
		
		for(int i = 1; i <= SIDE; i++) {
			for(int j = 1; j <= SIDE; j++) {
				Vector3f v = verticesEx[i][j];
				Vector3f n0 = cross(to(v, verticesEx[i][j + 1]), to(v, verticesEx[i + 1][j]));
				Vector3f n1 = cross(to(v, verticesEx[i][j - 1]), to(v, verticesEx[i - 1][j]));
				Vector3f vn = average(n0, n1);
				uas[SIDE * (i - 1) + j - 1] = new UniqueAttribute(v, vn, COLOR);
			}
		}
		
		return uas;
	}

	@Override
	public Model spawnModel() {
		return model;
	}
	
	@Override
	public void render() {
		super.render();
		if(useTrees) {
			for(Model tree : trees) tree.render();
		}
	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException{
		stream.defaultReadObject();
		init();
	}
}
