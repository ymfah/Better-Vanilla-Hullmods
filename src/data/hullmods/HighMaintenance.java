package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class HighMaintenance extends BaseHullMod {

	private static final float SUPPLY_USE_MULT = 2f;

	private static final float SMOD_SUPPLY_USE_MULT = 3f;
	public static float FLUX_DISSIPATION_PERCENT = 100f;
	public static float FLUX_CAPACITY_PERCENT = 100f;


	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);

		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite") || stats.getVariant().getHullMods().contains("ill_advised");

		for (String s : stats.getVariant().getNonBuiltInHullmods()) {
			if (s.contains("high_maintenance")) {
				stats.getFluxDissipation().modifyPercent(id, FLUX_DISSIPATION_PERCENT);
				stats.getFluxCapacity().modifyPercent(id, FLUX_CAPACITY_PERCENT);
				stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyMult(id, SUPPLY_USE_MULT);
				stats.getSuppliesToRecover().modifyMult(id, SUPPLY_USE_MULT);
				stats.getShieldUpkeepMult().modifyMult(id, SUPPLY_USE_MULT);
			}
		}
		if (sMod) {
			if(stats.getVariant().getHullSpec().getBuiltInMods().contains("high_maintenance")) { //for hyperion
				stats.getFluxDissipation().modifyPercent(id, FLUX_DISSIPATION_PERCENT);
				stats.getFluxCapacity().modifyPercent(id, FLUX_CAPACITY_PERCENT);
				stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyMult(id, SUPPLY_USE_MULT);
				stats.getShieldUpkeepMult().modifyMult(id, SUPPLY_USE_MULT);
			} else{
				stats.getFluxDissipation().modifyPercent(id, FLUX_DISSIPATION_PERCENT*2);
				stats.getFluxCapacity().modifyPercent(id, FLUX_CAPACITY_PERCENT*2);
				stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyMult(id, SMOD_SUPPLY_USE_MULT);
				stats.getSuppliesToRecover().modifyMult(id, SMOD_SUPPLY_USE_MULT);
				stats.getShieldUpkeepMult().modifyMult(id, SMOD_SUPPLY_USE_MULT);

			}
		}


	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int)((SUPPLY_USE_MULT - 1f) * 100f) + "%";
		return null;
	}

	public void addSModEffectSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec, boolean isForBuildInList) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();

		if (isForModSpec || ship == null || ship.getMutableStats() == null) return; //prevents crash from viewing it as modspec/progressive smod

		String ShipName = ship.getVariant().getHullSpec().getHullName();
		String ShipClass = ship.getVariant().getHullSpec().getHullSize().name();
		String ShipUpperClass = "Station";

		switch(ShipClass){
			case "FRIGATE":
				ShipClass = "Frigate";
				ShipUpperClass = "Cruiser";
				break;
			case "DESTROYER":
				ShipClass = "Destroyer";
				ShipUpperClass = "Capital";
				break;
			default:
		}

		if(ship.getVariant().getHullSpec().getBuiltInMods().contains("high_maintenance")) { //for hyperion
			tooltip.addPara("For the %s, Replace the %s class power grid to a %s class grid.", opad, h, ShipName, ShipClass, ShipUpperClass);
			tooltip.addPara("Base flux capacity, base dissipation and deployment points increased by %s.", opad, h,  "" + (int) Math.round(FLUX_CAPACITY_PERCENT) + "%", (int)(SUPPLY_USE_MULT - 1f) + "%");
		}else{
			tooltip.addPara("Increases the monthly maintenance supply cost by %s.", opad, h, (int)((SUPPLY_USE_MULT - 1f) * 200f) + "%");
			tooltip.addPara("For the %s, Replace the %s class power grid to a %s class grid.", opad, h, ShipName, ShipClass, ShipUpperClass);
			tooltip.addPara("Base flux capacity, base dissipation and deployment points increased by %s.", opad, h,  "" + (int) Math.round(FLUX_CAPACITY_PERCENT*2) + "%", (int)((SUPPLY_USE_MULT - 1f) * 200f) + "%");
		}


	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, final ShipAPI ship, float width, boolean isForModSpec) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();

		if (isForModSpec || ship == null || ship.getMutableStats() == null) return; //prevents crash from viewing it as modspec/progressive smod

		String ShipName = ship.getVariant().getHullSpec().getHullName();
		String ShipClass = ship.getVariant().getHullSpec().getHullSize().name();
		String ShipUpperClass = ship.getVariant().getHullSpec().getHullSize().name();

		switch(ShipClass){
			case "FRIGATE":
				ShipClass = "Frigate";
				ShipUpperClass = "Destroyer";
				break;
			case "DESTROYER":
				ShipClass = "Destroyer";
				ShipUpperClass = "Cruiser";
				break;
			default:
		}


		for (String s : ship.getVariant().getNonBuiltInHullmods()) {
			if (s.contains("high_maintenance")) {
				tooltip.addPara("For the %s, Replace the %s class power grid to a %s class grid.", opad, h, ShipName, ShipClass, ShipUpperClass);
				tooltip.addPara("Base flux capacity, base dissipation and deployment points increased by %s.", opad, h,  "" + (int) Math.round(FLUX_CAPACITY_PERCENT) + "%", (int)((SUPPLY_USE_MULT - 1f) * 100f) + "%");
			}
		}
	}


	public boolean isApplicableToShip(ShipAPI ship) {
		if (ship.getHullSize() == HullSize.CRUISER || ship.getHullSize() ==  HullSize.CAPITAL_SHIP) {
			return false;
		}
		return ship != null;
	}


	public String getUnapplicableReason(ShipAPI ship) {
		//if (ship != null && ship.getNumFighterBays() > 0) return "Ship has fighter bays";
		return "Cannot be installed on Cruiser or Capital class ships.";
	}


}
