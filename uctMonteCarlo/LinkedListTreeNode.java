package uctMonteCarlo;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import BattleSimulation.Action;
import BattleSimulation.ExpectedValue;
import BattleSimulation.SimulationController;
import BattleSimulation.SimulationGroup;

public class LinkedListTreeNode implements TreeNode {
	private TreeNode parent;
	private LinkedList<Child> children;
	private int numberOfFramesSimulated;
	private int numberOfTrials;
	private double myExpectedValue;
	private double opponentExpectedValue;
	private double mySquaredEV;
	private double opponentSquaredEV;
	private double myUncertainty;
	private double opponentUncertainty;
	//true if the decision group is controlled by my AI -
	//false if it is controlled by the opposing player
	private boolean myDecision;
	//a representation of the groups before the decision is made
	private Hashtable<Integer, SimulationGroup> groups;
	private int decisionGroupID;
	private SimulationController simulationController;
	
	public LinkedListTreeNode(Hashtable<Integer, SimulationGroup> groups,
			SimulationController simulationController, int decisionGroupID,
			int numberOfFramesSimulated){
		this.groups = groups;
		this.simulationController = simulationController;
		this.decisionGroupID = decisionGroupID;
		this.numberOfFramesSimulated = numberOfFramesSimulated;
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
	public double getMyValue() {
		return myExpectedValue + myUncertainty;		
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
	public void update(ExpectedValue ev) {
		if(parent!=null){
			parent.update(ev);
			myExpectedValue = (myExpectedValue * numberOfTrials + ev.myEV) / (numberOfTrials + 1);
			opponentExpectedValue = (opponentExpectedValue * numberOfTrials + ev.opponentEV) / (numberOfTrials + 1);
			
			mySquaredEV = ((mySquaredEV*numberOfTrials) + myExpectedValue * myExpectedValue) / (numberOfTrials + 1);
			opponentSquaredEV = ((opponentSquaredEV*numberOfTrials) + opponentExpectedValue * opponentExpectedValue) / (numberOfTrials + 1);
			
			numberOfTrials++;
			
			int n = parent.getNumberOfTrials()-1;
			
			double logN = Math.log(n);
			double myV = (mySquaredEV - (myExpectedValue*myExpectedValue) + Math.sqrt(logN/numberOfTrials));
			double opponentV = (opponentSquaredEV - (opponentExpectedValue*opponentExpectedValue) + Math.sqrt(logN/numberOfTrials));
			double myMultiplier = Math.min(0.25, myV);
			double opponentMultiplier = Math.min(0.25, opponentV);
			myUncertainty = Math.sqrt((logN/numberOfTrials)*myMultiplier);
			opponentUncertainty = Math.sqrt((logN/numberOfTrials)*opponentMultiplier);
		}
		else{
			numberOfTrials++;
		}
	}

	@Override
	public TreeNode chooseAction(Action action) {
		TreeNode node = null; 
		for(int i=0;i<children.size();i++){
			Child child = children.get(i);
			if(child.action.equals(action)){
				node = child.node;			
			}
		}
		return node;
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
				int numberOfFrames = simulationController.getFramesSimulated();
				int newDecisionGroupID = idleGroups.get(0).getID();
				TreeNode newNode = new LinkedListTreeNode(nextGroups, simulationController,
						newDecisionGroupID, numberOfFrames + numberOfFramesSimulated);
				Child newChild = new Child(newNode, action);
				newChildren.add(newChild);
			}
		}
		decisionGroup.setAction(decisionAction);
		this.children = newChildren;
		playAllChildrenOnce();
		return actions.size();
	}

	private void playAllChildrenOnce() {
		for(Child child : children){
			Hashtable<Integer, SimulationGroup> groups = child.node.getGroups();
			ExpectedValue ev = simulationController.randomPlayout(groups, numberOfFramesSimulated, decisionGroupID);
			child.node.update(ev);
			
		}
	}


	@Override
	public double getOpponentValue() {
		return opponentExpectedValue + opponentUncertainty;
	}


	@Override
	public Hashtable<Integer, SimulationGroup> getGroups() {
		return groups;
	}

}
