package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class NavRelayAlt extends BaseHullMod {
	
	public static float SPEED_INCREASE = 0.15f;
	public static float SMOD_RANGE_BONUS = 0.20f;

	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 2f);
		mag.put(HullSize.DESTROYER, 3f);
		mag.put(HullSize.CRUISER, 4f);
		mag.put(HullSize.CAPITAL_SHIP, 5f);
	}
	
	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		
		MutableShipStatsAPI stats = fighter.getMutableStats();
		
		stats.getMaxSpeed().modifyMult(id, 1f + SPEED_INCREASE);
		
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite");
		if (sMod) fighter.getMutableStats().getFighterWingRange().modifyMult(id, 1f + SMOD_RANGE_BONUS);
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.COORDINATED_MANEUVERS_FLAT).modifyFlat(id, (Float) mag.get(hullSize));
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue() + "%";
		if (index == 1) return "" + ((Float) mag.get(HullSize.DESTROYER)).intValue() + "%";
		if (index == 2) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue() + "%";
		if (index == 3) return "" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue() + "%";
		if (index == 4) return "" + (int) Math.round(SPEED_INCREASE * 100f) + "%";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round(SMOD_RANGE_BONUS * 100f) + "%";
		return null;
	}
	
}




