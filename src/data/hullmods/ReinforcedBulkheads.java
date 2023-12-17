package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import com.fs.starfarer.api.impl.campaign.DModManager;
public class ReinforcedBulkheads extends BaseHullMod {
	
	public static final float HULL_BONUS = 40f;
	public static final float SMOD_HULL_BONUS = 5f;
	private float DMOD_NUM = 0;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite");
		
		DMOD_NUM = DModManager.getNumNonBuiltInDMods(stats.getVariant());
		
		if (!sMod) stats.getHullBonus().modifyPercent(id, HULL_BONUS);
		if (sMod) stats.getHullBonus().modifyPercent(id, HULL_BONUS + SMOD_HULL_BONUS * DMOD_NUM);
		
		stats.getDynamic().getMod(Stats.INDIVIDUAL_SHIP_RECOVERY_MOD).modifyFlat(id, 1000f);
		stats.getBreakProb().modifyMult(id, 0f);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) HULL_BONUS + "%";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) SMOD_HULL_BONUS + "%";
		return null;
	}
	
}



