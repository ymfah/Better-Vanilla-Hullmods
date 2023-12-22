package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.PhaseCloakStats;

public class ExperimentalPhaseCoilsAlt extends BaseHullMod {

	public static float PHASE_COOLDOWN_REDUCTION = 80f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getPhaseCloakCooldownBonus().modifyMult(id, 1f - PHASE_COOLDOWN_REDUCTION / 100f);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round(PHASE_COOLDOWN_REDUCTION) + "%";
		return null;
	}
	
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return ship.getHullSpec().isPhase();
	}

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (!ship.getHullSpec().isPhase()) {
			return "Can only be installed on phase ships";
		}
		return super.getUnapplicableReason(ship);
	}
	
	

	
// ??? this seems unnecessary, commenting it out -am
//	@Override
//	public boolean affectsOPCosts() {
//		return true;
//	}

}








