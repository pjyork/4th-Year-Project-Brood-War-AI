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
			gatewayTrain(unit);
			return true;
		}
		else if(roboticsCanProduce(unit)){
			roboticsTrain(unit);
			return true;
		}
		else if(stargateCanProduce(unit)){
			stargateTrain(unit);
			return true;
		}
		else if(unit == UnitType.Protoss_Probe){
			nexusTrain(unit);
			return true;
		}
		return false;
	}

	private boolean stargateCanProduce(UnitType unit) {
		if(!stargates.isEmpty()){
			if(unit == UnitType.Protoss_Corsair || unit == UnitType.Protoss_Scout){
				stargateTrain(unit);
				return true;
			}
			if(unit == UnitType.Protoss_Carrier && fleetBeacon != null){
				stargateTrain(unit);
				return true;
			}
			if(unit == UnitType.Protoss_Arbiter && arbiterTribunal != null){
				stargateTrain(unit);
				return true;
			}
		}
		return false;
	}

	private boolean roboticsCanProduce(UnitType unit) {
		if(!robotics.isEmpty()){
			if(unit == UnitType.Protoss_Shuttle){
				roboticsTrain(unit);
				return true;
			}
			if(unit == UnitType.Protoss_Reaver && roboticsSupportBay != null){
				roboticsTrain(unit);
				return true;
			}
			if(unit == UnitType.Protoss_Observer  && observatory != null){
				roboticsTrain(unit);
				return true;
			}
		}
		return false;
	}

	private boolean gatewayCanProduce(UnitType unit) {
		if(!gateways.isEmpty()){
			if(unit == UnitType.Protoss_Zealot){
				gatewayTrain(unit);
				return true;
			}
			if(unit == UnitType.Protoss_Dragoon && cyberneticsCore != null){
				gatewayTrain(unit);
				return true;
			}
			if((unit == UnitType.Protoss_High_Templar || unit == UnitType.Protoss_Dark_Templar) && templarArchives != null){
				gatewayTrain(unit);
				return true;
			}
		}
		return false;
	}

	private void gatewayTrain(UnitType unit) {
		Unit gateway = gateways.remove();
		gateway.train(unit);
		gateways.add(gateway);
	}

	private void roboticsTrain(UnitType unit) {
		Unit roboticsFac = robotics.remove();
		roboticsFac.train(unit);
		robotics.add(roboticsFac);
	}

	private void stargateTrain(UnitType unit) {
		Unit stargate = stargates.remove();
		stargate.train(unit);
		stargates.add(stargate);
	}
	
	private void nexusTrain(UnitType unit) {
		Unit nexus = nexuses.remove();
		nexus.train(unit);
		nexuses.add(nexus);
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
		//TODO
		return false;
	}
	
	
}
