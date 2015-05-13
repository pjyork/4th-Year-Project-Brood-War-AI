package BuildOrderManager;


import bwapi.Race;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class BuildOrderManager {
	private BuildOrder buildOrder;
	
	public BuildOrderManager (int mapPlayers,Race opponentRace){
		selectBuildOrder(mapPlayers, opponentRace);
	}

	private void selectBuildOrder(int mapPlayers, Race opponentRace) {
		selectAgressiveBuild(opponentRace);		
	}

	private void selectAgressiveBuild(Race opponentRace) {
		System.out.println("selct aggro - " + opponentRace.toString());
		BuildOrder newBuildOrder = new BuildOrder();
		newBuildOrder.queueWorkers(4);
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Pylon));
		newBuildOrder.queueWorkers(2);
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Gateway));
		newBuildOrder.queueWorkers(2);
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Assimilator));
		newBuildOrder.queueWorkers(1);
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Cybernetics_Core));
		newBuildOrder.queueWorkers(2);
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Pylon));
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
		newBuildOrder.queueWorkers(2);
		newBuildOrder.add(new BuildOrderItem(UpgradeType.Singularity_Charge));
		newBuildOrder.queueWorkers(1);
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Gateway));
		newBuildOrder.queueWorkers(2);
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Nexus));
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Pylon));
		newBuildOrder.queueWorkers(1);
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
		newBuildOrder.queueWorkers(2);
		newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Robotics_Facility));			
	
		if(opponentRace.equals(Race.Terran)){
				
		}
		else if(opponentRace.equals(Race.Zerg)){
			
		}
		else if(opponentRace.equals(Race.Protoss)){
			
		}
		else if(opponentRace.equals(Race.Random)){
			
		}
		buildOrder = newBuildOrder;
	}
	
	public BuildOrder getBuildOrder(){
		return buildOrder;
	}
	
}
