package data.hullmods;

import java.awt.Color;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;

import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class AdvancedTargetingCore extends BaseHullMod {

//	public static final float BONUS = 100f;
//	
//	public String getDescriptionParam(int index, HullSize hullSize) {
//		if (index == 0) return "" + (int)BONUS + "%";
//		return null;
//	}
//	
//	
//	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
//		stats.getBallisticWeaponRangeBonus().modifyPercent(id, BONUS);
//		stats.getEnergyWeaponRangeBonus().modifyPercent(id, BONUS);
//	}


	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return true;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		return null;
		//return "Incompatible with Dedicated Targeting Core";
	}
	
	public static float RANGE_BONUS = 100f;
	public static float SMOD_RANGE_BONUS = 60f;
	public static float PD_MINUS = 40f;
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int)Math.round(RANGE_BONUS) + "%";
		if (index == 1) return "" + (int)Math.round(RANGE_BONUS - PD_MINUS) + "%";
		//if (index == 0) return "" + (int)RANGE_THRESHOLD;
		//if (index == 1) return "" + (int)((RANGE_MULT - 1f) * 100f);
		//if (index == 1) return "" + new Float(VISION_BONUS).intValue();
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int)Math.round(SMOD_RANGE_BONUS) + "%";
		return null;
	}
	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite") || stats.getVariant().getHullMods().contains("ill_advised");
		
		if(stats.getVariant().getHullSpec().getHullId().startsWith("paragon")) { //gotta do startsWith to make it apply to dmod variants
		
			if(!sMod) {
				stats.getVariant().getHullSpec().setShipSystemId("fortressshield");
			}
				
			if(sMod) {
				stats.getVariant().getHullSpec().setShipSystemId("plasmajets"); //change to system for funi
			}
//			stats.getVariant().getHullSpec().setHullName(stats.getVariant().getHullSpec().getHullId()); //debug pls ignore
		} else {
			if (!sMod) {
				stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
				stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
		
				stats.getNonBeamPDWeaponRangeBonus().modifyPercent(id, -PD_MINUS);
				stats.getBeamPDWeaponRangeBonus().modifyPercent(id, -PD_MINUS);
			}
			if (sMod) {
				stats.getBallisticWeaponRangeBonus().modifyPercent(id, SMOD_RANGE_BONUS);
				stats.getEnergyWeaponRangeBonus().modifyPercent(id, SMOD_RANGE_BONUS);
				stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, 2f); // set to two, meaning boost is always on 
			}
		}
	}
	@Override
	public void addSModEffectSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec, boolean isForBuildInList) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();

		
		tooltip.addPara("Certain protocols are disabled in favor of mobility, similar to safety overrides. However, this reduces the range bonus of ballistic and energy weapons to %s. The range of point-defense weapons is unaffected.", opad, bad, new String[]{"" + (int)Math.round(SMOD_RANGE_BONUS) + "%"});

		if (isForModSpec || ship == null || ship.getMutableStats() == null) return; //prevents crash from viewing it as modspec/progressive smod

		String hullId = ship.getVariant().getHullSpec().getHullId();
		String systemName = "";
		
		if(hullId.startsWith("paragon")){
			systemName = "Plasma Jets";
		} 
		
		if(systemName != "") {

			tooltip.addPara("For the %s, change the ships system to %s.", opad, h, new String[]{ship.getVariant().getHullSpec().getHullName(), systemName});

		} else {
			tooltip.addPara("For the %s, provide zero flux boost regardless of flux levels.", opad, h, new String[]{ship.getVariant().getHullSpec().getHullName()});
		}
	}
	
}
