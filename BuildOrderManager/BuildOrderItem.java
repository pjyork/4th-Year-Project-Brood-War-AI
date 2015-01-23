package BuildOrderManager;

import bwapi.UnitType;
import bwapi.UpgradeType;

public class BuildOrderItem {
	private UnitType unit;
	private UpgradeType upgrade;
	
	public BuildOrderItem(UnitType unit){
		assert(unit != null);
		this.unit = unit;
		this.upgrade = null;
	}
	
	public BuildOrderItem(UpgradeType upgrade){
		assert(upgrade != null);
		this.unit = null;
		this.upgrade = upgrade;
	}
	
	
	public boolean isUnitOrBuilding(){
		//returns true if the next thing in the queue is not an upgrade i.e. if it returns UnitType
		return unit != null;	
	}
	public UnitType unitItem(){
		//returns the UnitType to be built iff isUnitOrBuilding returns true
		// else returns null
		//precondition - isUnitOrBuilding has been called and has returned true
		return unit;
	}
	
	public UpgradeType upgradeItem(){
		//returns the UpgradeType to be built iff isUnitOrBuilding returns false
		// else returns null
		//precondition - isUnitOrBuilding has been called and has returned false
		return upgrade;
	}	
}
