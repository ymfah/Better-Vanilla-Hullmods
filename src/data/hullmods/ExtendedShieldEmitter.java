package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class ExtendedShieldEmitter extends BaseHullMod {

	public static final float SHIELD_ARC_BONUS = 60f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite") || stats.getVariant().getHullMods().contains("ill_advised");

		if(!sMod){
			stats.getShieldArcBonus().modifyFlat(id, SHIELD_ARC_BONUS);
		}else{
			stats.getShieldArcBonus().modifyFlat(id, 360f);
			stats.getShieldUpkeepMult().modifyMult(id, 360f/(stats.getVariant().getHullSpec().getShieldSpec().getArc()+60f));
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) SHIELD_ARC_BONUS;
		return null;
	}
/*
	public String getSModDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
		if (index == 0) return "" + (int) Math.round((360f/(ship.getVariant().getHullSpec().getShieldSpec().getArc()+60f)-1) * 100f) + "%";
		return null;
	}

 */

	public boolean isApplicableToShip(ShipAPI ship) {
		return ship != null && ship.getShield() != null;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		return "Ship has no shields";
	}

}



