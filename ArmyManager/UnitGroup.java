package ArmyManager;

import java.util.List;

import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.Unit;

public class UnitGroup {
	private List<Unit> units;
	private Position centre;
	
	public UnitGroup(List<Unit> units){
		this.units = units;
		computeCentre();
	}
	
	public Position computeCentre(){
		int x = 0 , y = 0;
		for(Unit unit : units){
			Position pos = unit.getPosition();
			x += pos.getX();
			y += pos.getY();
		}
		int num = units.size();
		Position result = new Position(x / num, y / num);
		this.centre = result;
		return result;
	}
	
	public Position getCentre(){
		return this.centre;
	}
	
	public void join(UnitGroup that){
		this.move(that.computeCentre());
		that.beJoinedBy(this);
	}
	
	public void beJoinedBy(UnitGroup that) {
		this.units.addAll(that.units);
	}

	public void move(Position pos){
		for(Unit unit : units){
			unit.move(pos);
		}
	}
	
	public void attack(UnitGroup that){
		for(Unit unit : units){
			unit.attack(new PositionOrUnit(that.computeCentre()));
		}
	}
	
	public void centreUnits(){
		//move this group's units to the centre of the group
		for(Unit unit : units){
			unit.move(this.computeCentre());
		}
	}
}
