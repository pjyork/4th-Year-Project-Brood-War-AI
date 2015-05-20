package BaseManager;
	
import java.util.List;

import BuildOrderManager.BuildOrder;
import BuildOrderManager.BuildOrderItem;
import bwapi.*;
import bwta.*;

public class BaseManager {
	private List<Base> bases;
	private Base mainBase;//initial base where most of our production will be
	private ProductionManager productionManager;
	private Game game;
	private BuildOrder buildOrder;
	private int spentMinerals = 0;
	private int spentGas = 0;
	private Player me;
	
	public BaseManager(List<Base> bases,Game game, BuildOrder buildOrder, Unit hq){
		this.bases = bases;
		this.game = game;
		this.mainBase = bases.get(0);
		this.buildOrder = buildOrder;
		this.productionManager = new ProductionManager(hq);
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
			base.checkMiners();
		}
	}
	
	private void followBuildOrder() {
		//dequeues as many items as can be built from the build order and builds them
		boolean resourcesRemain = true;
		while(!buildOrder.isEmpty() && resourcesRemain){
			BuildOrderItem toBuild = buildOrder.peek();
			String toBuildString = "";
			if(toBuild.isUnitOrBuilding()){
				resourcesRemain = queueToBuild(toBuild.unitItem());
				toBuildString = toBuild.unitItem().toString();
			}
			else{
				resourcesRemain = queueToUpgrade(toBuild.upgradeItem());
				toBuildString = toBuild.upgradeItem().toString();
			}
			
			if(resourcesRemain){
				buildOrder.remove();
			}
		}
		
	}

	private boolean queueToUpgrade(UpgradeType upgrade) {
		int mineralsRemaining = mineralsRemaining();
		int gasRemaining = gasRemaining();
		if(mineralsRemaining > upgrade.mineralPrice() && gasRemaining > upgrade.gasPrice()){
			return researchUpgrade(upgrade);
		}
		return false;
	}

	private boolean researchUpgrade(UpgradeType upgrade) {
		return productionManager.researchUpgrade(upgrade);
	}

	private boolean queueToBuild(UnitType unit) {
		int mineralsRemaining = mineralsRemaining();
		int gasRemaining = gasRemaining();
		
		if(mineralsRemaining >= unit.mineralPrice() && gasRemaining >= unit.gasPrice()){
			if(unit.isBuilding()){
				mainBase.queueToBuild(unit);
				spentGas += unit.gasPrice();
				spentMinerals += unit.mineralPrice();
				return true;
			}
			else{
				if(unit.supplyRequired() > me.supplyTotal() - me.supplyUsed()){
					return false;
				}
				return productionManager.buildUnit(unit);
			}
		}
		return false;
	}

	private int mineralsRemaining() {
		return me.minerals() - spentMinerals;
	}
	
	private int gasRemaining() {
		return me.gas() - spentGas;
	}

	public void expand(){
		//select a place to expand to and send the worker manager to expand there
	}

	public void buildingCreate(Unit building) {
		if(game.getFrameCount()!=0){
			for (Base base : bases){
				base.buildingCreate(building);
			}
			spentMinerals -= building.getType().mineralPrice();
			spentGas -= building.getType().gasPrice();	
		}
	}

	public void buildingComplete(Unit building) {
		if(game.getFrameCount()!=0){
			for (Base base : bases){
				base.buildingComplete(building);
			}
			productionManager.buildingCompleted(building);
		}		
	}

	public int getTotalSupply() {
		int sum = 0;
		for(Base base : bases){
			sum += base.supplySupplied();
		}
		return sum;
	}

	public void workerDestroyed(Unit unit) {
		for(Base base : bases){
			base.workerDestroyed(unit);
		}
		
	}

	public void buildingDestroyed(Unit unit) {
		for(Base base : bases){
			base.buildingDestroyed(unit);
		}		
	}
}
