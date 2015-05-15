package IntelManager;

import java.util.LinkedList;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

public class IntelManager {
	private LinkedList<Unit> enemyUnits;
	
	public IntelManager(){
		enemyUnits = new LinkedList<Unit>();
	}
	
	public void addEnemyUnit(Unit unit){
		enemyUnits.add(unit);
	}

	public void removeUnit(Unit unit) {
		enemyUnits.remove(unit);
	}

	public Unit getNearestUnit(Position armyPosition) {
		int shortestDist = Integer.MAX_VALUE;
		Unit result = null;
		for(Unit enemy : enemyUnits){
			int dist = enemy.getPosition().getApproxDistance(armyPosition);
			if(dist < shortestDist){
				shortestDist = dist;
				result = enemy;
			}
		}
		UnitType t = result.getType();
		if(t.isWorker() || t.isBuilding() || t == UnitType.Zerg_Overlord){
			return null;
		}
		return result;
	}
}
