package BaseManager;

import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class BuildingPlacer {
	private Unit hq;
	private Unit builder;
	private Game game;
	
	public BuildingPlacer(Unit hq, Game game) {
		this.hq = hq;
		this.game = game;
	}

	public void setBuilder(Unit builder){
		this.builder = builder;
	}
	
	//class with methods to decide precisely where buildings should be placed
	public TilePosition placePylon() {
		boolean locFound = false;
		int i = 1, dir = 0;
		TilePosition loc = new TilePosition(-1,-1);
		while(!locFound&&i<10){
			switch (dir){
				case 0: loc = new TilePosition(hq.getTilePosition().getX(),hq.getTilePosition().getY()-7*i);
						break;
				case 1: loc = new TilePosition(hq.getTilePosition().getX()+4+7*i,hq.getTilePosition().getY());
						break;
				case 2: loc = new TilePosition(hq.getTilePosition().getX(),hq.getTilePosition().getY()+3+7*i);
						break;
				case 3: loc = new TilePosition(hq.getTilePosition().getX()-7*i,hq.getTilePosition().getY());
						break;
			}
			if(game.canBuildHere(builder, loc, UnitType.Protoss_Pylon)){
				locFound = true;
			}
			else if(dir<3){dir++;}
			else{i++; dir = 0;}
		}
		return loc;
	}

	public TilePosition placeOther(UnitType buildingType) {
		TilePosition ret = null;
		int maxDist = 3;
		int stopDist = 40;
		TilePosition aroundTile = hq.getTilePosition();
		// Refinery, Assimilator, Extractor
		if (buildingType.isRefinery()) {
			for (Unit n : game.neutral().getUnits()) {
				if ((n.getType() == UnitType.Resource_Vespene_Geyser) && 
						( Math.abs(n.getTilePosition().getX() - aroundTile.getX()) < stopDist ) &&
						( Math.abs(n.getTilePosition().getY() - aroundTile.getY()) < stopDist )
						) {
					ret = n.getTilePosition();
				}
			}
		}
		else{
			while ((maxDist < stopDist) && (ret == null)) {
				for (int i=aroundTile.getX()-maxDist; i<=aroundTile.getX()+maxDist; i++) {
					for (int j=aroundTile.getY()-maxDist; j<=aroundTile.getY()+maxDist; j++) {
						if (game.canBuildHere(builder, new TilePosition(i,j), buildingType, false)) {
							// units that are blocking the tile
							boolean unitsInWay = false;
							for (Unit u : game.getAllUnits()) {
								if (u.getID() == builder.getID()) continue;
								if ((Math.abs(u.getTilePosition().getX()-i) < 4) && (Math.abs(u.getTilePosition().getY()-j) < 4)) unitsInWay = true;
							}
							if (!unitsInWay) {
								ret = new TilePosition(i, j);
							}
							
						}
					}
				}
				maxDist += 2;
			}
		}
		return ret;
	}
}
