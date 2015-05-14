package uctMonteCarlo;

import java.util.Hashtable;
import java.util.LinkedList;

import BattleSimulation.Action;
import BattleSimulation.SimulationController;
import BattleSimulation.SimulationGroup;

public class UCTSearch {
	TreeNode treeHead;
	int totalNumberOfTrials;
	long avgSearchTime = 0;
	int searches = 0;
	SimulationController simulationController;
	Hashtable<Integer, SimulationGroup> groups;
	
	public UCTSearch(TreeNode treeHead, SimulationController simulationController,
			Hashtable<Integer, SimulationGroup> groups){
		this.treeHead=treeHead;
		this.groups = groups;
		this.treeHead.generateChildren(groups);
		this.simulationController = simulationController;
	}
	private int treeSearch(){
		System.out.println("treeSearch");
		TreeNode node = treeHead;
		Child child = null;
		boolean myDecision;
		long start = System.currentTimeMillis();
		while(!node.isLeaf()){	
			myDecision = node.isMyDecision();
			child = node.getChild(myDecision);
			node = child.getNode();
		}
		start = System.currentTimeMillis()-start;
		avgSearchTime = (avgSearchTime*searches+start)/(searches+1);
		searches++;
		return node.generateChildren(groups);	
	}
	
	public Action findAMove(int trials){
		System.out.println("findAMove");
		Action action = new Action(null, 0, 0);
		int trialsDone = 0;
		
		while(trialsDone<trials){
			treeSearch();
			trialsDone++;
		}
		action = treeHead.getChild(treeHead.isMyDecision()).getAction();
		return action; 	
	}
	
	public Action findAMove(long timeInMillis) {
		long timeStart = System.currentTimeMillis();
		long nodesGenerated = 0;
		long treeSearches=0;
		Action action = new Action(null, 0, 0);
		
		while(System.currentTimeMillis() - timeStart < timeInMillis){
			nodesGenerated += treeSearch();
		}
		action = treeHead.getChild(treeHead.isMyDecision()).getAction();
		return action; 	 
	}
	
	
	public void reset() {
/*
		LinkedList<Child> children = new LinkedList<Child>();
		treeHead = new LinkedListTreeNode(simulationController, 0, 0);
		treeHead.generateChildren();
	*/	
	}
	
	
	public void printProfiling() {
		System.out.println("average treeSearch - " + avgSearchTime);
		treeHead.printProfiling();
		
	}

}
