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
	

	public ArmyManager(){
		this.armies = new LinkedList<Army>();
		this.armies.add(new Army());
	}
	
	public void checkArmies(){
	}
	
	public void addUnit(Unit unit) {
		armies.get(0).addUnit(unit);
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
