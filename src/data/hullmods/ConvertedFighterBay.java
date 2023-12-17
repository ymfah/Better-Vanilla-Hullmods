package data.hullmods;

import java.util.Map;
import java.util.HashMap;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Strings;

public class ConvertedFighterBay extends BaseHullMod {

	public static int CREW_REQ_PER_BAY = 20;
	public static int MAX_CREW = 80;
	public static int CARGO_PER_BAY = 50;
	
	public static float SMOD_HULL_BONUS = 15f;
	public static float SMOD_MAINT_PER_BAY = 15f;
	
	
	public static int MIN_DP = -1;
	
	private static Map dpsize = new HashMap();
	static {
		dpsize.put(HullSize.FRIGATE, 1f);
		dpsize.put(HullSize.DESTROYER, 1f);
		dpsize.put(HullSize.CRUISER, 2f);
		dpsize.put(HullSize.CAPITAL_SHIP, 2f);
	}
	
	public float computeCRMult(float suppliesPerDep, float dpMod) {
		return 1f + dpMod / suppliesPerDep;
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		int bays = (int) Math.round(stats.getNumFighterBays().getBaseValue());
		stats.getNumFighterBays().modifyFlat(id, -bays);

		int crewReduction = CREW_REQ_PER_BAY * bays;
		if (crewReduction > MAX_CREW) crewReduction = MAX_CREW;
		int cargo = CARGO_PER_BAY * bays;
		
//		ShipVariantAPI v = stats.getVariant();
//		if (v != null) {
//			
//		}
		
		stats.getMinCrewMod().modifyPercent(id, -crewReduction);
		stats.getCargoMod().modifyFlat(id, cargo);

		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite");
		
		//DP decrease
		float dpMod = -1f * bays * (Float) dpsize.get(hullSize);
		stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, dpMod);
		
		if (stats.getFleetMember() != null) {
			float perDep = stats.getFleetMember().getHullSpec().getSuppliesToRecover();
			float mult = computeCRMult(perDep, dpMod);
			stats.getCRPerDeploymentPercent().modifyMult(id, mult);
		}
		stats.getSuppliesToRecover().modifyFlat(id, dpMod);
		
		if (sMod) stats.getHullBonus().modifyPercent(id, SMOD_HULL_BONUS);
		
		if (sMod && bays > 0) {
			float bonus = bays * (SMOD_MAINT_PER_BAY * 0.01f);
			if (bonus > 1f) bonus = 1f;
			stats.getSuppliesPerMonth().modifyMult(id, 1f - bonus);
		}
		
	}
	
	public boolean isApplicableToShip(ShipAPI ship) {
//		int builtIn = ship.getHullSpec().getBuiltInWings().size();
		int bays = (int) Math.round(ship.getMutableStats().getNumFighterBays().getBaseValue());
		if (bays > 0) return true;
		return false;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		return "Requires standard fighter bays or drone bays";
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
		if (index == 0) return "" + (int) SMOD_MAINT_PER_BAY + "%";
		if (index == 1) return "" + (int) SMOD_HULL_BONUS + "%";
		return null;
	}
		
	public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
		if (index == 0) return "" + CARGO_PER_BAY;
		if (index == 1) return "" + CREW_REQ_PER_BAY + "%";
		if (index == 2) return "" + MAX_CREW + "%";
		if (index == 3) return "" + ((Float) dpsize.get(HullSize.FRIGATE)).intValue();
		if (index == 4) return "" + ((Float) dpsize.get(HullSize.DESTROYER)).intValue();
		if (index == 5) return "" + ((Float) dpsize.get(HullSize.CRUISER)).intValue();
		if (index == 6) return "" + ((Float) dpsize.get(HullSize.CAPITAL_SHIP)).intValue();
		return null;
	}
	
}



