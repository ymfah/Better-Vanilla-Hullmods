package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.PhaseCloakStats;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;

public class MakeshiftPhase extends BaseHullMod {
	
	public static float PHASE_UPKEEP_COST = 0.05f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		stats.getVariant().getHullSpec().setShipSystemId("phasecloak");
		
		
		
	}
	
	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;
        if (ship.isPhased()) {
			float maxFlux = ship.getFluxTracker().getMaxFlux();
			float currentFlux = ship.getFluxTracker().getHardFlux();
			if (currentFlux >= maxFlux) {
                ship.getFluxTracker().beginOverloadWithTotalBaseDuration(5f);
			}
            ship.getFluxTracker().setHardFlux(currentFlux + maxFlux * PHASE_UPKEEP_COST * amount);
			
        }
		  
    }


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round(PHASE_UPKEEP_COST * 100f) + "%";
		return null;
	}

	public boolean isApplicableToShip(ShipAPI ship) {
		
		return ship != null && ship.getHullSpec().getDefenseType() != ShieldType.PHASE;
		
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		return "Ship already has phase";
	}
}
