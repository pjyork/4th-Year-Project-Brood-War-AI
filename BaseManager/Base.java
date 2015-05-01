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
	
	public Base (Unit hq, Game game){
		assert(hq.getType()==UnitType.Protoss_Nexus||hq.getType()==UnitType.Terran_Command_Center||hq.getType()==UnitType.Zerg_Hatchery);
		this.hq=hq;
		this.mineralMiningWorkers = new LinkedList<Unit>();
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
		System.out.println("send to build -" + buildingType);
		if(builderWorker == null){
			System.out.println("null worker");
			builderWorker = mineralMiningWorkers.remove(0);
			buildingPlacer.setBuilder(builderWorker);
		}
		if(builderWorker.isIdle()){
			System.out.println("idle worker");
			if (buildingType == UnitType.Protoss_Pylon){
				TilePosition loc = buildingPlacer.placePylon();
				builderWorker.build(loc, UnitType.Protoss_Pylon);
			}
			else if(!pylons.isEmpty()) {
				TilePosition loc = buildingPlacer.placeOther(buildingType);
				System.out.println("loc found");
				
				builderWorker.build(loc, buildingType);
			}
		}
	}

	
	
	



	public int getDistance(Unit unit) {
		//get the distance from the unit to this base
		return hq.getDistance(unit);
	}

	public void buildingCreate(Unit building) {
		//called when a building has begun to be built
		if(building.getType() == UnitType.Protoss_Pylon){
			pylons.add(building);
		}
		if(!buildQueue.isEmpty()){
			if(buildQueue.get(0) == building.getType()){
				//if the building is from the start of our build queue
				buildQueue.remove(0);
			}
			
			if(!(builderWorker==null)){				
				if(buildQueue.isEmpty()){
					sendToMine(builderWorker);
					builderWorker=null;
				}
				else{
					System.out.println(buildQueue.toString());
					build(buildQueue.get(0));
				}
			}
		}
	}
	
	public void queueToBuild(UnitType buildingType){
		if(builderWorker == null && !mineralMiningWorkers.isEmpty()){
			builderWorker = mineralMiningWorkers.remove(0);
			buildingPlacer.setBuilder(builderWorker);
			builderWorker.stop();
		}
		buildQueue.add(buildingType);
		checkBuilder();
	}

	public void checkBuilder() {
		if(!buildQueue.isEmpty()){
			if(builderWorker == null){
				System.out.println("null worker check");
				builderWorker = mineralMiningWorkers.remove(0);
				buildingPlacer.setBuilder(builderWorker);
				builderWorker.stop();
			}
			if(builderWorker.isIdle()){
				System.out.println("idle worker check");
				build(buildQueue.get(0));
			}
		}
		else{
			System.out.println("empty queue worker");
			sendToMine(builderWorker);
			builderWorker = null;
		}
	}
}
