package IntelManager;

import java.util.LinkedList;

import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.Unit;
import bwapi.UnitType;

public class IntelManager {
	private LinkedList<Unit> enemyUnits;
	private Position opponentStart;
	
	public IntelManager(Position opponentStart){
		enemyUnits = new LinkedList<Unit>();
		this.opponentStart = opponentStart;
	}
	
	public void addEnemyUnit(Unit unit){
		enemyUnits.add(unit);
	}

	public void removeUnit(Unit unit) {
		enemyUnits.remove(unit);
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
		while(!enemyUnits.isEmpty() && result == null){
			Unit unit = enemyUnits.getFirst();
			if(unit.getHitPoints() >= 0){
				result = new PositionOrUnit(enemyUnits.getFirst().getPosition());
				Unit enemy  = enemyUnits.removeFirst();	
				enemyUnits.addLast(enemy);
			}
			else{
				enemyUnits.remove(unit);
			}
		}
		if(result == null){
			result = new PositionOrUnit(opponentStart);
		}
		return result;		
	}
}
