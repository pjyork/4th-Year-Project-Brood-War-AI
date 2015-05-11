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
	
	public Hashtable<Integer,SimulationGroup> groupsForNextNode(Hashtable<Integer,SimulationGroup> groups){
		boolean idleFound = false;
		Hashtable<Integer,SimulationGroup> currentGroups = groups;
		Hashtable<Integer,SimulationGroup> result = new Hashtable<Integer,SimulationGroup>();
		while(!idleFound){
			int minFramesUntilChange = Integer.MAX_VALUE;
			List<SimulationGroup> groupsWhoseStateChanges = new LinkedList<SimulationGroup>();
			
			minFramesUntilChange = findNoOfFramesToSimulate(currentGroups, groupsWhoseStateChanges);
			
			simulateNFrames(minFramesUntilChange, currentGroups, result);
			
			updateStates(currentGroups, groupsWhoseStateChanges, result);
			currentGroups = result;
		}
		return result;
	}

	private void updateStates(Hashtable<Integer, SimulationGroup> currentGroups,
			List<SimulationGroup> groupsWhoseStateChanges, Hashtable<Integer, SimulationGroup> result) {

		Iterator<Entry<Integer, SimulationGroup>> groupIter = currentGroups.entrySet().iterator();
		while(groupIter.hasNext()){
			SimulationGroup group = groupIter.next().getValue();
			if(groupsWhoseStateChanges.contains(group)){
				updateState(group, result);
			}
		}
		
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
			if(framesUntilChange == minFramesUntilChange){
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

	private void updateState(SimulationGroup group,	Hashtable<Integer, SimulationGroup> peers) {
		Action action = group.getAction();
		if(action.getActionType() == ActionType.ATTACK){
			combatCalculator.setValues(group, peers.get(action.getGroup2ID()));
		}
		
	}
	
}
