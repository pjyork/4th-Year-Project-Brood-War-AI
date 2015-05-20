package IntelManager;

import java.util.LinkedList;
import java.util.List;

import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;

public class IntelManager {
	private LinkedList<Unit> enemyUnits;
	private LinkedList<Unit> enemyBuildings;
	private Position opponentStart;
	private LinkedList<Position> enemyBuildingPositions;
	
	public IntelManager(Position opponentStart){
		enemyUnits = new LinkedList<Unit>();
		enemyBuildings = new LinkedList<Unit>();
		enemyBuildingPositions = new LinkedList<Position>();
		this.opponentStart = opponentStart;
	}
	
	public void addEnemyUnit(Unit unit){
		if(unit.getType().isBuilding()){
			enemyBuildings.add(unit);
			enemyBuildingPositions.add(unit.getPosition());
		}
		else{
			enemyUnits.add(unit);
		}
	}

	public void removeUnit(Unit unit) {
		if(unit.getType().isBuilding()){
			enemyBuildings.remove(unit);
			//enemyBuildingPositions.remove(unit.getPosition());
		}
		else{
			enemyUnits.remove(unit);
		}
	}

	public PositionOrUnit getNearestUnit(Position armyPosition) {
		int shortestDist = Integer.MAX_VALUE;
		Unit result = null;
		for(Unit enemy : enemyUnits){
			int dist = enemy.getPosition().getApproxDistance(armyPosition);
			if(dist < shortestDist && enemy.isTargetable()){
				shortestDist = dist;
				result = enemy;
			}
		}
		if(result == null){
			return new PositionOrUnit(opponentStart);
		}
		UnitType t = result.getType();
		return new PositionOrUnit(result);
	}

	public Position getEnemyHQ() {
		return opponentStart;		
	}

	public PositionOrUnit getTarget() {
		PositionOrUnit result = null;
		if(!enemyBuildings.isEmpty()){
			
		}
		if(!enemyBuildingPositions.isEmpty() && result == null){
			result = new PositionOrUnit(enemyBuildingPositions.getFirst());
			Position enemy  = enemyBuildingPositions.removeFirst();	
			enemyBuildingPositions.addLast(enemy);
		}
		if(result == null){
			result = new PositionOrUnit(opponentStart);
		}
		return result;		
	}

	public void addLocations(List<BaseLocation> baseLocations) {
		for(BaseLocation baseLoc: baseLocations){
			System.out.println("added - " + baseLoc.getPosition().toString());
			enemyBuildingPositions.add(baseLoc.getPosition());
		}		
		for(Position pos : enemyBuildingPositions){
			System.out.println("positions - " + pos.toString());
			
		}
	}
}
