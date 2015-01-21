package BaseManager;

import java.util.LinkedList;
import java.util.List;

import bwapi.*;
import bwta.*;

public class Base {//represents an expansion
	private Unit hq;//The Nexus/Command Centre/Hatchery
	private Game game;
	private List<Unit> miningWorkers;// all workers mining in this base
	private Unit buildingWorker=null;// if there is a worker building in this base, this is that worker
	private List<UnitType> buildQueue;
	
	public Base (Unit hq, Game game){
		assert(hq.getType()==UnitType.Protoss_Nexus||hq.getType()==UnitType.Terran_Command_Center||hq.getType()==UnitType.Zerg_Hatchery);
		this.hq=hq;
		this.miningWorkers = new LinkedList<Unit>();
		this.buildQueue = new LinkedList<UnitType>();
		this.game=game;
		
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
		if(buildingWorker==null){
			buildingWorker=miningWorkers.remove(0);
		}
		
		if (buildingType==UnitType.Protoss_Pylon){
			boolean locFound = false;
			int i = 1, dir = 0;
			TilePosition loc = new TilePosition(-1,-1);
		
			while(!locFound&&i<10){
				switch (dir){
					case 0: loc = new TilePosition(hq.getTilePosition().getX(),hq.getTilePosition().getY()-7*i);
							break;
					case 1: loc = new TilePosition(hq.getTilePosition().getX()+4+7*i,hq.getTilePosition().getY());
							break;
					case 2: loc = new TilePosition(hq.getTilePosition().getX(),hq.getTilePosition().getY()+3+7*i);
							break;
					case 3: loc = new TilePosition(hq.getTilePosition().getX()-7*i,hq.getTilePosition().getY());
							break;
				}
				if(game.canBuildHere(buildingWorker, loc, buildingType)){
					locFound = true;
				}
				else if(dir<3){dir++;}
				else{i++;}
			}
			buildingWorker.build(loc, UnitType.Protoss_Pylon);
		}
		else{
			TilePosition ret = null;
			int maxDist = 3;
			int stopDist = 40;
			TilePosition aroundTile = hq.getTilePosition();
			// Refinery, Assimilator, Extractor
			if (buildingType.isRefinery()) {
				for (Unit n : game.neutral().getUnits()) {
					if ((n.getType() == UnitType.Resource_Vespene_Geyser) && 
							( Math.abs(n.getTilePosition().getX() - aroundTile.getX()) < stopDist ) &&
							( Math.abs(n.getTilePosition().getY() - aroundTile.getY()) < stopDist )
							) ret = n.getTilePosition();
				}
			}
			
			while ((maxDist < stopDist) && (ret == null)) {
				for (int i=aroundTile.getX()-maxDist; i<=aroundTile.getX()+maxDist; i++) {
					for (int j=aroundTile.getY()-maxDist; j<=aroundTile.getY()+maxDist; j++) {
						if (game.canBuildHere(buildingWorker, new TilePosition(i,j), buildingType, false)) {
							// units that are blocking the tile
							boolean unitsInWay = false;
							for (Unit u : game.getAllUnits()) {
								if (u.getID() == buildingWorker.getID()) continue;
								if ((Math.abs(u.getTilePosition().getX()-i) < 4) && (Math.abs(u.getTilePosition().getY()-j) < 4)) unitsInWay = true;
							}
							if (!unitsInWay) {
								ret = new TilePosition(i, j);
							}
							
						}
					}
				}
				maxDist += 2;
			}

			buildingWorker.build(ret, buildingType);
		}
	}

	
	
	public int getDistance(Unit unit) {
		return hq.getDistance(unit);
	}

	public void buildingCreate(Unit building) {
		if(!buildQueue.isEmpty()){
			buildQueue.remove(0);
			
			if(!(buildingWorker==null)){				
				if(buildQueue.isEmpty()){
					sendToMine(buildingWorker);
					buildingWorker=null;
					
				}
				else{
					System.out.println(buildQueue.toString());
					build(buildQueue.get(0));
				}
			}
		}
	}
	
	public void queueToBuild(UnitType buildingType){	
		if(buildingWorker == null && !miningWorkers.isEmpty()){
			buildingWorker = miningWorkers.remove(0);buildingWorker.stop();
		}
		buildQueue.add(buildingType);
		
		checkBuilder();
	}

	public void checkBuilder() {
		if(!buildQueue.isEmpty()){
			if(buildingWorker == null){
				buildingWorker=miningWorkers.remove(0); 
				buildingWorker.stop();
			}
			if(buildingWorker.isIdle()){
				build(buildQueue.get(0));
			}
		}
		else{
			sendToMine(buildingWorker);
			buildingWorker = null;
		}
	}
}
