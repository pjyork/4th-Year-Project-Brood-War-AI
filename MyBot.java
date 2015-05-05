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
				System.out.println("unitcomplete - " + unit.getType().toString());
				if(unit.getType().isWorker()){
					baseManager.addWorker(unit);
				}
				if(unit.isBeingConstructed()){
					System.out.println("this is being constructed! it isn't complete!");
				}
            }
			
			@Override
			public void onUnitCreate(Unit unit){
				System.out.println("unitcreate - " + unit.getType().toString());
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

                buildOrderManager = new BuildOrderManager(2, Race.Terran);
                BuildOrder buildOrder = buildOrderManager.getBuildOrder();
                LinkedList<Base> bases = new LinkedList<Base>();
                Base base = new Base(hq,game);
                bases.add(base);
                baseManager = new BaseManager(bases, game, buildOrder, hq);
               
                
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
