package ArmyManager;

import java.util.LinkedList;
import java.util.List;

import IntelManager.IntelManager;
import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.Unitset;

public class Army {
	private List<Unit> units;
	private UnitGroup zealotGroup;
	private UnitGroup dragoonGroup;
	private boolean isAttacking;
	private PositionOrUnit target;
	private boolean attackLaunched;
	
	public Army(){
		attackLaunched = false;
	}	
	
	public void move(Position target){
		
	}
	
	public void attack(PositionOrUnit target){
		isAttacking = true;
		attackLaunched = true;
		if(!(zealotGroup == null)){
			zealotGroup.attack(target);
		}
		if(!(dragoonGroup == null)){
			/*(if(target.isUnit()){
				dragoonGroup.attack(target);
			}
			else if(zealotGroup != null){
				dragoonGroup.follow(zealotGroup);
			}*/
			dragoonGroup.attack(target);
		}
		this.target = target;
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

	public void addUnit(Unit unit) {
		if(unit.getType() == UnitType.Protoss_Zealot){
			if(zealotGroup == null){
				generateZealotGroup(unit);
			}
			zealotGroup.add(unit);
			if(isAttacking){
				zealotGroup.attack(target);
			}
		}
		else{
			if(dragoonGroup == null){
				generateDragoonGroup(unit);
			}
			dragoonGroup.add(unit);
			if(isAttacking){
				dragoonGroup.attack(target);
			}
		}
		
	}
	
	private void generateDragoonGroup(Unit unit) {
		LinkedList<Unit> unitss = new LinkedList<Unit>();
		unitss.add(unit);
		dragoonGroup = new UnitGroup(unitss);
	}

	private void generateZealotGroup(Unit unit) {
		LinkedList<Unit> unitss = new LinkedList<Unit>();
		unitss.add(unit);
		zealotGroup = new UnitGroup(unitss);
	}

	public int getSize(){
		int size = 0;
		if(zealotGroup != null){
			size += zealotGroup.size();
		}
		if(dragoonGroup != null){
			size += dragoonGroup.size();
		}
		return size;
	}

	public Position getPosition() {
		int posX = 0, posY = 0;
		if(zealotGroup != null){
			Position zealPos = zealotGroup.getCentre();
			posX = zealPos.getX();
			posY = zealPos.getY();
			if(dragoonGroup != null){
				Position dragPos = dragoonGroup.getCentre();
				posX = (posX + dragPos.getX()) /2 ;
				posY = (posX + dragPos.getY()) /2 ;
			}
		}
		else if(dragoonGroup != null){
			Position dragPos = dragoonGroup.getCentre();
			posX = posX + dragPos.getX();
			posY = posX + dragPos.getY();
			
		}
		
		return new Position(posX, posY);
	}

	public void unitDestroyed(Unit unit) {
		if(unit.getType() == UnitType.Protoss_Zealot){
			if(zealotGroup != null){
				zealotGroup.unitDestroyed(unit);
			}
		}
		else{
			if (dragoonGroup != null){
				dragoonGroup.unitDestroyed(unit);
			}
		}
	}

	public void check(IntelManager intelManager) {
		Position targett = intelManager.getEnemyHQ();
		if(zealotGroup != null){
			if(zealotGroup.allIdle()){
				if(zealotGroup.getCentre().getApproxDistance(targett) < 15){
					System.out.println("attackingUnit zeal");
					zealotGroup.attack(intelManager.getNearestUnit(zealotGroup.getCentre()));
				}	
				else{
					zealotGroup.attack(new PositionOrUnit(targett));
				}
			}
		}
		if(dragoonGroup != null){
			if(dragoonGroup.allIdle()){
				if(dragoonGroup.getCentre().getApproxDistance(targett) < 15){
					System.out.println("attackingUnit goon");
					dragoonGroup.attack(intelManager.getNearestUnit(dragoonGroup.getCentre()));
				}
				else{
					dragoonGroup.attack(new PositionOrUnit(targett));
				}
			}
		}
	}

	public boolean attackLaunched() {
		return attackLaunched;
	}

}
