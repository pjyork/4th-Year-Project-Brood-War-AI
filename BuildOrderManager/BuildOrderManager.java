package BuildOrderManager;


import bwapi.Race;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class BuildOrderManager {
	private BuildOrder buildOrder;
	private Race opponentRace;
	
	public BuildOrderManager (int mapPlayers,Race opponentRace){
		this.opponentRace = opponentRace;
		selectBuildOrder(mapPlayers);
	}

	private void selectBuildOrder(int mapPlayers) {
		selectAgressiveBuild();		
	}

	private void selectAgressiveBuild() {
		System.out.println("selct aggro - " + opponentRace.toString());
		BuildOrder newBuildOrder = new BuildOrder();
			
		if(opponentRace.equals(Race.Terran)){
			newBuildOrder.queueWorkers(4);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Pylon));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Gateway));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Assimilator));
			newBuildOrder.queueWorkers(1);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Cybernetics_Core));
			newBuildOrder.queueWorkers(1);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Pylon));
			newBuildOrder.queueWorkers(3);
			newBuildOrder.add(new BuildOrderItem(UpgradeType.Singularity_Charge));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Gateway));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Gateway));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Pylon));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Gateway));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));

		}
		else if(opponentRace.equals(Race.Zerg)
				|| opponentRace.equals(Race.Protoss)
				|| opponentRace.equals(Race.Random)
				|| opponentRace.equals(Race.Unknown)){
			newBuildOrder.queueWorkers(4);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Pylon));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Gateway));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.queueWorkers(1);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Gateway));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Pylon));
			newBuildOrder.queueWorkers(1);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Pylon));
			newBuildOrder.queueWorkers(4);
			newBuildOrder.queueWorkers(1);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Gateway));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Pylon));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Gateway));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Gateway));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.queueWorkers(2);
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			newBuildOrder.queueWorkers(2);
			
		}
		else if(opponentRace.equals(Race.Protoss)){
			
		}
		else if(opponentRace.equals(Race.Random)){
			
		}
		buildOrder = newBuildOrder;
		System.out.println(" build order complete ");
	}
	
	public BuildOrder getBuildOrder(){
		return buildOrder;
	}
	
	public void updateBuildOrder(int supplyRemaining){
		
		if(supplyRemaining <= 10){
			BuildOrderItem frontOfQueue = buildOrder.peek();
			boolean notUpgrade = frontOfQueue.isUnitOrBuilding();
			if(notUpgrade){
				if(frontOfQueue.unitItem() != UnitType.Protoss_Pylon){
					buildOrder.addToFront(new BuildOrderItem(UnitType.Protoss_Pylon));					
				}
			}
			else{
				buildOrder.addToFront(new BuildOrderItem(UnitType.Protoss_Pylon));
			}
		}
		if(buildOrder.size() < 10){
			if(opponentRace == Race.Terran){
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Dragoon));
			}
			else{
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
				buildOrder.add(new BuildOrderItem(UnitType.Protoss_Zealot));
			}
		}
	}
	
	public void printBuildOrder(){
		System.out.println("buildOrder");
		for(BuildOrderItem item : buildOrder){
			if(item.isUnitOrBuilding()){
				System.out.println(item.unitItem());
			}
			else{
				System.out.println(item.upgradeItem());
			}
		}
		System.out.println();
		System.out.println();
		System.out.println();
	}
	
}
