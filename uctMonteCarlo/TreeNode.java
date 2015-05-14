package uctMonteCarlo;

import java.util.Hashtable;
import java.util.List;

import BattleSimulation.Action;
import BattleSimulation.ExpectedValue;
import BattleSimulation.SimulationGroup;

public interface TreeNode {
	List<Child> getChildren();// get a list of the node's children
	int getNumberOfTrials();// get the number of times this node has been played out
	double getMyValue();//get the expected  for the AI from this position
	double getOpponentValue();//get the expected return for the opponent from this position
	boolean isLeaf();//returns whether the node is a leaf (does it have any children yet)
	boolean isMyDecision();//is true if it's a decision of the AI
	void update(ExpectedValue ev);//updates a node and propagates the update back to the tree head
	TreeNode chooseAction(Action action);
	Child getChild(boolean myDecision);
	void printProfiling();
	int generateChildren(Hashtable<Integer, SimulationGroup> groups);
	
	
	
}
