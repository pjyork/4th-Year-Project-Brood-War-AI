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
    private BaseLocation enemyStart;
    private IntelManager intelManager;
    private boolean attackLaunched = false;
    
    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }
    
    @Override
    public void onUnitDestroy(Unit unit){
		if(unit.getPlayer() != self && unit.getPlayer() != game.neutral()){
			intelManager.removeUnit(unit);
			Position armyPosition = armyManager.getPosition();
			Unit newTarget = intelManager.getNearestUnit(armyPosition);
			System.out.println("this died - " + unit.getType());
			if(newTarget != null){
				System.out.println("new target  - " + newTarget.getType());
				armyManager.attack(new PositionOrUnit(newTarget));
			}
			else{
				System.out.println("attack again!");
				armyManager.attack(enemyStart.getPosition());
			}
		}
		else{
			if(!unit.getType().isBuilding()){
				if(!unit.getType().isWorker()){
					armyManager.unitDestroyed(unit);
				}
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
		/*if(unit.getPlayer() != self && unit.getPlayer() != game.neutral()){
			armyManager.attack(new PositionOrUnit(unit));
			intelManager.addEnemyUnit(unit);
		}*/
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
        		enemyStart = startSpot;
        	}
        }
        List<Player> players  = game.getPlayers();
        for(Player player : players){
        	if(player.isEnemy(self) && opponent == null){
        		opponent = player;
        	}
        }
        buildOrderManager = new BuildOrderManager(2, opponent.getRace());
        BuildOrder buildOrder = buildOrderManager.getBuildOrder();
        LinkedList<Base> bases = new LinkedList<Base>();
        Base base = new Base(hq,game);
        bases.add(base);
        baseManager = new BaseManager(bases, game, buildOrder, hq);
        this.armyManager = new ArmyManager();
        intelManager = new IntelManager();
        
        game.setLocalSpeed(5);
    }
	

    @Override
    public void onFrame() {
       // game.setTextSize(10);
        game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());
        baseManager.manageBases();
        if(self.supplyUsed() > 40){
        	int predictedSupply = baseManager.getTotalSupply();
        	buildOrderManager.updateBuildOrder(predictedSupply - self.supplyUsed());
        	int size = armyManager.size();
        	System.out.println("army size" + size + "  attack launched - " + attackLaunched);
            if(armyManager.size() >= 16 && !attackLaunched){
                System.out.println("nerd");
            	
            	armyManager.attack(enemyStart.getPosition());
            	attackLaunched = true;
            }       
        }
    }

    public static void main(String... args) {
        new MyBot().run();
    }

}
