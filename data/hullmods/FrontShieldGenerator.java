package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class FrontShieldGenerator extends BaseHullMod {
	
	public static float EFFICIENCY = 1.2f;
	public static float SHIELD_ARC = 90f;
	public static float OMNI_SHIELD_ARC = 60f;
	public static float SPEED_MULT = 0.85f;
	public static float ARMOR_BONUS = 15f;
	
//	private static Map mag = new HashMap();
//	static {
//		mag.put(HullSize.FRIGATE, 40f);
//		mag.put(HullSize.DESTROYER, 30f);
//		mag.put(HullSize.CRUISER, 25f);
//		mag.put(HullSize.CAPITAL_SHIP, 15f);
//	}
	
	//public void applyEffectsAfterShipCreationFirstPass(ShipAPI ship, String id) {
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		if (ship.getHullSpec().getDefenseType() == ShieldType.NONE) {
			ship.setShield(ShieldType.FRONT, 0.5f, EFFICIENCY, SHIELD_ARC);
		} 
		if (ship.getHullSpec().getDefenseType() == ShieldType.FRONT) {
			ship.setShield(ShieldType.FRONT, 0.5f, EFFICIENCY, SHIELD_ARC);
		} 
		if (ship.getHullSpec().getDefenseType() == ShieldType.OMNI) {
			ship.setShield(ShieldType.OMNI, 0.5f, EFFICIENCY, OMNI_SHIELD_ARC);
		} 
	}
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//stats.getMaxSpeed().modifyFlat(id, (Float) mag.get(hullSize) * -1f);
		
		if (stats.getVariant().getHullSpec().getDefenseType() == ShieldType.OMNI || stats.getVariant().getHullSpec().getDefenseType() == ShieldType.FRONT) {
			stats.getArmorBonus().modifyPercent(id, ARMOR_BONUS);
		} else {
			stats.getMaxSpeed().modifyMult(id, SPEED_MULT);
		}
		
	}


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) {
			return "" + (int) SHIELD_ARC;
		}
		if (index == 1) {
			return "" + (int) Math.round((1f - SPEED_MULT) * 100f) + "%";
		}
		if (index == 2) {
			return "" + (int) ARMOR_BONUS + "%";
		}
		
//		if (index == 1) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue();
//		if (index == 2) return "" + ((Float) mag.get(HullSize.DESTROYER)).intValue();
//		if (index == 3) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue();
//		if (index == 4) return "" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue();
		
		return null;
	}

	public boolean isApplicableToShip(ShipAPI ship) {
		if (ship != null && ship.getVariant().getHullMods().contains(HullMods.SHIELD_SHUNT)) {
			return false;
		}
		return ship != null && ship.getHullSpec().getDefenseType() != ShieldType.PHASE;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		return "Ship already has shields";
	}
}
