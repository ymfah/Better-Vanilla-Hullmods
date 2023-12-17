package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

@SuppressWarnings("unchecked")
public class IntegrationSuite extends BaseHullMod {

	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}


}
