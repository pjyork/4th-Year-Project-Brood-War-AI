package uctMonteCarlo;

import BattleSimulation.Action;

public class Child {
	TreeNode node;
	Action action;
	
	public Child(TreeNode node, Action action){
		this.node = node;
		this.action = action;
	}
	
	public TreeNode getNode(){
		return node;
	}
	
	public double getMyValue(){
		return node.getMyValue();
	}
	
	public double getOpponentValue(){
		return node.getOpponentValue();
	}
}
