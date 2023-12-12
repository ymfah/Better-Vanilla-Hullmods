package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class AssaultPackageAlt extends BaseHullMod {

//	public static float FLUX_PERCENT = 25f;
//	public static float HULL_PERCENT = 25f;
//	public static float ARMOR_PERCENT = 25f;
	public static float SPEED_PERCENT = 50f;
	public static float ROF_BONUS = 25f;
	
	public static float MANEUVER_PENALTY = 100f;
	public static float RECOIL_MULT = 100f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//buffs
		stats.getMaxSpeed().modifyPercent(id, SPEED_PERCENT);
		stats.getZeroFluxSpeedBoost().modifyPercent(id, SPEED_PERCENT);
		stats.getBallisticRoFMult().modifyMult(id, 1f + ROF_BONUS * 0.01f);
		stats.getEnergyRoFMult().modifyMult(id, 1f + ROF_BONUS * 0.01f);
		stats.getMissileRoFMult().modifyMult(id, 1f + ROF_BONUS * 0.01f);
		stats.getDynamic().getMod(Stats.ACT_AS_COMBAT_SHIP).modifyFlat(id, 1f);
		
		//nerfs
		stats.getDeceleration().modifyMult(id, 1f - MANEUVER_PENALTY * 0.01f);
		stats.getMaxRecoilMult().modifyMult(id, 1f + (0.01f * RECOIL_MULT));
		stats.getRecoilPerShotMult().modifyMult(id, 1f + (0.01f * RECOIL_MULT));
	}
	


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ROF_BONUS + "%";
		if (index == 1) return "" + (int) RECOIL_MULT + "%";
		if (index == 2) return "" + (int) SPEED_PERCENT + "%";
		return null;
	}
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		if (shipHasOtherModInCategory(ship, spec.getId(), HullMods.TAG_CIV_PACKAGE)) return false;
		return ship.getVariant().hasHullMod(HullMods.CIVGRADE) && super.isApplicableToShip(ship);
	}

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (shipHasOtherModInCategory(ship, spec.getId(), HullMods.TAG_CIV_PACKAGE)) {
			return "Can only install one combat package on a civilian-grade hull";
		}
		if (!ship.getVariant().hasHullMod(HullMods.CIVGRADE)) {
			return "Can only be installed on civilian-grade hulls";
		}
		return super.getUnapplicableReason(ship);
	}
	
}

