package BaseManager;

import java.util.LinkedList;
import java.util.List;

import bwapi.*;
import bwta.*;

public class Base {//represents an expansion
	private Unit hq;//The Nexus/Command Centre/Hatchery
	private Game game;
	private List<Unit> mineralMiningWorkers;// all workers mining minerals in this base
	private List<Unit> gasMiningWorkers;// all workers mining gas in this base	
	private Unit builderWorker=null;// if there is a worker building in this base, this is that worker
	private List<UnitType> buildQueue;//queue of buildings to be built in this base
	private BuildingPlacer buildingPlacer;//an object for finding locations to build buildings
	private List<Unit> pylons;
	private Unit assim;
	
	
	public Base (Unit hq, Game game){
		assert(hq.getType()==UnitType.Protoss_Nexus||hq.getType()==UnitType.Terran_Command_Center||hq.getType()==UnitType.Zerg_Hatchery);
		this.hq=hq;
		this.mineralMiningWorkers = new LinkedList<Unit>();
		this.gasMiningWorkers = new LinkedList<Unit>();
		this.pylons = new LinkedList<Unit>();
		this.buildQueue = new LinkedList<UnitType>();
		this.game=game;
		this.buildingPlacer = new BuildingPlacer(hq, game);
		
	}
	
	public Position getPosition(){
		return hq.getPosition();
		
	}
	
	public void addWorker(Unit newWorker){
		//assert(newWorker.getType().isWorker());
        sendToMine(newWorker);
	}
	
	private void sendToMine(Unit worker){
		//send worker to mine minerals at this base
		Unit closestMineral = null;
		
		for (Unit neutralUnit : game.neutral().getUnits()) {
             if (neutralUnit.getType().isMineralField()) {
                 if (closestMineral == null || hq.getDistance(neutralUnit) < hq.getDistance(closestMineral)) {
                     closestMineral = neutralUnit;
                 }
             }
         }
		if(closestMineral!=null){
			worker.gather(closestMineral);
		}
		mineralMiningWorkers.add(worker);
	}
	
	private void build(UnitType buildingType){
		//send our builder worker to build a building of type buildingType
		if(builderWorker == null){
			System.out.println("null worker");
			builderWorker = mineralMiningWorkers.remove(0);
			buildingPlacer.setBuilder(builderWorker);
			builderWorker.stop();
			System.out.println("b");
		}
		if(builderWorker.isIdle()
				|| builderWorker.isGatheringGas()
				|| builderWorker.isGatheringMinerals()){
			System.out.println("idle worker send to build -" + buildingType);
			if (buildingType == UnitType.Protoss_Pylon){
				TilePosition loc = buildingPlacer.placePylon();
				if(game.canBuildHere(loc, buildingType)){
					builderWorker.build(buildingType, loc);
				}
			}
			else if(!pylons.isEmpty() || buildingType == UnitType.Protoss_Assimilator) {
				TilePosition loc = buildingPlacer.placeOther(buildingType);
				System.out.println("loc found!!!!");
				if(game.canBuildHere(loc, buildingType)){
					System.out.println("can build");
					builderWorker.build(buildingType, loc);
					if(buildingType == buildQueue.get(0)){
						buildQueue.remove(0);
					}
					System.out.println("sent to build");		
				}
			}
		}
	}

	public int getDistance(Unit unit) {
		//get the distance from the unit to this base
		return (int) hq.getDistance(unit);
	}

	public void buildingCreate(Unit building) {
		//called when a building has begun to be built
		System.out.println("buildcreate - base");
		if(!buildQueue.isEmpty()){
			if(buildQueue.get(0) == building.getType()){
				//if the building is from the start of our build queue
				buildQueue.remove(0);
				System.out.println("build dequeued");
			}
		}
	}
	
	public void queueToBuild(UnitType buildingType){
		buildQueue.add(buildingType);
		checkBuilder();
	}

	public void checkBuilder() {
		if(!buildQueue.isEmpty() ){
			UnitType buildingType = buildQueue.get(0);
			if(buildingType == UnitType.Protoss_Pylon
					|| buildingType == UnitType.Protoss_Assimilator
					|| !pylons.isEmpty()){				
				if(builderWorker == null&& !mineralMiningWorkers.isEmpty()){
					builderWorker = mineralMiningWorkers.remove(0);
					buildingPlacer.setBuilder(builderWorker);
					builderWorker.stop();
				}
				if(builderWorker.isIdle()
						|| builderWorker.isGatheringGas()
						|| builderWorker.isGatheringMinerals()){
					build(buildQueue.get(0));
				}
			}
		}
		if(builderWorker!=null && builderWorker.isIdle()){
			System.out.println("sent to mine");
			sendToMine(builderWorker);
		}
	}

	public void checkMiners() {
		for(Unit worker : mineralMiningWorkers){
			if(worker.isIdle()){
				sendToMine(worker);
			}
		}
		for(Unit worker : gasMiningWorkers){
			sendToMineGas(worker);
		}
	}

	private void sendToMineGas(Unit worker) {
		System.out.println("GAS");
		gasMiningWorkers.add(worker);
		if(!(assim == null) && !assim.isBeingConstructed()){
			System.out.println("check gas miners");
			//send worker to mine minerals at this base
			Unit closestGeyser = null;
			
			for (Unit neutralUnit : game.neutral().getUnits()) {
	             if (neutralUnit.getType() == UnitType.Resource_Vespene_Geyser) {
	                 if (closestGeyser == null || hq.getDistance(neutralUnit) < hq.getDistance(closestGeyser)) {
	                     closestGeyser = neutralUnit;
	                 }
	             }
	        }
			if(closestGeyser!=null){
				System.out.println(worker.toString() + closestGeyser);
				worker.gather(closestGeyser);
			}
		} 
	}

	public void buildingComplete(Unit building) {
		if(building.getType() == UnitType.Protoss_Pylon){
			pylons.add(building);
		}
		System.out.println(building.getType());
		if(building.getType().isRefinery()){
			this.assim = building;
			for(int i = 0; i < 3; i++){
				sendToMineGas(mineralMiningWorkers.remove(0));
			}
						
		}
	}
}
