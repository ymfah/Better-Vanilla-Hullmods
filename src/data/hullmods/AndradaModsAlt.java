package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class AndradaModsAlt extends BaseHullMod {

	public static float CASUALTIES_PERCENT = 10f;
	public static float FLUX_PERCENT = 5f;
	public static float REPAIR_PERCENT = 25f;

	public static float CR_PERCENT = 0.07f;
	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getCrewLossMult().modifyPercent(id, CASUALTIES_PERCENT);
		
		stats.getFluxDissipation().modifyPercent(id, -FLUX_PERCENT);
		
		stats.getCombatEngineRepairTimeMult().modifyPercent(id, REPAIR_PERCENT);
		stats.getCombatWeaponRepairTimeMult().modifyPercent(id, REPAIR_PERCENT);


		stats.getMaxCombatReadiness().modifyFlat(id, Math.round(CR_PERCENT * 100f) * 0.01f, "Special Modifications");
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) (CR_PERCENT*100) + "%";
		if (index == 1) return "" + (int) CASUALTIES_PERCENT + "%";
		if (index == 2) return "" + (int) FLUX_PERCENT + "%";
		if (index == 3) return "" + (int) REPAIR_PERCENT + "%";
		return null;
	}


}








