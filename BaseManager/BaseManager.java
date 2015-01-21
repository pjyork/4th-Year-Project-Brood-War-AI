package BaseManager;
	
import java.util.List;

import bwapi.*;
import bwta.*;

public class BaseManager {
	private List<Base> bases;
	private Game game;
	private boolean pylonBuilding;
	private int expectedSupply;
	
	public BaseManager(List<Base> bases,Game game){
		this.bases = bases;
		this.game = game;
		this.pylonBuilding=false;
		this.expectedSupply=9;
	}
	
	public void addWorker(Unit newWorker){
		assert(newWorker.getType().isWorker());
		if(newWorker.getType()==UnitType.Protoss_Probe){
	        Base closestBase = bases.get(0);
			for (Base base : bases) {
	             if ( base.getDistance(newWorker) < closestBase.getDistance(newWorker)) {
	            	 closestBase = base;
	             }
	         }
			closestBase.addWorker(newWorker);
		}
	}
	
	public void manageBases(){
		//called every frame by main module. Performs general base management, expansion decisions etc.
		Player me = game.self();
		for (Base base : bases) {
			int supplyTotal = me.supplyTotal();
			int supplyUsed = me.supplyUsed();
			int minerals = me.minerals();
			if(minerals>50){
				base.buildWorker();
			}
			if(expectedSupply-supplyUsed<=4&&minerals>=100&&!pylonBuilding){
				base.queueToBuild(UnitType.Protoss_Pylon);
				expectedSupply+=16;
			}
			if(supplyUsed>26){
				base.queueToBuild(UnitType.Protoss_Gateway);
			}
			base.checkBuilder();
			
		}
	}
	
	public void expand(){
		//select a place to expand to and send the worker manager to expand there
	}

	public void pylonBuilt() {
		pylonBuilding=false;
		
	}

	public void buildingCreate(Unit building) {
		for (Base base : bases){
			base.buildingCreate(building);
		}
	}
}
