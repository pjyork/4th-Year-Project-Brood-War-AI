package BattleSimulation;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import bwapi.Game;

public class SimulationController {
	private CombatCalculator combatCalculator;
	
	public SimulationController(Game game){
		combatCalculator = new CombatCalculator(game);
	}
	
	public Hashtable<Integer,SimulationGroup> groupsForNextNode(Hashtable<Integer,SimulationGroup> groups, 
			List<SimulationGroup> idleGroups){
		boolean idleFound = false;
		Hashtable<Integer,SimulationGroup> currentGroups = groups;
		Hashtable<Integer,SimulationGroup> result = new Hashtable<Integer,SimulationGroup>();
		while(!idleFound){
			int minFramesUntilChange = Integer.MAX_VALUE;
			List<SimulationGroup> groupsWhoseStateChanges = new LinkedList<SimulationGroup>();
			
			minFramesUntilChange = findNoOfFramesToSimulate(currentGroups, groupsWhoseStateChanges);
			
			simulateNFrames(minFramesUntilChange, currentGroups, result);
			
			updateStates(currentGroups, groupsWhoseStateChanges, result, idleGroups);
			currentGroups = result;
		}
		return result;
	}

	private int findNoOfFramesToSimulate(Hashtable<Integer, SimulationGroup> currentGroups,
			List<SimulationGroup> groupsWhoseStateChanges) {
		int minFramesUntilChange = Integer.MAX_VALUE;
		Iterator<Entry<Integer, SimulationGroup>> groupIter = currentGroups.entrySet().iterator();
		while(groupIter.hasNext()){
			SimulationGroup group = groupIter.next().getValue();
			int framesUntilChange = group.getFramesUntilStateChange();
			
			if(framesUntilChange < minFramesUntilChange){
				minFramesUntilChange = framesUntilChange;
				groupsWhoseStateChanges.clear();
			}
			if(framesUntilChange == minFramesUntilChange || group.getAction().getActionType() == ActionType.WAIT){
				groupsWhoseStateChanges.add(group);
			}			
		}
		return minFramesUntilChange;
	}

	private void simulateNFrames(int n,	Hashtable<Integer, SimulationGroup> currentGroups, Hashtable<Integer, SimulationGroup> result) {

		Iterator<Entry<Integer, SimulationGroup>> groupIter = currentGroups.entrySet().iterator();
		while(groupIter.hasNext()){
			SimulationGroup group = groupIter.next().getValue();
			//set the group's peers
			group.setPeers(result);
			result.put(group.getID(), group.simulateNFrames(n));
		}
	}

	private boolean updateStates(Hashtable<Integer, SimulationGroup> currentGroups,
			List<SimulationGroup> groupsWhoseStateChanges, Hashtable<Integer,
			SimulationGroup> updatedGroups, List<SimulationGroup> idleGroups) {
		boolean idleFound = false;
		Iterator<Entry<Integer, SimulationGroup>> groupIter = currentGroups.entrySet().iterator();
		while(groupIter.hasNext()){
			SimulationGroup group = groupIter.next().getValue();
			if(groupsWhoseStateChanges.contains(group)){
				updateState(group, updatedGroups, idleGroups);
			}
		}
		return idleFound;
		
	}

	private boolean updateState(SimulationGroup group,	Hashtable<Integer,
			SimulationGroup> peers, List<SimulationGroup> idleGroups) {
		Action action = group.getAction();
		ActionType actionType = action.getActionType();
		boolean idleFound = false;
		if(actionType == ActionType.ATTACK){
			SimulationGroup defenderGroup = peers.get(action.getGroup2ID());
			combatCalculator.setValues(group, defenderGroup);
			if(combatCalculator.canAttack()){
				group.setState(State.ATTACKING);
				defenderGroup.increaseIncidentDamage(combatCalculator.calculateDamagePerFrame());
			}
			else{
				group.setState(State.MOVING);
				Velocity velocity = combatCalculator.calculateVelocity();
				int travelTime = combatCalculator.calculateTravelTime();
				group.setVelocityX(velocity.getXSpeed());
				group.setVelocityY(velocity.getYSpeed());				
				group.setFramesUntilStateChange(travelTime);
			}
		}
		else if(actionType == ActionType.WAIT){
			group.setAction(new Action(ActionType.DECISION,group.getID(),-1));
			group.setState(State.IDLE);
			idleGroups.add(group);
			idleFound = true;
		}
		else if(actionType == ActionType.DECISION){
			idleFound = true;
		}
		
		return idleFound;
		
	}

	public void setFramesUntilStateChange(SimulationGroup group, Action action) {
		ActionType actionType = action.getActionType();
		if(actionType == ActionType.RETREAT || actionType == ActionType.WAIT){
			group.setFramesUntilStateChange(Integer.MAX_VALUE);
		}
		else {
			group.setFramesUntilStateChange(0);
		}
	}
	
}
