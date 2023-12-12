package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class ECMPackageAlt extends BaseHullMod {
	
	public static float RANGE_BONUS = 25f;
	public static float REGEN_BONUS = 25f;
	public static float COOLDOWN_BONUS = 0.8f;

	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 1f);
		mag.put(HullSize.DESTROYER, 2f);
		mag.put(HullSize.CRUISER, 3f);
		mag.put(HullSize.CAPITAL_SHIP, 4f);
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat(id, (Float) mag.get(hullSize));
		boolean sMod = isSMod(stats);
		stats.getSystemRangeBonus().modifyPercent(id, RANGE_BONUS);
		if (sMod) {
			stats.getSystemRegenBonus().modifyPercent(id, REGEN_BONUS);
			stats.getSystemCooldownBonus().modifyMult(id, COOLDOWN_BONUS);
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue() + "%";
		if (index == 1) return "" + ((Float) mag.get(HullSize.DESTROYER)).intValue() + "%";
		if (index == 2) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue() + "%";
		if (index == 3) return "" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue() + "%";
		if (index == 4) return "" + (int) RANGE_BONUS + "%";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) REGEN_BONUS + "%";
		if (index == 1) return "" + (int) Math.round((1f - COOLDOWN_BONUS) * 100f) + "%";
		return null;
	}
}




