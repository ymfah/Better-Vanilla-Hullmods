package data.hullmods;

import java.awt.Color;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.DefectiveManufactory;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;

import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class ConvertedCargoBay extends BaseHullMod {

	//public static final int CREW_REQ = 40;
	public static int REFIT_TIME_PLUS = 50;
	
	public static float RATE_INCREASE_MODIFIER = 100f;
	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//stats.getFighterRefitTimeMult().modifyPercent(id, REFIT_TIME_PLUS);
		stats.getNumFighterBays().modifyFlat(id, 2f);
		
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite");
		
		if(stats.getVariant().getHullSpec().getHullId().startsWith("colossus3")) {
		
			if(!sMod) stats.getVariant().getHullSpec().setShipSystemId("flarelauncher");
				
			if(sMod) {
				stats.getVariant().getHullSpec().setShipSystemId("reservewing"); //change to system for funi
				stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).modifyPercent(id, -RATE_INCREASE_MODIFIER);
			}
		}
	}
	
	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		new DefectiveManufactory().applyEffectsToFighterSpawnedByShip(fighter, ship, id);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
		//if (index == 2) return "" + CREW_REQ;
		return new DefectiveManufactory().getDescriptionParam(index, hullSize, ship);
	}
//	public String getDescriptionParam(int index, HullSize hullSize) {
//		if (index == 0) return "" + REFIT_TIME_PLUS + "%";
//		return null;
//	}
	
	
	public void addSModEffectSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec, boolean isForBuildInList) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();
		String hullId = ship.getVariant().getHullSpec().getHullId();
		
		tooltip.addPara("Push the flight deck to its limits. Fighter replacement rate can %s.", opad, bad, new String[]{"no longer recover"});
		
		String systemName = "";
		
		if(hullId.startsWith("colossus3")){
			systemName = "Reserve Deployment";
		} 
		
		if(systemName != "") {

			tooltip.addPara("For the %s, change the ships system to %s.", opad, h, new String[]{ship.getVariant().getHullSpec().getHullName(), systemName});

		} 
	}
	

	public boolean isApplicableToShip(ShipAPI ship) {
		return true;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		return null;
	}
}



