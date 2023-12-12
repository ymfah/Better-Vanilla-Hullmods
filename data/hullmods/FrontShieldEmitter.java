package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class FrontShieldEmitter extends BaseHullMod {

	public static float ARC_BONUS = 100f;
	//public static float UPKEEP_BONUS = 50f;
	
	public static float SMOD_ARC_BONUS = 200f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		//stats.getShieldUpkeepMult().modifyMult(id, 1f - UPKEEP_BONUS * 0.01f);
		
		boolean sMod = isSMod(stats);
		if (!sMod) stats.getShieldArcBonus().modifyPercent(id, ARC_BONUS);
		if (sMod) stats.getShieldArcBonus().modifyPercent(id, SMOD_ARC_BONUS);
	}
	
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ShieldAPI shield = ship.getShield();
		if (shield != null) {
			shield.setType(ShieldType.FRONT);
		}
	}

	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ARC_BONUS + "%";
		//if (index == 1) return "" + (int) UPKEEP_BONUS + "%";
		return null;
	}

	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) SMOD_ARC_BONUS + "%";
		return null;
	}

	public boolean isApplicableToShip(ShipAPI ship) {
		if (ship.getVariant().hasHullMod("frontemitter") && ship.getShield() != null) return true;
		return ship != null && ship.getShield() != null && ship.getShield().getType() == ShieldType.OMNI;

	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship == null || ship.getShield() == null) {
			return "Ship has no shields";
		}
		
		if (ship.getShield().getType() == ShieldType.FRONT) {
			return "Ship already has front shields";
		}
		
		return null;
	}
	
	
}




