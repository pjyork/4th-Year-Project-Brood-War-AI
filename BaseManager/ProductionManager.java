package BaseManager;

import java.util.LinkedList;
import java.util.Queue;

import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class ProductionManager {
	private Queue<Unit> gateways;
	private Queue<Unit> robotics;
	private Queue<Unit> stargates;
	private Queue<Unit> forges;
	private Queue<Unit> nexuses;
	private Unit cyberneticsCore;
	private Unit templarArchives;
	private Unit citadelOfAdun;
	private Unit roboticsSupportBay;
	private Unit observatory;
	private Unit fleetBeacon;
	private Unit arbiterTribunal;
	
	
	
	public ProductionManager(Unit hq) {
		gateways = new LinkedList<Unit>();
		stargates = new LinkedList<Unit>();
		robotics = new LinkedList<Unit>();
		forges = new LinkedList<Unit>();
		nexuses = new LinkedList<Unit>();
		nexuses.add(hq);
	}

	public boolean buildUnit(UnitType unit) {
		if(gatewayCanProduce(unit)){
			return gatewayTrain(unit);
		}
		else if(roboticsCanProduce(unit)){
			return roboticsTrain(unit);
		}
		else if(stargateCanProduce(unit)){
			return stargateTrain(unit);
		}
		else if(unit == UnitType.Protoss_Probe){
			return nexusTrain(unit);
		}
		return false;
	}

	private boolean stargateCanProduce(UnitType unit) {
		if(!stargates.isEmpty()){
			if(unit == UnitType.Protoss_Corsair || unit == UnitType.Protoss_Scout){
				return true;
			}
			if(unit == UnitType.Protoss_Carrier && fleetBeacon != null){
				return true;
			}
			if(unit == UnitType.Protoss_Arbiter && arbiterTribunal != null){
				return true;
			}
		}
		return false;
	}

	private boolean roboticsCanProduce(UnitType unit) {
		if(!robotics.isEmpty()){
			if(unit == UnitType.Protoss_Shuttle){
				return true;
			}
			if(unit == UnitType.Protoss_Reaver && roboticsSupportBay != null){
				return true;
			}
			if(unit == UnitType.Protoss_Observer  && observatory != null){
				return true;
			}
		}
		return false;
	}

	private boolean gatewayCanProduce(UnitType unit) {
		if(!gateways.isEmpty()){
			if(unit == UnitType.Protoss_Zealot){
				return true;
			}
			if(unit == UnitType.Protoss_Dragoon && cyberneticsCore != null){
				return true;
			}
			if((unit == UnitType.Protoss_High_Templar || unit == UnitType.Protoss_Dark_Templar) && templarArchives != null){
				return true;
			}
		}
		return false;
	}

	private boolean gatewayTrain(UnitType unit) {
		Unit gateway = gateways.remove();
		boolean trained = gateway.train(unit);
		gateways.add(gateway);
		return trained;
	}

	private boolean roboticsTrain(UnitType unit) {
		Unit roboticsFac = robotics.remove();
		boolean trained = roboticsFac.train(unit);
		robotics.add(roboticsFac);
		return trained;
	}

	private boolean stargateTrain(UnitType unit) {
		Unit stargate = stargates.remove();
		boolean trained = stargate.train(unit);
		stargates.add(stargate);
		return trained;
	}
	
	private boolean nexusTrain(UnitType unit) {
		Unit nexus = nexuses.remove();
		boolean trained = nexus.train(unit);
		nexuses.add(nexus);
		return trained;
	}

	public void addGateway(Unit gate){
		gateways.add(gate);
	}
	
	public void addRobotics(Unit robo){
		robotics.add(robo);
	}
	
	public void addStargate(Unit stargate){
		stargates.add(stargate);
	}	
	
	public void addNexus(Unit nexus){
		nexuses.add(nexus);
	}	
	
	public void addForge(Unit forge){
		forges.add(forge);
	}
	
	public void setCyberneticsCore(Unit core){
		cyberneticsCore = core;
	}
	
	public void setTemplarArchives(Unit archives){
		templarArchives = archives;
	}

	public void gatewayDestroyed(Unit gateway){
		gateways.remove(gateway);
	}
	
	public void stargateDestroyed(Unit stargate){
		stargates.remove(stargate);
	}
	
	public void roboticsDestroyed(Unit robotic){
		robotics.remove(robotic);
	}
	
	public void forgeDestroyed(Unit forge){
		forges.remove(forge);
	}

	public boolean researchUpgrade(UpgradeType upgrade) {
		if(cyberneticsCoreCanUpgrade(upgrade)){
			return cyberneticsCore.upgrade(upgrade);
		}
		else if(upgrade == UpgradeType.Singularity_Charge){
			return cyberneticsCore.upgrade(upgrade);					
		}
		else if((upgrade == UpgradeType.Protoss_Ground_Weapons 
				|| upgrade == UpgradeType.Protoss_Ground_Armor)
				&& forges.size() > 0){
			System.out.println("forge up!");
			Unit forge =  forges.remove();
			forges.add(forge);
			return forge.upgrade(upgrade);
		}
		return false;
	}

	private boolean cyberneticsCoreCanUpgrade(UpgradeType upgrade) {
		return cyberneticsCore.canUpgrade(upgrade);
	}

	public void buildingCompleted(Unit building) {
		UnitType buildingType = building.getType();
		if(buildingType == UnitType.Protoss_Gateway){
			addGateway(building);
		}
		else if(buildingType == UnitType.Protoss_Cybernetics_Core){
			setCyberneticsCore(building);
		}
		else if(buildingType == UnitType.Protoss_Forge){
			System.out.println("forge found");
			addForge(building);
		}
	}
	
	
}
