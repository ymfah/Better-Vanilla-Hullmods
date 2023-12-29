package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;

public class AuxiliaryThrusters extends BaseHullMod {

	public static float MANEUVER_BONUS = 50f;
	public static float SMOD_MANEUVER_BONUS = 100f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {


		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite") || stats.getVariant().getHullMods().contains("ill_advised");
		if (sMod) {
			stats.getAcceleration().modifyPercent(id, MANEUVER_BONUS * 2f);
			stats.getDeceleration().modifyPercent(id, MANEUVER_BONUS);
			stats.getTurnAcceleration().modifyPercent(id, MANEUVER_BONUS * 2f);
			stats.getMaxTurnRate().modifyPercent(id, MANEUVER_BONUS);
		} else {
			stats.getTurnAcceleration().modifyPercent(id, SMOD_MANEUVER_BONUS * 2f);
			stats.getMaxTurnRate().modifyPercent(id, SMOD_MANEUVER_BONUS);
		}
		
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) MANEUVER_BONUS + "%";
		return null;
	}
	
	@Override
	public String getSModDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
		if (index == 0) return "" + (int) SMOD_MANEUVER_BONUS + "%";
		return null;
	}


}
