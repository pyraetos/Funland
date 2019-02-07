package net.pyraetos.objects;

import static net.pyraetos.Options.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import net.pyraetos.util.Sys;

public class Terrain{
	
	private Map<Integer, Map<Integer, Model>> regions;
	private Set<Model> activeRegionsWaiting;
	private Set<Model> activeRegions;
	private Scene scene;
	private Queue<Tuple> newRegions;
	private long lastGenTS;
	private boolean needRegionUpdate;
	
	private int rX;
	private int rZ;
	
	public Terrain(Scene scene, int rX, int rZ) {
		this.rX = rX;
		this.rZ = rZ;
		regions = new HashMap<Integer, Map<Integer, Model>>();
		activeRegions = new HashSet<Model>();
		this.scene = scene;
		activeRegionsWaiting = new HashSet<Model>();
		newRegions = new PriorityQueue<Tuple>();
		lastGenTS = 0;
		needRegionUpdate = true;
		updateRegions();
	}
	
	public Terrain(Scene scene) {
		this(scene, 0, 0);
	}
	
	public Set<Model> getActiveRegions(){
		return activeRegions;
	}
	
	public void tryGenRegion() {
		if(!newRegions.isEmpty()) {
			long currTS = Sys.time();
			if(currTS - lastGenTS > 100) {
				Tuple t = newRegions.poll();
				Model r = new RegionMesh(RegionMesh.SIDE * t.i - t.i, RegionMesh.SIDE * t.j - t.j, true).spawnModel();
				regions.get(t.i).put(t.j, r);
				lastGenTS = currTS;
				needRegionUpdate = true;
			}
		}
	}

	public void reorderNewRegions(int rX, int rZ) {
		this.rX = rX;
		this.rZ = rZ;
		if(newRegions.isEmpty()) return;
		Queue<Tuple> newQueue = new PriorityQueue<Tuple>();
		for(Tuple t : newRegions) newQueue.offer(t);
		newRegions = newQueue;
		needRegionUpdate = true;
	}
	
	//Not super ideal to generate as a square.. should be circular around camera
	//Also updates active regions that ought to be rendered
	public void updateRegions() {
		if(!needRegionUpdate) return;
		activeRegionsWaiting = new HashSet<Model>();
		for(int i = rX - TERRAIN_DISTANCE; i < rX + TERRAIN_DISTANCE + 1; i++) {
			for(int j = rZ - TERRAIN_DISTANCE; j < rZ + TERRAIN_DISTANCE + 1; j++) {
				if(!regions.containsKey(i))
					regions.put(i, new HashMap<Integer, Model>());
				Map<Integer, Model> internalMap = regions.get(i);
				if(!internalMap.containsKey(j)) {
					Tuple t = new Tuple(i, j);
					if(!newRegions.contains(t)) newRegions.offer(t);
				}else{
					activeRegionsWaiting.add(internalMap.get(j));
				}
			}
		}
		scene.removeModels(activeRegions);
		activeRegions.clear();
		activeRegions = activeRegionsWaiting;
		scene.addModels(activeRegions);
		needRegionUpdate = false;
	}

	private class Tuple implements Comparable<Tuple>{
		
		int i;
		int j;
		
		Tuple(int i, int j){
			this.i = i;
			this.j = j;
		}
		
		@Override
		public boolean equals(Object other) {
			if(other instanceof Tuple) {
				Tuple t = (Tuple)other;
				return t.i == i && t.j == j;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return (i << 16) ^ j;
		}
		
		@Override
		public int compareTo(Tuple o){
			double thisDist = Math.pow(rX - i, 2) + Math.pow(rZ - j, 2);
			double otherDist = Math.pow(rX - o.i, 2) + Math.pow(rZ - o.j, 2);
			if(thisDist < otherDist) return -1;
			if(thisDist > otherDist) return 1;
			return 0;
		}
		
	}
}
