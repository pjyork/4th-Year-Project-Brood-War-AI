package Listeners;

import java.util.LinkedList;
import java.util.List;

import bwapi.UnitType;

public class BuildingCreateNotifier {
	private List<BuildingCreateListener> listeners;
	
	public BuildingCreateNotifier(){
		this.listeners = new LinkedList<BuildingCreateListener>();
	}
	
	public void addListener(BuildingCreateListener listener){
		listeners.add(listener);
	}
	
	public void notify(UnitType building){
		for(BuildingCreateListener listener : listeners){
			listener.buildingCreated(building);
		}
	}
}
