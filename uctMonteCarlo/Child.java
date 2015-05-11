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
	
	public float getMyValue(){
		return node.getMyValue();
	}
	
	public float getOpponentValue(){
		return node.getOpponentValue();
	}
}
