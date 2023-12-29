package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

@SuppressWarnings("unchecked")
public class FluxShunt extends BaseHullMod {

	public static final int HARD_FLUX_DISSIPATION_PERCENT = 50;
	public static final int SMOD_HARD_FLUX_DISSIPATION_PERCENT = 25;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite") || stats.getVariant().getHullMods().contains("ill_advised");
		String BaseHullID = stats.getVariant().getHullSpec().getBaseHullId();
		if (!sMod) stats.getHardFluxDissipationFraction().modifyFlat(id, (float)HARD_FLUX_DISSIPATION_PERCENT * 0.01f);
		if (sMod) {
			stats.getHardFluxDissipationFraction().modifyFlat(id, (float)SMOD_HARD_FLUX_DISSIPATION_PERCENT * 0.01f);
			if (BaseHullID.equals("monitor")) {
				stats.getBallisticWeaponFluxCostMod().modifyMult(id, 0);
				stats.getEnergyWeaponFluxCostMod().modifyMult(id, 0);
				stats.getMissileWeaponFluxCostMod().modifyMult(id, 0);
			} else{
				stats.getFluxDissipation().modifyMult(id, 1.5f);
			}

		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + HARD_FLUX_DISSIPATION_PERCENT + "%";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + SMOD_HARD_FLUX_DISSIPATION_PERCENT + "%";
		return null;
	}

	public void addSModEffectSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec, boolean isForBuildInList) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();;

		tooltip.addPara("The shunt is diverted. Bonus decreased to %s of the normal rate while shields are on instead.", opad, h, "50%");
		if (isForModSpec || ship == null || ship.getMutableStats() == null) return; //prevents crash from viewing it as modspec/progressive smod


		String BaseHullID = ship.getVariant().getHullSpec().getBaseHullId();
		String ShipName = ship.getVariant().getHullSpec().getHullName();

		if (BaseHullID.equals("monitor")) {
			tooltip.addPara("For the %s, shunt is diverted to the weapon mounts. Weapons no longer generate flux.", opad, h, ShipName);

		} else {
			tooltip.addPara("For the %s, shunt is used to aid flux dissipation. Increase total flux dissipation by %s.", opad, h, ShipName,"50%");
		}

	}

}
