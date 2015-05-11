package BattleSimulation;



public class Action {
	private ActionType actionType;
	private final int group1ID;
	private final int group2ID;
	//number of frames before a change in state will happen 
	//eg group becomes idle - group begins to attack opposing group
	private int framesUntilChange;
	
	public Action(ActionType actionType, int group1ID, int group2ID){
		this.actionType = actionType;
		this.group1ID = group1ID;
		this.group2ID = group2ID;
	}
	
	public boolean equals(Action that){
		return this.actionType == that.actionType && this.group1ID == that.group1ID && this.group2ID == that.group2ID;
	}
	
	public int getGroup1ID() {
		return group1ID;
	}

	public int getGroup2ID() {
		return group2ID;
	}

	public ActionType getActionType() {
		return actionType;
	}	
}
