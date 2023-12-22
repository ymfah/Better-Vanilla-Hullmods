package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;


public class HighVolitionAttractor extends BaseHullMod {
	// nothing to do, just a marker hullmod
	public static float CREW_CASUALTIES = 100000;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getCrewLossMult().modifyPercent(id, CREW_CASUALTIES);
	}
	
	
	public boolean isApplicableToShip(ShipAPI ship) {
		
		return ship != null && ship.getVariant().getHullSpec().getShipSystemId().contains("mote_control");
		
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		return "Mote Attractor system required.";
	}
	
}
