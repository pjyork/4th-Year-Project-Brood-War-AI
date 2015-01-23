package BaseManager;
	
import java.util.List;

import BuildOrderManager.BuildOrder;
import BuildOrderManager.BuildOrderItem;
import bwapi.*;
import bwta.*;

public class BaseManager {
	private List<Base> bases;
	private Base mainBase;//initial base where most of our production will be 
	private Game game;
	private boolean pylonBuilding;
	private int expectedSupply;
	private BuildOrder buildOrder;
	private int spentMinerals;
	private int spentGas;
	
	public BaseManager(List<Base> bases,Game game, BuildOrder buildOrder){
		this.bases = bases;
		this.game = game;
		this.pylonBuilding=false;
		this.expectedSupply=9;
		this.buildOrder = buildOrder;
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
		int minerals = me.minerals() - spentMinerals;
		int gas = me.gas() - spentGas;
		int supply = me.supplyTotal()-me.supplyUsed();
		followBuildOrder(minerals, gas ,supply);
		for (Base base : bases) {
			base.checkBuilder();
			
		}
	}
	
	private void followBuildOrder(int minerals, int gas, int supply) {
		
		int unusedMinerals = minerals;
		int unusedSupply = supply;
		int unusedGas = gas;
		
		while(unusedMinerals > 0 && unusedGas > 0 && unusedSupply > 0 && !buildOrder.isEmpty()){
			BuildOrderItem toBuild = buildOrder.remove();
			if(toBuild.isUnitOrBuilding()){
				UnitType unit = toBuild.unitItem();
				mainBase.queueToBuild(unit);
			}
			else{
				
			}
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
