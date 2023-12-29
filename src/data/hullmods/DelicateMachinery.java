package data.hullmods;

import java.awt.Color;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.ui.TooltipMakerAPI;




public class DelicateMachinery extends BaseHullMod {

	public static final float DEGRADE_INCREASE_PERCENT = 50f;
	public static final float SMOD_PPT_PERCENT = 50f;

	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {



		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite") || stats.getVariant().getHullMods().contains("ill_advised");

		stats.getCRLossPerSecondPercent().modifyPercent(id, DEGRADE_INCREASE_PERCENT);
		if (sMod) stats.getPeakCRDuration().modifyPercent(id, -SMOD_PPT_PERCENT);

		
		String BaseHullID = stats.getVariant().getHullSpec().getBaseHullId();
		if (BaseHullID.equals("doom")) {
			if(!sMod) {
				stats.getVariant().getHullSpec().setShipSystemId("mine_strike"); //revert to base forced
				stats.getVariant().removePermaMod("stealth_minefield_Doom");
			}
			if(sMod) {
				stats.getVariant().getHullSpec().setShipSystemId("interdictor");
				stats.getVariant().addPermaMod("stealth_minefield_Doom");
			}
		} else if (BaseHullID.equals("shade")) {
			if(!sMod) {
				stats.getVariant().getHullSpec().setShipSystemId("emp");
				stats.getVariant().removePermaMod("shield_shunt");
			}
			if(sMod) {
				stats.getVariant().getHullSpec().setShipSystemId("emp_oc");
				stats.getVariant().addPermaMod("shield_shunt");
			}
		} else if (BaseHullID.equals("revenant")) {
			if(!sMod) {
				stats.getVariant().getHullSpec().setShipSystemId("flarelauncher_active"); //revert to base forced
				stats.getVariant().addTag("CIVILIAN");
			}
			if(sMod) {
				stats.getVariant().getHullSpec().setShipSystemId("drone_station_high");
				stats.getCargoMod().modifyMult(id, 0);
				stats.getVariant().removeTag("CIVILIAN");
			}
		} else {
			if(sMod) {
//				stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyMult(id, 2f);
				stats.getVariant().addPermaMod("integrationsuite");
			} else {
				stats.getVariant().removePermaMod("integrationsuite");
			}
		}
	}


	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) DEGRADE_INCREASE_PERCENT + "%";
		return null;
	}

	public boolean isApplicableToShip(ShipAPI ship) {
		return ship != null && (ship.getHullSpec().getNoCRLossTime() < 10000 || ship.getHullSpec().getCRLossPerSecond(ship.getMutableStats()) > 0); 
	}
	
	public void addSModEffectSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec, boolean isForBuildInList) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();;



		tooltip.addPara("Even more experimental technology is introduced. Decreases peak performance time by %s.", opad, h, "" + (int) Math.round(SMOD_PPT_PERCENT) + "%");

		if (isForModSpec || ship == null || ship.getMutableStats() == null) return; //prevents crash from viewing it as modspec/progressive smod
		String BaseHullID = ship.getVariant().getHullSpec().getBaseHullId();
		String systemName = "";
		String smodChanges = "";
		
		if (BaseHullID.equals("doom")) {
			systemName = "Interdictor Array";
			smodChanges = "Additionally, mines are now automatically deployed to the closest enemy ship within 1500su.";
		} else if (BaseHullID.equals("shade")) {
			systemName = "Overclocked EMP Emitter";
			smodChanges = "Increases range by a factor of 3. Ship can no longer phase.";
		} else if (BaseHullID.equals("revenant")) {
			systemName = "Gargoyle Drones";
			smodChanges = "These drones are stored in the cargo holds and released. Cannot be replenished mid-combat.";
		}
		
		if(systemName != "") {

			tooltip.addPara("For the %s, change the ships system to %s. %s", opad, h, new String[]{BaseHullID, systemName,smodChanges});

		}	else {
			smodChanges = "Activates the positive S-mod effect of other hullmods.";
			tooltip.addPara("For the %s, %s", opad, h, new String[]{BaseHullID, smodChanges});
		}
	}
}
