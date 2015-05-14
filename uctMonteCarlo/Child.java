package uctMonteCarlo;

import BattleSimulation.Action;

public class Child {
	TreeNode node;
	Action action;
	
	public Child(TreeNode node, Action action){
		this.node = node;
		this.action = action;
	}
	
	public Action getAction(){
		return action;
	}
	
	public TreeNode getNode(){
		System.out.println("getNode");
		return node;
	}
	
	public double getMyValue(){
		return node.getMyValue();
	}
	
	public double getOpponentValue(){
		return node.getOpponentValue();
	}
}
