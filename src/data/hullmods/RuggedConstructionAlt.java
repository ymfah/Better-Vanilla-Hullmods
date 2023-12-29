package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import com.fs.starfarer.api.impl.campaign.DModManager;

public class RuggedConstructionAlt extends BaseHullMod {

	public static float DEPLOYMENT_COST_MULT = 0.5f;
	
//	public static float MIN_HULL = 30f;
//	public static float MAX_HULL = 40f;
//	
//	public static float MIN_CR = 30f;
//	public static float MAX_CR = 40f;
//	
//	public static float CR_LOSS_WHEN_DISABLED = 0.1f;
//	public static float REPAIR_FRACTION = 0.5f;
	
	public static float DMOD_EFFECT_MULT = 0.5f;
	public static float SMOD_DMOD_EFFECT_MULT = 0.75f;
	public static float DMOD_AVOID_CHANCE = 50f;
	
	private float DMOD_NUM = 0;
	public static float HULL_BONUS = 20f;
	public static float ARMOR_BONUS = 20f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//stats.getDynamic().getMod(Stats.DMOD_AVOID_PROB_MOD).modifyFlat(id, DMOD_AVOID_CHANCE * 0.01f);

		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite") || stats.getVariant().getHullMods().contains("ill_advised");
		if (!sMod) stats.getDynamic().getStat(Stats.DMOD_EFFECT_MULT).modifyMult(id, DMOD_EFFECT_MULT);
		
		DMOD_NUM = DModManager.getNumNonBuiltInDMods(stats.getVariant());
		
		if (sMod) stats.getDynamic().getStat(Stats.DMOD_EFFECT_MULT).modifyMult(id, SMOD_DMOD_EFFECT_MULT);
		if (sMod) stats.getHullBonus().modifyPercent(id, HULL_BONUS * DMOD_NUM);
		if (sMod) stats.getArmorBonus().modifyPercent(id, ARMOR_BONUS * DMOD_NUM);
		
		
		stats.getDynamic().getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, (1f - DMOD_AVOID_CHANCE * 0.01f));
		
		stats.getDynamic().getMod(Stats.INDIVIDUAL_SHIP_RECOVERY_MOD).modifyFlat(id, 1000f);
		
		
		//stats.getDynamic().getStat(Stats.CR_LOSS_WHEN_DISABLED_MULT).modifyMult(id, CR_LOSS_WHEN_DISABLED);
		//stats.getDynamic().getMod(Stats.INSTA_REPAIR_FRACTION).modifyFlat(id, REPAIR_FRACTION + 0.45f);
		//stats.getDynamic().getMod(Stats.INSTA_REPAIR_FRACTION).modifyFlat(id, REPAIR_FRACTION);
		
		
//		stats.getDynamic().getMod(Stats.RECOVERED_HULL_MIN).modifyFlat(id, MIN_HULL * 0.01f);
//		stats.getDynamic().getMod(Stats.RECOVERED_HULL_MAX).modifyFlat(id, MAX_HULL * 0.01f);
//		stats.getDynamic().getMod(Stats.RECOVERED_CR_MIN).modifyFlat(id, MIN_CR * 0.01f);
//		stats.getDynamic().getMod(Stats.RECOVERED_CR_MAX).modifyFlat(id, MAX_CR * 0.01f);
		
//		stats.getMinArmorFraction().modifyFlat(id, 0.1f);
//		stats.getBeamDamageTakenMult().modifyMult(id, 0.5f);
		
		stats.getSuppliesToRecover().modifyMult(id, DEPLOYMENT_COST_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round((1f - DMOD_EFFECT_MULT) * 100f) + "%";
		if (index == 1) return "" + (int) DMOD_AVOID_CHANCE + "%";
		if (index == 2) return "" + (int) Math.round((1f - DEPLOYMENT_COST_MULT) * 100f) + "%";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round((1f - SMOD_DMOD_EFFECT_MULT) * 100f) + "%";
		if (index == 1) return "" + (int) HULL_BONUS + "%";
		if (index == 2) return "" + (int) ARMOR_BONUS + "%";
		return null;
	}

	@Override
	public boolean affectsOPCosts() {
		return true;
	}

}








