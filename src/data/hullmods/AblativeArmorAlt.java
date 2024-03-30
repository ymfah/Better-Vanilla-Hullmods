package data.hullmods;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.Global;


import java.awt.*;

public class AblativeArmorAlt extends BaseHullMod {

	public static float ARMOR_MULT = 0.1f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite") || stats.getVariant().getHullMods().contains("ill_advised");
		
		if(stats.getVariant().getHullSpec().getHullId().equals("invictus")||stats.getVariant().getHullSpec().getHullId().equals("invictus_hbismod")) {
		
			if(!sMod) stats.getVariant().getHullSpec().setShipDefenseId("canister_flak");
			if(sMod) stats.getVariant().getHullSpec().setShipDefenseId("damper_omega"); //change to system for funi

		} else {
			if(!sMod){
				stats.getEffectiveArmorBonus().modifyMult(id, ARMOR_MULT);
				stats.getMinArmorFraction().modifyMult(id, ARMOR_MULT);
			} else {
				float hullBonus = stats.getVariant().getHullSpec().getArmorRating()*0.9f;
				stats.getArmorBonus().modifyPercent(id, -90f);
				stats.getHullBonus().modifyFlat(id,hullBonus);

			}
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round(ARMOR_MULT * 100f) + "%";
		return null;
	}

	public void addSModEffectSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec, boolean isForBuildInList) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();;

		tooltip.addPara("The non-standard armor is modified.", opad, h);
		if (isForModSpec || ship == null || ship.getMutableStats() == null) return; //prevents crash from viewing it as modspec/progressive smod


		String BaseHullID = ship.getVariant().getHullSpec().getBaseHullId();
		String ShipName = ship.getVariant().getHullSpec().getHullName();

		if (BaseHullID.equals("invictus")) {
			tooltip.addPara("For the %s, outfit the ship's canister flak system to an experimental damper field.", opad, h, ShipName);

		} else {
			tooltip.addPara("For the %s, the non-standard armor is replaced with space grade armor. The rest is filled with hull.", opad, h, ShipName);
		}

	}


}








