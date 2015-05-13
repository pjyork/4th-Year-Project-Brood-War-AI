package BattleSimulation;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import bwapi.Game;
import bwapi.Player;

public class SimulationController {
	private CombatCalculator combatCalculator;
	private int framesSimulated = 0; 
	private Player myPlayer;
	private Player opponent;
	private int myStartingHP;
	private int opponentStartingHP;
	
	public SimulationController(Game game){
		combatCalculator = new CombatCalculator(game);
	}
	
	public Hashtable<Integer,SimulationGroup> groupsForNextNode(Hashtable<Integer,SimulationGroup> groups, 
			List<SimulationGroup> idleGroups){
		boolean idleFound = false;
		Hashtable<Integer,SimulationGroup> currentGroups = groups;
		Hashtable<Integer,SimulationGroup> result = new Hashtable<Integer,SimulationGroup>();
		int framesSimulated = 0;
		while(!idleFound){
			int minFramesUntilChange = Integer.MAX_VALUE;
			List<SimulationGroup> groupsWhoseStateChanges = new LinkedList<SimulationGroup>();
			
			minFramesUntilChange = findNoOfFramesToSimulate(currentGroups, groupsWhoseStateChanges);
			
			simulateNFrames(minFramesUntilChange, currentGroups, result);
			
			updateStates(currentGroups, groupsWhoseStateChanges, result, idleGroups);
			currentGroups = result;
			framesSimulated += minFramesUntilChange;
		}
		this.framesSimulated = framesSimulated;
		return result;
	}
	
	public int getFramesSimulated(){
		//gets the number of frames simulated during the last group generation
		return framesSimulated;
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
		else if(actionType == ActionType.RETREAT){
			group.setState(State.MOVING);
		}
		
		return idleFound;
		
	}

	public void setFramesUntilStateChange(SimulationGroup group, Action action) {
		ActionType actionType = action.getActionType();
		if(actionType == ActionType.WAIT){
			group.setFramesUntilStateChange(Integer.MAX_VALUE);
		}
		else {
			group.setFramesUntilStateChange(0);
		}
	}

	public ExpectedValue randomPlayout(Hashtable<Integer, SimulationGroup> groups,
			int framesSimulatedThusFar, int decisionGroupID) {
		LinkedList<SimulationGroup> idleGroups = new LinkedList<SimulationGroup>();
		idleGroups.add(groups.get(decisionGroupID));
		Hashtable<Integer, SimulationGroup> currentGroups = groups;
		int frames = framesSimulatedThusFar;
		boolean bothPlayersHaveGroups = true;
		while(frames < 7200 && bothPlayersHaveGroups && !idleGroups.isEmpty()){//under 5 minutes have been simulated
			idleGroups.clear();
			currentGroups = groupsForNextNode(currentGroups, idleGroups);
			SimulationGroup newDecisionGroup = idleGroups.getFirst();
			List<Action> actions = newDecisionGroup.generateActions();
			frames += framesSimulated;
			Action actionChoice = actions.get((int) Math.round(Math.random() * actions.size()));
			newDecisionGroup.setAction(actionChoice);
		}
		ExpectedValue result = getValue(currentGroups);
		return result;
	}

	private ExpectedValue getValue(Hashtable<Integer, SimulationGroup> currentGroups) {
		Iterator<Entry<Integer, SimulationGroup>> groupIter = currentGroups.entrySet().iterator();
		ExpectedValue result = new ExpectedValue();
		int myHP = 0;
		int opponentHP = 0;
		while(groupIter.hasNext()){
			SimulationGroup group = groupIter.next().getValue();
			if(group.getPlayer().equals(myPlayer)){
				myHP += group.getHitPoints();
			}
			else{
				opponentHP += group.getHitPoints();
			}
		}
		double myEV = myHP / (double) myStartingHP +  opponentStartingHP / (double) opponentHP;
		double opponentEV = opponentHP / (double) opponentStartingHP +  myStartingHP / (double) myHP;
		result.myEV = myEV;
		result.opponentEV = opponentEV;
		return result;
	}
	
}
