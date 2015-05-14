package uctMonteCarlo;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

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
	private int decisionGroupID;
	private SimulationController simulationController;
	private long averageUpdateTime = 0, averagePlayoutTime = 0;
	private int updates = 0, playouts = 0;
	
	public LinkedListTreeNode(SimulationController simulationController, int decisionGroupID,
			int numberOfFramesSimulated){
		this.simulationController = simulationController;
		this.decisionGroupID = decisionGroupID;
		this.numberOfFramesSimulated = numberOfFramesSimulated;
		this.children = new LinkedList<Child>();
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
	public int generateChildren(Hashtable<Integer, SimulationGroup> groups) {
		
		SimulationGroup decisionGroup = groups.get(decisionGroupID);
		if(decisionGroup != null){
			List<Action> actions = decisionGroup.generateActions();		
			if(actions.size() > 0){
				for(Action action : actions){
					Hashtable<Integer, SimulationGroup> cloneGroups = clone(groups);
					SimulationGroup cloneDecisionGroup = groups.get(decisionGroupID);
					cloneDecisionGroup.setAction(action);
					simulationController.setFramesUntilStateChange(cloneDecisionGroup, action);
					LinkedList<SimulationGroup> idleGroups = new LinkedList<SimulationGroup>();
					simulationController.progressGroups(groups, idleGroups);
					int numberOfFrames = simulationController.getFramesSimulated();
					int newDecisionGroupID = -1;
					if(!idleGroups.isEmpty()){
						newDecisionGroupID = idleGroups.get(0).getID();
					}
					TreeNode newNode = new LinkedListTreeNode(simulationController,
							newDecisionGroupID, numberOfFrames + numberOfFramesSimulated);
					Child newChild = new Child(newNode, action);
					children.add(newChild);
				}
			}
			playAllChildrenOnce(groups);
			return actions.size();
		}
		else{
			return 0;
		}
	}

	private Hashtable<Integer, SimulationGroup> clone(Hashtable<Integer, SimulationGroup> groups) {
		Hashtable<Integer, SimulationGroup> result = new Hashtable<Integer, SimulationGroup>();
		
		Iterator<Entry<Integer, SimulationGroup>> groupIter = groups.entrySet().iterator();
		while(groupIter.hasNext()){
			SimulationGroup group = groupIter.next().getValue();
			result.put(group.getID(), group.clone());
			group.setPeers(result);
		}
		return result;
	}


	private void playAllChildrenOnce(Hashtable<Integer, SimulationGroup> groups) {
		
		for(Child child : children){
			Hashtable<Integer, SimulationGroup> cloneGroups = clone(groups);
			SimulationGroup decisionGroup = cloneGroups.get(decisionGroupID);
			decisionGroup.setAction(child.getAction());
			LinkedList<SimulationGroup> idleGroups = new LinkedList<SimulationGroup>();
			simulationController.progressGroups(cloneGroups, idleGroups);
			long randomStart = 0;
			randomStart = System.currentTimeMillis()-randomStart;
			int cloneDecision = -1;
			if(!idleGroups.isEmpty()){
				cloneDecision = idleGroups.getFirst().getID();
			}
			ExpectedValue ev = simulationController.randomPlayout(cloneGroups, numberOfFramesSimulated, cloneDecision);
			averagePlayoutTime = (averagePlayoutTime*playouts+randomStart)/(playouts+1);
			playouts++;
			
			long start = System.currentTimeMillis();
			
			child.node.update(ev);

			start = System.currentTimeMillis()-start;
			averageUpdateTime = (averageUpdateTime*updates+start)/(updates+1);
			updates++;
		}
	}


	@Override
	public double getOpponentValue() {
		return opponentExpectedValue + opponentUncertainty;
	}



	@Override
	public Child getChild(boolean myDecision) {
		Child currentMaxChild = children.get(0);	
		double currentMaxValue = 0;
		
	
		
		for(int i=0;i<children.size();i++){
			Child child = children.get(i);
			double val = 0;
			if(child.node.isMyDecision()){
				val = child.node.getMyValue();
			}
			else{
				val = child.node.getOpponentValue();
			}
					
			if(val >currentMaxValue){
				currentMaxChild = child;
				currentMaxValue = val;				
			}
		}
		return currentMaxChild;
	}


	@Override
	public void printProfiling() {
		System.out.println("average update time - " + averageUpdateTime);
		System.out.println("average playout time - " + averagePlayoutTime);
	}

}
