package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;

public class AblativeArmorAlt extends BaseHullMod {

	public static float ARMOR_MULT = 0.1f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getEffectiveArmorBonus().modifyMult(id, ARMOR_MULT);
		stats.getMinArmorFraction().modifyMult(id, ARMOR_MULT);
		
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite");
		
		if(stats.getVariant().getHullSpec().getHullId().equals("invictus")) {
		
			if(!sMod) stats.getVariant().getHullSpec().setShipDefenseId("canister_flak");
				
			if(sMod) stats.getVariant().getHullSpec().setShipDefenseId("canister_flak"); //change to system for funi
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round(ARMOR_MULT * 100f) + "%";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		return null;
	}


}








