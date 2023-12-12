package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class EscortPackageAlt extends BaseHullMod {

//	private static float MANEUVER_PERCENT = 25;
//	private static float PD_RANGE = 75;
//	public static float FIGHTER_DAMAGE_BONUS = 25f;
//	public static float MISSILE_DAMAGE_BONUS = 25f;
	private static float MANEUVER_PERCENT = 25;
	private static float PD_RANGE = 100;
	public static float FIGHTER_DAMAGE_BONUS = 25f;
	public static float MISSILE_DAMAGE_BONUS = 25f;
	
	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		
		MutableShipStatsAPI stats = fighter.getMutableStats();
		
		stats.getDamageToFighters().modifyFlat(id, FIGHTER_DAMAGE_BONUS / 100f);
		stats.getDamageToMissiles().modifyFlat(id, MISSILE_DAMAGE_BONUS / 100f);
		
		stats.getBeamPDWeaponRangeBonus().modifyFlat(id, PD_RANGE);
		stats.getNonBeamPDWeaponRangeBonus().modifyFlat(id, PD_RANGE);

		stats.getAcceleration().modifyPercent(id, MANEUVER_PERCENT);
		stats.getDeceleration().modifyPercent(id, MANEUVER_PERCENT);
		stats.getTurnAcceleration().modifyPercent(id, MANEUVER_PERCENT * 2f);
		stats.getMaxTurnRate().modifyPercent(id, MANEUVER_PERCENT);
		
		stats.getDynamic().getMod(Stats.ACT_AS_COMBAT_SHIP).modifyFlat(id, 1f);
		
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		stats.getDamageToFighters().modifyFlat(id, FIGHTER_DAMAGE_BONUS / 100f);
		stats.getDamageToMissiles().modifyFlat(id, MISSILE_DAMAGE_BONUS / 100f);
		
		stats.getBeamPDWeaponRangeBonus().modifyFlat(id, PD_RANGE);
		stats.getNonBeamPDWeaponRangeBonus().modifyFlat(id, PD_RANGE);

		stats.getAcceleration().modifyPercent(id, MANEUVER_PERCENT);
		stats.getDeceleration().modifyPercent(id, MANEUVER_PERCENT);
		stats.getTurnAcceleration().modifyPercent(id, MANEUVER_PERCENT * 2f);
		stats.getMaxTurnRate().modifyPercent(id, MANEUVER_PERCENT);
		
		stats.getDynamic().getMod(Stats.ACT_AS_COMBAT_SHIP).modifyFlat(id, 1f);
	}
	


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round(MANEUVER_PERCENT) + "%";
		if (index == 1) return "" + (int) Math.round(PD_RANGE);
		if (index == 2) return "" + (int)Math.round(FIGHTER_DAMAGE_BONUS) + "%";
		return null;
	}
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		if (shipHasOtherModInCategory(ship, spec.getId(), HullMods.TAG_CIV_PACKAGE)) return false;
		return  ship.getVariant().hasHullMod(HullMods.CIVGRADE) && super.isApplicableToShip(ship);
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

