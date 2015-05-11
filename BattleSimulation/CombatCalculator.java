package BattleSimulation;

import java.awt.Point;

import bwapi.DamageType;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Region;
import bwapi.TechType;
import bwapi.UnitSizeType;
import bwapi.UnitType;
import bwapi.WeaponType;

class CombatCalculator {
	SimulationGroup attackerGroup;
	UnitType attackerType;
	UnitType defenderType;
	Player attackerPlayer;
	Player defenderPlayer;
	Position attackerPosition;
	Position defenderPosition;
	Game game;
	
	public CombatCalculator(Game game){
		this.game = game;
	}


	public void setValues(SimulationGroup attackerGroup, SimulationGroup defenderGroup){
		this.attackerGroup = attackerGroup;
		this.attackerType = attackerGroup.getType();
		this.defenderType = defenderGroup.getType();
		this.attackerPlayer = attackerGroup.getPlayer();
		this.defenderPlayer = defenderGroup.getPlayer();
		this.attackerPosition = attackerGroup.getPosition();
		this.defenderPosition = defenderGroup.getPosition();
	}
	
	public boolean canAttack(){
		//only called immediately after setValues is called
		return attackerPosition.getApproxDistance(defenderPosition) <= calculateRange();
	}
	
	private int calculateRange(){
		return attackerPlayer.weaponMaxRange(getWeapon());
	}
	
	public double calculateDamagePerFrame(){
		double damagePerAttack = calculateDamagePerAttack();
		int numberOfUnits =  (int) Math.ceil((double) attackerGroup.getHitPoints() / (double) attackerType.maxHitPoints());
		return damagePerAttack * numberOfUnits / getWeapon().damageCooldown();
	}
	
	private double calculateDamagePerAttack(){
		//only called immediately after setValues is called
		WeaponType weapon = getWeapon();
		int damage = getDamage(weapon);
		int armor = getArmor();
		//some units hit more than once in an attack
		int factor = weapon.damageFactor();
		//some attacks are more effective against certain sizes of units
		double multiplier = getMultiplier(weapon);
		double hitChance = getHitChance();
		//compute the damage per hit, then multiply it by the chance to hit
		double damagePerAttack = Math.max((damage-armor)*factor*multiplier,1)*hitChance;
		//frames between attacks
		int cooldown = getCooldown(weapon);
		return damagePerAttack / cooldown;
	}
	
	private int getCooldown(WeaponType weapon) {
		int ret = weapon.damageCooldown();
		if(attackerType == UnitType.Terran_Marine && attackerPlayer.hasResearched(TechType.Stim_Packs)){
			ret = ret/2;
		}
		return ret;
	}

	private double getHitChance() {
		Region attackerRegion = game.getRegionAt(attackerPosition);
		Region defenderRegion = game.getRegionAt(defenderPosition);
		if(defenderRegion.isHigherGround() && !attackerRegion.isHigherGround()){
			return 0.53125;
		}
		return 1;
	}

	private int getDamage(WeaponType weapon){
		return attackerPlayer.damage(weapon);
	}
	
	private WeaponType getWeapon(){
		if(defenderType.isFlyer()){
			return attackerType.airWeapon();
		}
		else{
			return attackerType.groundWeapon();
		}
	}
	
	private int getArmor(){
		return defenderPlayer.armor(defenderType);
	}
	private double getMultiplier(WeaponType weapon){
		//calculates the damage multiplier based on damage type and unit size
		DamageType damage = weapon.damageType();
		UnitSizeType size = defenderType.size();
		if(damage == DamageType.Normal){
			return 1;
		}
		else if(damage == DamageType.Concussive){
			if(size == UnitSizeType.Small){
				return 1;
			}
			else if(size == UnitSizeType.Medium){
				return 0.5;
			}
			else{
				return 0.25;
			}
		}
		else if(damage == DamageType.Explosive){
			if(size == UnitSizeType.Small){
				return 0.5;
			}
			else if(size == UnitSizeType.Medium){
				return 0.75;
			}
			else{
				return 1;
			}
		}
		return 0;
	}


	public Velocity calculateVelocity() {
		double speed = attackerType.topSpeed();
	    int xDistance = defenderPosition.getX() - attackerPosition.getX();
	    int yDistance = defenderPosition.getY() - attackerPosition.getY();
	    int distance = attackerPosition.getApproxDistance(defenderPosition);
	    
	    double xSpeed = speed * xDistance / distance;
	    double ySpeed = speed * yDistance / distance;
	    
		return new Velocity(xSpeed, ySpeed);
	}


	public int calculateTravelTime() {
		int distance = attackerPosition.getApproxDistance(defenderPosition) - getWeapon().maxRange();
		double frames = distance / attackerType.topSpeed();
		return (int) Math.ceil(frames);
	}
}
