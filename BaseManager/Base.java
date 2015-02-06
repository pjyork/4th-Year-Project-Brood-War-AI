package BaseManager;

import java.util.LinkedList;
import java.util.List;

import BuildOrderManager.BuildingPlacer;
import bwapi.*;
import bwta.*;

public class Base {//represents an expansion
	private Unit hq;//The Nexus/Command Centre/Hatchery
	private Game game;
	private List<Unit> miningWorkers;// all workers mining in this base
	private Unit builderWorker=null;// if there is a worker building in this base, this is that worker
	private List<UnitType> buildQueue;
	private BuildingPlacer buildingPlacer;
	
	public Base (Unit hq, Game game){
		assert(hq.getType()==UnitType.Protoss_Nexus||hq.getType()==UnitType.Terran_Command_Center||hq.getType()==UnitType.Zerg_Hatchery);
		this.hq=hq;
		this.miningWorkers = new LinkedList<Unit>();
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
	
	public void sendToMine(Unit worker){
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
		miningWorkers.add(worker);
	}
	
	public void buildWorker(){
		if(!hq.isTraining()){
			hq.train(UnitType.Protoss_Probe);
		}
	}
	
	private void build(UnitType buildingType){
		if(builderWorker == null){
			builderWorker = miningWorkers.remove(0);
			buildingPlacer.setBuilder(builderWorker);
		}
		
		if (buildingType == UnitType.Protoss_Pylon){
			TilePosition loc = buildingPlacer.placePylon();
			
			builderWorker.build(loc, UnitType.Protoss_Pylon);
		}
		else{
			TilePosition loc = buildingPlacer.placeOther(buildingType);
			
			builderWorker.build(loc, buildingType);
		}
	}

	
	
	



	public int getDistance(Unit unit) {
		return hq.getDistance(unit);
	}

	public void buildingCreate(Unit building) {
		if(!buildQueue.isEmpty()){
			if(buildQueue.get(0) == building.getType()){
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
		System.out.println("abandon all hope");		
		if(builderWorker == null && !miningWorkers.isEmpty()){
			builderWorker = miningWorkers.remove(0);
			buildingPlacer.setBuilder(builderWorker);
			builderWorker.stop();
		}
		buildQueue.add(buildingType);
		checkBuilder();
	}

	public void checkBuilder() {
		if(!buildQueue.isEmpty()){
			if(builderWorker == null){
				builderWorker = miningWorkers.remove(0);
				buildingPlacer.setBuilder(builderWorker);
				builderWorker.stop();
			}
			if(builderWorker.isIdle()){
				build(buildQueue.get(0));
			}
		}
		else{
			sendToMine(builderWorker);
			builderWorker = null;
		}
	}
}
