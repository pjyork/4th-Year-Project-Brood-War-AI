package ArmyManager;

import java.util.LinkedList;
import java.util.List;

import bwapi.Position;
import bwapi.Unit;

public class GroupDecider {
	public List<UnitGroup> getGroupings(List<Unit> units){
		LinkedList<UnitGroup> result =  initialiseUnitGroups(units);
		int distanceBound = 10;
		
		
		return result;
	}

	private LinkedList<UnitGroup> initialiseUnitGroups(List<Unit> units) {
		LinkedList<UnitGroup> result =  new LinkedList<UnitGroup>();
		//we use average linkage agglomerative clustering to , with an distance upper bound to group stuff
		for(Unit unit : units){
			//to initialise make a unitGroup for each unit
			LinkedList<Unit> unitList = new LinkedList<Unit>();
			unitList.add(unit);
			UnitGroup unitGroup = new UnitGroup(unitList);
			result.add(unitGroup);
		}
		return result;
	}
}
