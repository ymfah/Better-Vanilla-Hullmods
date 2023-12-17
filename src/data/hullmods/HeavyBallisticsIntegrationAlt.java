package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class HeavyBallisticsIntegrationAlt extends BaseHullMod {

	public static final float COST_REDUCTION  = 10f;
	public static final float SMOD_COST_REDUCTION_SMALL  = 1f;
	public static final float SMOD_COST_REDUCTION_MEDIUM  = 5f;
	public static final float SMOD_COST_REDUCTION_LARGE  = 5f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite");
		if (!sMod) stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(id, -COST_REDUCTION);
		if (sMod) {
			stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(id, -SMOD_COST_REDUCTION_LARGE);
			stats.getDynamic().getMod(Stats.MEDIUM_BALLISTIC_MOD).modifyFlat(id, -SMOD_COST_REDUCTION_MEDIUM);
			stats.getDynamic().getMod(Stats.SMALL_BALLISTIC_MOD).modifyFlat(id, -SMOD_COST_REDUCTION_SMALL);
		}
		
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) COST_REDUCTION + "";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) SMOD_COST_REDUCTION_SMALL + "";
		if (index == 1) return "" + (int) SMOD_COST_REDUCTION_MEDIUM + "";
		if (index == 2) return "" + (int) SMOD_COST_REDUCTION_LARGE + "";
		return null;
	}

	@Override
	public boolean affectsOPCosts() {
		return true;
	}

}








