package ArmyManager;

import java.util.List;

import bwapi.Position;
import bwapi.Unit;

public class Army {
	private List<Unit> units;
	private List<UnitGroup> unitGroups;
	
	public void move(Position target){
		
	}
	
	public void attack(Unit target){
		
	}
	
	public Position centre(){
		int x = 0 , y = 0;
		for(Unit unit : units){
			Position pos = unit.getPosition();
			x += pos.getX();
			y += pos.getY();
		}
		int num = units.size();
		Position result = new Position(x / num, y / num);
		return result;		
	}
	
	public void spread(){
		//randomly spreads out units so that they can be grouped and UCT can do something
		Position centre = this.centre();
		int origX = centre.getX();
		int origY = centre.getY();
		for(Unit unit : units){
			int x = (int) Math.random() * 50 - 25;
			int y = (int) Math.random() * 50 - 25;
			Position targetPos = new Position(x + origX, y + origY);
			unit.move(targetPos);
		}
	}
}
