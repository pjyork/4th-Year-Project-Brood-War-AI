package BattleSimulation;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import bwapi.Player;
import bwapi.Position;
import bwapi.UnitType;


//represents a group of units in the simulation
public class SimulationGroup {
	private int groupID;
	private Player controller;
	private UnitType type;
	private Position pos;
	private Action action;
	private State state;
	
	//the other simulationgroups at the node
	private Hashtable<Integer,SimulationGroup> peers;
	//a representation of the health of the group
	private int hitPoints;
	//number of frames before this group changes state (walking to attacking, attacking to idle etc.)
	private int framesUntilStateChange;
	//velocity on x axis
	private double velocityX;
	//velocity on y axis
	private double velocityY;
	//amount of damage being dealt per frame;
	private double incidentDamagePerFrame;
	
	public SimulationGroup(int groupID, Player controller, UnitType type,
			Position pos, int hitPoints){
		this.groupID = groupID;
		this.controller = controller;
		this.type = type;
		this.pos = pos;
		this.hitPoints = hitPoints;
		this.action = new Action(ActionType.DECISION,groupID,-1);
		this.state = State.IDLE;
	}
	
	public List<Action> generateActions(){
		System.out.println("generateAction - " + groupID + " " + this.toString());
		LinkedList<Action> result = new LinkedList<Action>();
		Iterator<Entry<Integer, SimulationGroup>> groupIter = peers.entrySet().iterator();
		while(groupIter.hasNext()){
			SimulationGroup that = groupIter.next().getValue();
			if(!that.getPlayer().equals(controller) && that.getHitPoints() > 0){
				result.add(new Action(ActionType.ATTACK, groupID, that.groupID));
			}
		}
		result.add(new Action(ActionType.WAIT, groupID, -1));
		result.add(new Action(ActionType.RETREAT, groupID, -1));
		return result;
	}
	
	public void setPeers(Hashtable<Integer,SimulationGroup> peers){
		this.peers = peers;
	}
	
	public void increaseIncidentDamage(double damagePerFrame){
		incidentDamagePerFrame += damagePerFrame;
	}
	
	public boolean equals(SimulationGroup that){
		return this.groupID == that.groupID;
	}
	
	public UnitType getType() {
		return type;
	}

	public Player getPlayer() {
		return controller;
	}
	
	public Position getPosition(){
		return pos;
	}
	
	public int getFramesUntilStateChange(){
		return framesUntilStateChange;
	}
	
	public void setAction(Action action){
		this.action = action;
	}
	
	public void simulateNFrames(int n){
		int oldX = pos.getX();
		int oldY = pos.getX();
		int newX =  oldX + (int) velocityX * n;
		int newY =  oldY + (int) velocityY * n;
		newX = Math.min(1000, Math.max(0, newX));
		newY = Math.min(1000, Math.max(0, newY));
		
		hitPoints = Math.max(hitPoints - (int) (n * incidentDamagePerFrame), 0);
		
		pos = new Position(newX,newY);
		framesUntilStateChange -= n;
	}

	public void setState(State state) {
		this.state = state;
		if(state != State.MOVING){
			this.velocityX = 0;
			this.velocityY = 0;
		}
	}
	
	public State getState(){
		return state;
	}

	public Integer getID() {
		return groupID;
	}

	public Action getAction() {
		return action;
	}

	public int getHitPoints() {
		return hitPoints;
	}
	
	public void setVelocityX(Double velocityX){
		this.velocityX = velocityX;
	}
	
	public void setVelocityY(Double velocityY){
		this.velocityY = velocityY;
	}
	
	public void setFramesUntilStateChange(int frames){
		this.framesUntilStateChange = frames;
	}

	public int getFramesToLive() {
		return (int) (hitPoints / incidentDamagePerFrame);
	}
	
	public SimulationGroup clone(){
		SimulationGroup result = new SimulationGroup(groupID, controller, type, pos, framesUntilStateChange);
		result.setAction(action);
		result.setState(state);
		result.setVelocityX(velocityX);
		result.setVelocityY(velocityY);
		return result;		
	}
}
