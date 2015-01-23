import java.util.LinkedList;

import BaseManager.*;
import BuildOrderManager.BuildOrder;
import BuildOrderManager.BuildOrderManager;
import bwapi.*;
import bwta.*;

public class MyBot {
	private Mirror mirror = new Mirror();

    private Game game;

    private Player self;
    
    private BuildOrderManager buildOrderManager;
    private BaseManager baseManager;
    
    public void run() {
        mirror.getModule().setEventListener(new DefaultBWListener() {
            
			@Override
            public void onUnitComplete(Unit unit) {
				if(unit.getType().isWorker()){
					baseManager.addWorker(unit);
				}
				if(unit.getType()==UnitType.Protoss_Pylon){
					baseManager.pylonBuilt();
				}
            }
			
			@Override
			public void onUnitCreate(Unit unit){
				if(unit.getType().isBuilding()){
					baseManager.buildingCreate(unit);
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

                buildOrderManager = new BuildOrderManager(2, Race.Terran);
                BuildOrder buildOrder = buildOrderManager.getBuildOrder();
                LinkedList<Base> bases = new LinkedList<Base>();
                Base base = new Base(hq,game);
                bases.add(base);
                baseManager = new BaseManager(bases, game, buildOrder);
               
                
            }

            @Override
            public void onFrame() {
                game.setTextSize(10);
                game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());
                baseManager.manageBases();
                
                
            }
        });

        mirror.startGame();
    }

    public static void main(String... args) {
        new MyBot().run();
    }

}
