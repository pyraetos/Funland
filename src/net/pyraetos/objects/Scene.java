package net.pyraetos.objects;

import java.util.HashSet;
import java.util.Set;

public class Scene{

	private Set<Model> models;
	
	public Scene() {
		models = new HashSet<Model>();
	}
	
	public void addModels(Model...models) {
		for(Model m : models) this.models.add(m);
	}
	
	public void addModels(Set<Model> models) {
		this.models.addAll(models);
	}
	
	public void removeModels(Model...models) {
		for(Model m : models) this.models.remove(m);
	}
	
	public void removeModels(Set<Model> models) {
		this.models.removeAll(models);
	}
	
	public Set<Model> models(){
		return models;
	}
	
}
