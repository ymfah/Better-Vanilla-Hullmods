package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

@SuppressWarnings("unchecked")
public class FluxShunt extends BaseHullMod {

	public static final int HARD_FLUX_DISSIPATION_PERCENT = 50;
	public static final int SMOD_HARD_FLUX_DISSIPATION_PERCENT = 25;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats);
		if (!sMod) stats.getHardFluxDissipationFraction().modifyFlat(id, (float)HARD_FLUX_DISSIPATION_PERCENT * 0.01f);
		if (sMod) {
			stats.getHardFluxDissipationFraction().modifyFlat(id, (float)SMOD_HARD_FLUX_DISSIPATION_PERCENT * 0.01f);
			stats.getBallisticWeaponFluxCostMod().modifyMult(id, 0);
			stats.getEnergyWeaponFluxCostMod().modifyMult(id, 0);
			stats.getMissileWeaponFluxCostMod().modifyMult(id, 0);
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + HARD_FLUX_DISSIPATION_PERCENT + "%";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + SMOD_HARD_FLUX_DISSIPATION_PERCENT + "%";
		return null;
	}

}
