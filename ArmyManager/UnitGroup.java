package ArmyManager;

import java.util.List;

import bwapi.Player;
import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.Unit;
import bwapi.UnitType;

public class UnitGroup {
	//each group can only have one type of unit
	private UnitType unitType;
	//knowing which player controls the group is important for knowing upgrades
	private Player controller;
	private List<Unit> units;

	private Position centre;
	private boolean isFollowing;
	private boolean isAttacking;
	private Unit followedUnit;
	private PositionOrUnit target;
	
	public UnitGroup(List<Unit> units){
		this.units = units;
		Unit unit = units.get(0);
		this.unitType = unit.getType();
		this.controller = unit.getPlayer();
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
	
	public double effectiveHitPoints(){
		//using the sum of the sqrts so that it is recognised that a small number of healthy units
		// is weaker than a large number of hurt units
		double ret = 0.0;
		for(Unit unit : units){
			ret += Math.sqrt((double)unit.getHitPoints());
		}
		return ret*ret;
	}
	
	public int unitArmor(){
		return units.get(0).getType().armor();		
	}

	public UnitType getType() {
		return unitType;
	}

	public Player getPlayer() {
		return controller;
	}

	public int size() {
		return units.size();
	}

	public void add(Unit unit) {
		if(isFollowing){
			unit.follow(followedUnit);
		}
		if(isAttacking){
			unit.attack(target);
		}
		this.units.add(unit);		
	}

	public void attack(PositionOrUnit target) {
		isAttacking = true;
		isFollowing = false;
		for(Unit unit : units){
			unit.attack(target);
		}
		this.target = target;
	}

	public void follow(UnitGroup zealotGroup) {
		isFollowing = true;
		isAttacking = false;
		Unit followUnit = zealotGroup.getRepresentative();
		for(Unit unit : units){
			unit.follow(followUnit);			
		}
		this.followedUnit = followUnit;
	}

	private Unit getRepresentative() {
		return units.get(0);
	}

	public void unitDestroyed(Unit unit) {
		units.remove(unit);
	}
}