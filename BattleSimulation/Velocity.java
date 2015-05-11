package BattleSimulation;

public class Velocity {
	private double xSpeed;
	private double ySpeed;
	public Velocity(double xSpeed, double ySpeed){
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
	}
	
	public double getXSpeed(){
		return this.xSpeed;
	}
	
	public double getYSpeed(){
		return this.ySpeed;
	}
}
