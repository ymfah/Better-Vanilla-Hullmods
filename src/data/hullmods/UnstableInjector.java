package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class UnstableInjector extends BaseHullMod {
	
	public static float SMOD_MANEUVER_PENALTY = 90f;
	public static float SMOD_SPEED_MULT = 2.5f;

	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 25f);
		mag.put(HullSize.DESTROYER, 20f);
		mag.put(HullSize.CRUISER, 15f);
		mag.put(HullSize.CAPITAL_SHIP, 15f);
	}
	
//	private static final float RANGE_MULT = 0.85f;
//	private static final float FIGHTER_RATE = 25f;
//	private static final float MAX_CR_PENALTY = 0.1f;
	private static final float ENGINE_MALFUNCTION_PROB = 0.005f;
	
	//private static final float ACCELERATION_BONUS = 100f;
	//private static final float EXTRA_DAMAGE = 300f;
	//private static final int BURN_LEVEL_BONUS = 1;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite") || stats.getVariant().getHullMods().contains("ill_advised");
		stats.getMaxSpeed().modifyFlat(id, (Float) mag.get(hullSize));
		if (sMod) {
			stats.getMaxSpeed().modifyFlat(id, (Float) mag.get(hullSize)*SMOD_SPEED_MULT);
			stats.getZeroFluxSpeedBoost().modifyFlat(id, (Float) mag.get(hullSize)*SMOD_SPEED_MULT);
		} else {
			stats.getMaxSpeed().modifyFlat(id, (Float) mag.get(hullSize));
			stats.getZeroFluxSpeedBoost().modifyFlat(id, (Float) mag.get(hullSize));
		}
//		stats.getBallisticWeaponRangeBonus().modifyMult(id, RANGE_MULT);
//		stats.getEnergyWeaponRangeBonus().modifyMult(id, RANGE_MULT);	
//		stats.getFighterRefitTimeMult().modifyPercent(id, FIGHTER_RATE);
//		stats.getMaxCombatReadiness().modifyFlat(id, -MAX_CR_PENALTY, "Unstable injector penalty");

		stats.getEngineMalfunctionChance().modifyFlat(id, ENGINE_MALFUNCTION_PROB);
		
		
		if (sMod) stats.getDeceleration().modifyMult(id, 1f - SMOD_MANEUVER_PENALTY * 0.01f);
		
		//stats.getAcceleration().modifyPercent(id, ACCELERATION_BONUS);
		//stats.getEngineDamageTakenMult().modifyPercent(id, EXTRA_DAMAGE);
		
		//stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue();
		if (index == 1) return "" + ((Float) mag.get(HullSize.DESTROYER)).intValue();
		if (index == 2) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue();
		if (index == 3) return "" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue();
		if (index == 4) return "" + ENGINE_MALFUNCTION_PROB * 100f + "%";
//		if (index == 4) return "" + (int) Math.round(MAX_CR_PENALTY * 100f) + "%";
//		if (index == 4) return "" + (int) Math.round((1f - RANGE_MULT) * 100f) + "%";
//		if (index == 5) return "" + (int) Math.round(FIGHTER_RATE) + "%";
//		if (index == 4) return "" + (int) ACCELERATION_BONUS;
//		//if (index == 5) return "four times";
//		if (index == 5) return "4" + Strings.X;
//		if (index == 6) return "" + BURN_LEVEL_BONUS;
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + SMOD_SPEED_MULT;
		return null;
	}
	

}
