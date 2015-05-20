package ArmyManager;

import java.util.LinkedList;
import java.util.List;

import IntelManager.IntelManager;
import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.Unit;

public class ArmyManager {
	private List<Army> armies;
	private boolean isMoving;
	private IntelManager intelManager;
	private int launchSize;
	private Army newArmy;
	

	public ArmyManager(IntelManager intelManager, int launchSize){
		this.armies = new LinkedList<Army>();
		this.newArmy = new Army();
		this.armies.add(newArmy);
		this.intelManager = intelManager;
		this.launchSize = launchSize;
	}
	
	public void checkArmies(){
		boolean armyAdded = false;
		for(Army army : armies){
			if(army.getSize() > launchSize && !army.attackLaunched()){
				army.attack(new PositionOrUnit(intelManager.getEnemyHQ()));
				armyAdded = true;
			}
		}
		if(armyAdded){
			newArmy = new Army();
			this.armies.add(newArmy);
		}
	}
	
	public void addUnit(Unit unit) {
		newArmy.addUnit(unit);
	}
	public int size(){
		int sum = 0;
		for(Army army : armies){
			sum += army.getSize();
		}
		return sum;
	}
	public void attack(Position position) {
		System.out.println("attack!");
		isMoving = true;
		for(Army army : armies){
			army.attack(new PositionOrUnit(position));
		}
	}
	public void attack(PositionOrUnit target) {
		System.out.println("attack!!");
		for(Army army : armies){
			army.attack(target);
		}	
		
	}

	public Position getPosition() {
		return armies.get(0).getPosition();
	}

	public void unitDestroyed(Unit unit) {
		for(Army army : armies){
			army.unitDestroyed(unit);
		}
	}
}
