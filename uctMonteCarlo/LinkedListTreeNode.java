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
	
	public LinkedListTreeNode(Hashtable<Integer, SimulationGroup> groups,
			SimulationController simulationController, int decisionGroupID){
		this.groups = groups;
		this.simulationController = simulationController;
		this.decisionGroupID = decisionGroupID;
	}
	
	
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
	public int generateChildren() {
		SimulationGroup decisionGroup = groups.get(decisionGroupID);
		Action decisionAction = decisionGroup.getAction();
		List<Action> actions = decisionGroup.generateActions();
		LinkedList<Child> newChildren = new LinkedList<Child>();
		if(actions.size() > 0){
			for(Action action : actions){
				decisionGroup.setAction(action);
				simulationController.setFramesUntilStateChange(decisionGroup, action);
				LinkedList<SimulationGroup> idleGroups = new LinkedList<SimulationGroup>();
				Hashtable<Integer, SimulationGroup> nextGroups = simulationController.groupsForNextNode(groups, idleGroups);
				int newDecisionGroupID = idleGroups.get(0).getID();
				TreeNode newNode = new LinkedListTreeNode(nextGroups, simulationController, newDecisionGroupID);
				Child newChild = new Child(newNode, action);
				newChildren.add(newChild);
			}
		}
		decisionGroup.setAction(decisionAction);
		this.children = newChildren;
		return actions.size();
	}

	@Override
	public float getOpponentValue() {
		return opponentExpectedValue;
	}

}
