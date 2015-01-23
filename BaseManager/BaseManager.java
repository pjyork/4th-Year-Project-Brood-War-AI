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
	private BuildOrder buildOrder;
	private int spentMinerals;
	private int spentGas;
	private Player me;
	
	public BaseManager(List<Base> bases,Game game, BuildOrder buildOrder){
		this.bases = bases;
		this.game = game;
		this.pylonBuilding=false;
		this.buildOrder = buildOrder;
		this.me = game.self();
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
		followBuildOrder();
		for (Base base : bases) {
			base.checkBuilder();
			
		}
	}
	
	private void followBuildOrder() {
		//dequeues as many items as can be built from the build order and builds them
		boolean resourcesRemain = false;
		while(!buildOrder.isEmpty() && resourcesRemain){
			BuildOrderItem toBuild = buildOrder.peek();
			if(toBuild.isUnitOrBuilding()){
				resourcesRemain = queueToBuild(toBuild.unitItem());
			}
			else{
				resourcesRemain = queueToUpgrade(toBuild.upgradeItem());
			}
		}
		
	}

	private boolean queueToUpgrade(UpgradeType upgrade) {
		int mineralsRemaining = mineralsRemaining();
		int gasRemaining = gasRemaining();
		if(mineralsRemaining > upgrade.mineralPrice() && gasRemaining > upgrade.gasPrice()){
			buildOrder.remove();
			researchUpgrade(upgrade);
			return true;
		}
		return false;
	}

	private void researchUpgrade(UpgradeType upgrade) {
		
	}

	private boolean queueToBuild(UnitType unit) {
		int mineralsRemaining = mineralsRemaining();
		int gasRemaining = gasRemaining();
		if(mineralsRemaining > unit.mineralPrice() && gasRemaining > unit.gasPrice()){
			buildOrder.remove();
			mainBase.queueToBuild(unit);
			return true;
		}
		return false;
	}

	private int mineralsRemaining() {
		return spentMinerals - me.minerals();
	}
	
	private int gasRemaining() {
		return spentGas - me.gas();
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
