package BattleSimulation;

public enum ActionType {
	JOIN, 
	ATTACK,
	RETREAT, 
	DECISION, //represents that the group is awaiting a decision in the tree 
	WAIT;
}
