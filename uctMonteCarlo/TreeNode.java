package uctMonteCarlo;

import java.util.Hashtable;
import java.util.List;

import BattleSimulation.Action;
import BattleSimulation.SimulationGroup;

public interface TreeNode {
	List<Child> getChildren();// get a list of the node's children
	int getNumberOfTrials();// get the number of times this node has been played out
	float getMyValue();//get the expected  for the AI from this position
	float getOpponentValue();//get the expected return for the opponent from this position
	boolean isLeaf();//returns whether the node is a leaf (does it have any children yet)
	boolean isMyDecision();//is true if it's a decision of the AI
	void update(double ev1, double ev2);//updates a node and propagates the update back to the tree head
	TreeNode chooseAction(Action action);
	int generateChildren();
	
	
	
}
