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
	
	public SimulationController(Game game, Player myPlayer){
		combatCalculator = new CombatCalculator(game);
		this.myPlayer = myPlayer;
	}
	
	public void progressGroups(Hashtable<Integer,SimulationGroup> groups, 
			List<SimulationGroup> idleGroups){
		boolean idleFound = false;
		int framesSimulated = 0;
		int loops = 0;
		while(!idleFound && !groups.isEmpty() && framesSimulated < 7200){
			int minFramesUntilChange = Integer.MAX_VALUE;
			List<SimulationGroup> groupsWhoseStateChanges = new LinkedList<SimulationGroup>();
			
			minFramesUntilChange = findNoOfFramesToSimulate(groups, groupsWhoseStateChanges);
			
			simulateNFrames(minFramesUntilChange, groups);
			
			idleFound = updateStates(groups, groupsWhoseStateChanges, idleGroups);
			framesSimulated += minFramesUntilChange;
			
		}
		System.out.println("frames - " + framesSimulated);
		this.framesSimulated = framesSimulated;
		
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

	private void simulateNFrames(int n,	Hashtable<Integer, SimulationGroup> currentGroups) {

		Iterator<Entry<Integer, SimulationGroup>> groupIter = currentGroups.entrySet().iterator();
		while(groupIter.hasNext()){
			SimulationGroup group = groupIter.next().getValue();
			//set the group's peers
			group.simulateNFrames(n);
		}
	}

	private boolean updateStates(Hashtable<Integer, SimulationGroup> currentGroups,
			List<SimulationGroup> groupsWhoseStateChanges, List<SimulationGroup> idleGroups) {
		boolean idleFound = false;
		Iterator<Entry<Integer, SimulationGroup>> groupIter = currentGroups.entrySet().iterator();
		while(groupIter.hasNext()){
			SimulationGroup group = groupIter.next().getValue();
			if(groupsWhoseStateChanges.contains(group)){
				idleFound = updateState(group, currentGroups, idleGroups) || idleFound;
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
			if(defenderGroup.getHitPoints() <= 0){
				group.setState(State.IDLE);
				group.setAction(new Action(ActionType.DECISION,group.getID(),-1));
				idleFound = true;
				idleGroups.add(group);
				group.setFramesUntilStateChange(0);
			}
			else if(group.getState() == State.MOVING && combatCalculator.canAttack()){
				group.setState(State.ATTACKING);
				defenderGroup.increaseIncidentDamage(combatCalculator.calculateDamagePerFrame());
				group.setFramesUntilStateChange(defenderGroup.getFramesToLive());
			}
			else if(group.getState() == State.MOVING && 
					defenderGroup.getAction().getActionType() == ActionType.RETREAT &&
					defenderGroup.getType().topSpeed() >= group.getType().topSpeed()){
				group.setState(State.IDLE);
				group.setAction(new Action(ActionType.DECISION,group.getID(),-1));
				group.setVelocityX(0.0);
				group.setVelocityY(0.0);
			}
			else if(group.getState() == State.IDLE && !combatCalculator.canAttack()){
				group.setState(State.MOVING);
				Velocity velocity = combatCalculator.calculateVelocity();
				int travelTime = combatCalculator.calculateTravelTime();
				group.setVelocityX(velocity.getXSpeed());
				group.setVelocityY(velocity.getYSpeed());	
				group.setFramesUntilStateChange(travelTime);
			}
			else{
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
			group.setFramesUntilStateChange(0);
		}
		else if(actionType == ActionType.DECISION){
			idleGroups.add(group);
			idleFound = true;
		}
		else if(actionType == ActionType.RETREAT){
			combatCalculator.setValues(group, group);
			Velocity velocity = combatCalculator.calculateRetreat();
			group.setVelocityX(velocity.getXSpeed());
			group.setVelocityY(velocity.getYSpeed());		
			group.setFramesUntilStateChange(Integer.MAX_VALUE);
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
		Hashtable<Integer, SimulationGroup> currentGroups = groups;
		int frames = framesSimulatedThusFar;
		boolean bothPlayersHaveGroups = true;
		while(decisionGroupID != -1 && frames < 7200 && bothPlayersHaveGroups && !idleGroups.isEmpty()){//under 5 minutes have been simulated
			idleGroups.clear();
			progressGroups(currentGroups, idleGroups);
			SimulationGroup newDecisionGroup = idleGroups.getFirst();
			List<Action> actions = newDecisionGroup.generateActions();
			frames += framesSimulated;
			Action actionChoice = actions.get((int) (Math.random() * actions.size()));
			newDecisionGroup.setAction(actionChoice);

			Iterator<Entry<Integer, SimulationGroup>> groupIter = currentGroups.entrySet().iterator();
			boolean myGroupFound = false, opponentGroupFound = false;
			while(groupIter.hasNext()){
				SimulationGroup group = groupIter.next().getValue();
				if(group.getPlayer().getID() == 0){
					myGroupFound = true;
				}
				else{
					opponentGroupFound = true;
				}				
			}
			bothPlayersHaveGroups = myGroupFound && opponentGroupFound;			
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
