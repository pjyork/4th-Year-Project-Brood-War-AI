package uctMonteCarlo;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import BattleSimulation.Action;
import BattleSimulation.SimulationController;
import BattleSimulation.SimulationGroup;

public class LinkedListTreeNode implements TreeNode {
	private LinkedList<Child> children;
	private int numberOfTrials;
	private float myExpectedValue;
	private float opponentExpectedValue;
	//true if the decision group is controlled by my AI -
	//false if it is controlled by the opposing player
	private boolean myDecision;
	//a representation of the groups before the decision is made
	private Hashtable<Integer, SimulationGroup> groups;
	private int decisionGroupID;
	private SimulationController simulationController;
	
	
	
	@Override
	public List<Child> getChildren() {
		return children;
	}

	@Override
	public int getNumberOfTrials() {
		return numberOfTrials;
	}

	@Override
	public float getMyValue() {
		return myExpectedValue;		
	}

	@Override
	public boolean isLeaf() {
		return children.size() == 0;
	}

	@Override
	public boolean isMyDecision() {
		return myDecision;
	}

	@Override
	public void update(double ev1, double ev2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TreeNode chooseAction(Action action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int generateChildren(Hashtable<Integer, SimulationGroup> groups) {
		SimulationGroup decisionGroup = groups.get(decisionGroupID);
		List<Action> actions = decisionGroup.generateActions();
		if(actions.size() > 0){
			for(Action action : actions){
				decisionGroup.setAction(action);
				simulationController.groupsForNextNode(groups);
			}
		}
		return actions.size();
	}

	@Override
	public float getOpponentValue() {
		return opponentExpectedValue;
	}

}
