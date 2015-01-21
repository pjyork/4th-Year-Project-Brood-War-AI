package BuildOrderManager;

import java.util.List;

import bwapi.UnitType;

public interface BuildOrder {
	public int timeToExpand();
	public List<UnitType> buildAtSupply(int supply);//lists the things that need to built at this supply
	public void inputBuildOrder();
}
