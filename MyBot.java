import java.util.LinkedList;
import java.util.List;

import ArmyManager.ArmyManager;
import BaseManager.*;
import BuildOrderManager.BuildOrder;
import BuildOrderManager.BuildOrderManager;
import IntelManager.IntelManager;
import bwapi.*;
import bwta.*;

public class MyBot extends DefaultBWListener {
	private Mirror mirror = new Mirror();

    private Game game;

    private Player self;
    private Player opponent;
    
    private BuildOrderManager buildOrderManager;
    private BaseManager baseManager;
    private ArmyManager armyManager;
    private BaseLocation opponentStart;
    private IntelManager intelManager;
    private boolean attackLaunched = false;
    private Race opponentRace;
    private List<BaseLocation> baseLocations;
    
	private boolean hqDestroyed;
	private int timer;
	private int timerTwo;
    
    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }
    
    @Override
    public void onUnitDestroy(Unit unit){
		if(unit.getPlayer() != self && unit.getPlayer() != game.neutral()){
			intelManager.removeUnit(unit);
			boolean hqDestroyed = unit.getType().isResourceDepot() && unit.getTilePosition().equals(opponentStart.getTilePosition());
			if(hqDestroyed && !this.hqDestroyed){
				this.baseLocations = BWTA.getBaseLocations();
				this.hqDestroyed = true;
				System.out.println("here we go!!");
				PositionOrUnit newTarget = intelManager.getTarget();
				System.out.println(newTarget.getPosition().toString());
				armyManager.attack(newTarget);
				intelManager.addLocations(this.baseLocations);
				game.setLocalSpeed(5);
			}
			else if(this.hqDestroyed){
				System.out.println("here we go!!");
				PositionOrUnit newTarget = intelManager.getTarget();
				System.out.println(newTarget.getPosition().toString());
				armyManager.attack(newTarget);				
			}
			else{
				/*System.out.println("attack again!");
				armyManager.attack(enemyStart.getPosition());*/				
			}
		}
		else{
			if(!unit.getType().isBuilding()){
				if(!unit.getType().isWorker()){
					armyManager.unitDestroyed(unit);
				}
				else{
					baseManager.workerDestroyed(unit);
				}
			}	
			else{
				baseManager.buildingDestroyed(unit);
			}
		}
    	
    }
    
    @Override
    public void onUnitComplete(Unit unit) {
		if(unit.getType().isWorker()){
			baseManager.addWorker(unit);
		}
		else if(unit.getType().isBuilding()){
			baseManager.buildingComplete(unit);
		}
		else{
			armyManager.addUnit(unit);
		}
		/*if(unit.getPlayer() != self && unit.getPlayer() != game.neutral()  && !unit.isCloaked()){
			//armyManager.attack(new PositionOrUnit(unit));
			intelManager.addEnemyUnit(unit);
		}*/
    }
	
    @Override
    public void onUnitDiscover(Unit unit){

		if(unit.getPlayer() != self && unit.getPlayer() != game.neutral()  && !unit.isCloaked()){
			//armyManager.attack(new PositionOrUnit(unit));
			intelManager.addEnemyUnit(unit);
		}
    }
    
	@Override
	public void onUnitCreate(Unit unit){
		if(unit.getType().isBuilding()){
			baseManager.buildingCreate(unit);
		}
		if(unit.getType().equals(UnitType.Protoss_Assimilator)){
			System.out.println("assim created");
		}
	}
	
	@Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();

        //Use BWTA to analyze map
        //This may take a few minutes if the map is processed first time!
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
        Unit hq = null;
        for (Unit myUnit : self.getUnits()) {
        	if(myUnit.getType() == UnitType.Protoss_Nexus){
        		hq = myUnit;
        	}
        }

        List<BaseLocation> startSpots = BWTA.getStartLocations();
        for(BaseLocation startSpot : startSpots){
        	int startSpotX = startSpot.getTilePosition().getX();
        	System.out.println(startSpot.getTilePosition().toString());
        	System.out.println(hq.getTilePosition().toString());
        	if(!(Math.abs(startSpotX - hq.getTilePosition().getX()) < 10)){
        		opponentStart = startSpot;
        	}
        }
        List<Player> players  = game.getPlayers();
        for(Player player : players){
        	System.out.println(player.getID());
    		System.out.println(player.getRace());
        	if(player.isEnemy(self) && !player.equals(self) && opponent == null && player.getID()<2){
        		opponent = player;
        		opponentRace = player.getRace();
        		System.out.println(opponentRace);
        	}
        }
        buildOrderManager = new BuildOrderManager(2, opponentRace);
        BuildOrder buildOrder = buildOrderManager.getBuildOrder();
        LinkedList<Base> bases = new LinkedList<Base>();
        Base base = new Base(hq,game);
        bases.add(base);
        baseManager = new BaseManager(bases, game, buildOrder, hq);
        intelManager = new IntelManager(opponentStart.getPosition());
        this.armyManager = new ArmyManager(intelManager,19);
        attackLaunched = false;
        game.setLocalSpeed(5);
        hqDestroyed = false;
        timer = 0;
        timerTwo = 0;
    }
	

    @Override
    public void onFrame() {
       // game.setTextSize(10);
        game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());
        baseManager.manageBases();
        armyManager.checkArmies();
        if(self.supplyUsed() > 40){
        	int predictedSupply = baseManager.getTotalSupply();
        	buildOrderManager.updateBuildOrder(predictedSupply - self.supplyUsed());
        	int size = armyManager.size();
        	
        	if(opponentRace == Race.Terran){
	        	if(armyManager.size() >= 19 && !attackLaunched){
	                System.out.println("nerd");
	            	
	            	//armyManager.attack(opponentStart.getPosition());
	            	attackLaunched = true;
	        		game.setLocalSpeed(5);
	            }       
        	}
        	else if(opponentRace == Race.Zerg){
        		if(armyManager.size() >= 19 && !attackLaunched){
	            	
	            	//armyManager.attack(opponentStart.getPosition());
	            	attackLaunched = true;
	        		game.setLocalSpeed(5);
        			
        		}
        	}
        	else if(opponentRace == Race.Protoss){
        		if(armyManager.size() >= 19 && !attackLaunched){
	            	
	            	//armyManager.attack(opponentStart.getPosition());
	            	attackLaunched = true;
	        		game.setLocalSpeed(5);
        			
        		}
        	}
        	else {
        		if(armyManager.size() >= 22 && !attackLaunched){
	            	
	            	//armyManager.attack(opponentStart.getPosition());
	            	attackLaunched = true;
	        		game.setLocalSpeed(5);
        			
        		}
        	}
        	if(hqDestroyed){
        		if(timerTwo >= 20200){
        			//30 mins have been played out
        			System.out.println("concede");
        			game.leaveGame();
        		}
        		if(timer >= 1000){
    				PositionOrUnit newTarget = intelManager.getTarget();
    				System.out.println(newTarget.getPosition().toString());
    				armyManager.attack(newTarget);
    				timer = 0;
        		}
        		timer++;	
        		timerTwo++;
        	}
        }
    }

    public static void main(String... args) {
        new MyBot().run();
    }

}
