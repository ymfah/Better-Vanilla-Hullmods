package data.hullmods;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ConvertedHangarAlt extends BaseHullMod {

	public static float FIGHTER_OP_PER_DP = 10;
	public static int MIN_DP = 1;
	public static float REPLACEMENT_TIME_MULT = 1.5f;
	public static int CREW_REQ = 20;
	
	//public static float EXTRA_REARM_TIME = 5f;
	public static float REARM_TIME_FRACTION = 0.4f;
	
	public static float SMOD_CRUISER = 10f;
	public static float SMOD_CAPITAL = 25f;
	
	public static float CR_THRESHOLD_UNINSTALLABLE = 70;
	
	
	
	//public static final int CARGO_REQ = 80;
//	public static final int ALL_FIGHTER_COST_PERCENT = 50;
//	public static final int BOMBER_COST_PERCENT = 100;
	
//	private static Map mag = new HashMap();
//	static {
//		mag.put(HullSize.FRIGATE, 0f);
//		mag.put(HullSize.DESTROYER, 75f);
//		mag.put(HullSize.CRUISER, 50f);
//		mag.put(HullSize.CAPITAL_SHIP, 25f);
//	}
	
	public static int computeDPModifier(float fighterOPCost) {
		int mod = (int) Math.ceil(fighterOPCost / FIGHTER_OP_PER_DP);
		if (mod < MIN_DP) mod = MIN_DP;
		return mod;
	}
	
	public static float getFighterOPCost(MutableShipStatsAPI stats) {
		float cost = 0;
		for (String wingId : getFighterWings(stats)) {
			FighterWingSpecAPI spec = Global.getSettings().getFighterWingSpec(wingId);
			cost += spec.getOpCost(stats);
		}
		return cost;
	}
	
	public static List<String> getFighterWings(MutableShipStatsAPI stats) {
		if (stats.getVariant() != null) {
			return stats.getVariant().getFittedWings();
		}
		return new ArrayList<String>();
//		if (stats.getEntity() instanceof ShipAPI) {
//			ShipAPI ship = (ShipAPI) stats.getEntity();
//		} else {
//			FleetMemberAPI member = stats.getFleetMember();
//		}
	}
	
	public float computeCRMult(float suppliesPerDep, float dpMod) {
		return 1f + dpMod / suppliesPerDep;
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//stats.getFighterRefitTimeMult().modifyPercent(id, ((Float) mag.get(hullSize)));
		float numBays = 1f;
		int bays = (int) stats.getNumFighterBays().getBaseValue();
		
		numBays += stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_MOD).computeEffective(0f);
		stats.getNumFighterBays().modifyFlat(id, numBays);



		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite") || stats.getVariant().getHullMods().contains("ill_advised");
		if (sMod) {
			float bonus = 0f;
			if (hullSize == HullSize.CRUISER) bonus = SMOD_CRUISER;
			else if (hullSize == HullSize.CAPITAL_SHIP) bonus = SMOD_CAPITAL;
			if (bonus != 0) {
				stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).modifyPercent(id, bonus);
			}
		}
		
		boolean crewIncrease = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_CREW_INCREASE).computeEffective(0f) <= 0;
		boolean rearmIncrease = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_REARM_INCREASE).computeEffective(0f) <= 0;
		boolean dpIncrease = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_DP_INCREASE).computeEffective(0f) <= 0;
		boolean refitPenalty = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_REFIT_PENALTY).computeEffective(0f) <= 0;
		
		if (refitPenalty) {
			stats.getFighterRefitTimeMult().modifyMult(id, (1+(REPLACEMENT_TIME_MULT-1)/(bays+1)));
			stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).modifyMult(id, 1f / (1+(REPLACEMENT_TIME_MULT-1)/(bays+1)));
			stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).modifyMult(id, 1f / (1+(REPLACEMENT_TIME_MULT-1)/(bays+1)));
		}
		
		if (rearmIncrease) {
			stats.getDynamic().getMod(Stats.FIGHTER_REARM_TIME_EXTRA_FRACTION_OF_BASE_REFIT_TIME_MOD).modifyFlat(id, REARM_TIME_FRACTION/(bays+1));
		}
		
		if (dpIncrease) {
			float dpMod = computeDPModifier(getFighterOPCost(stats));
			if (dpMod > 0) {
				
				if(hullSize != HullSize.CRUISER && hullSize != HullSize.CAPITAL_SHIP && bays == 0) stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, dpMod);
				
				if (stats.getFleetMember() != null) {
					float perDep = stats.getFleetMember().getHullSpec().getSuppliesToRecover();
					float mult = computeCRMult(perDep, dpMod);
					stats.getCRPerDeploymentPercent().modifyMult(id, mult);
				}
				
				stats.getSuppliesToRecover().modifyFlat(id, dpMod);
			}
		}
		
		if (crewIncrease) {
			stats.getMinCrewMod().modifyFlat(id, CREW_REQ);
		}
		
		
//		boolean costIncrease = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_COST_INCREASE).computeEffective(0f) <= 0;
//		//costIncrease = false;
//		if (costIncrease) {
//			stats.getMinCrewMod().modifyFlat(id, CREW_REQ);
//			//stats.getDynamic().getMod(Stats.ALL_FIGHTER_COST_MOD).modifyPercent(id, ALL_FIGHTER_COST_PERCENT);
//			stats.getDynamic().getMod(Stats.BOMBER_COST_MOD).modifyPercent(id, BOMBER_COST_PERCENT);
//			stats.getDynamic().getMod(Stats.FIGHTER_COST_MOD).modifyPercent(id, ALL_FIGHTER_COST_PERCENT);
//			stats.getDynamic().getMod(Stats.INTERCEPTOR_COST_MOD).modifyPercent(id, ALL_FIGHTER_COST_PERCENT);
//			stats.getDynamic().getMod(Stats.SUPPORT_COST_MOD).modifyPercent(id, ALL_FIGHTER_COST_PERCENT);
//		}
		//stats.getCargoMod().modifyFlat(id, -CARGO_REQ);
	}
	
	public boolean isApplicableToShip(ShipAPI ship) {
		//if (ship.getMutableStats().getCargoMod().computeEffective(ship.getHullSpec().getCargo()) < CARGO_REQ) return false;
		if (ship != null && ship.getHullSpec().getCRToDeploy() > CR_THRESHOLD_UNINSTALLABLE) {
			return false;
		}
		return ship != null;
								//ship.getHullSpec().getShieldType() != ShieldType.PHASE;		
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		//if (ship != null && ship.getNumFighterBays() > 0) return "Ship has fighter bays";
		return "Ship's combat readiness lost per deployment is too high";
	}

	
	@Override
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, final ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 3f;
		float opad = 10f;
		Color h = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();


		
		tooltip.addPara("Converts the ship's standard shuttle hangar to house a fighter bay. "
				+ "The improvised flight deck, its crew, and the related machinery all function "
				+ "at a pace below that of a dedicated carrier.", opad);

		if (isForModSpec || ship == null || ship.getMutableStats() == null) return;

		int bays = 0;
		bays = (int) ship.getMutableStats().getNumFighterBays().getBaseValue();


		tooltip.addPara("Increases fighter refit time by %s, "
				+ "and the fighter replacement rate both decays and recovers %s more slowly. "
				+ "In addition, bombers returning to rearm (or fighters returning for repairs) "
				+ "take %s of their base refit time to relaunch, "
				+ "where normally it takes under a second. "
				+ "", opad, h,
				"" + Misc.getRoundedValueMaxOneAfterDecimal((1+(REPLACEMENT_TIME_MULT-1)/(bays+1))) + Strings.X,
				"" + Misc.getRoundedValueMaxOneAfterDecimal((1+(REPLACEMENT_TIME_MULT-1)/(bays+1))) + Strings.X,
				"" + (int) Math.round((REARM_TIME_FRACTION/(bays+1)) * 100f) + "%");
		
		
		tooltip.addPara("Increases the minimum crew by %s to account for pilots and flight crews. "
				+ "Increases the ship's deployment points and supply cost to recover "
				+ "from deployment by %s for every %s ordnance points spent on "
				+ "fighters, or by at least %s point. This comes with a proportional increase "
				+ "in combat readiness lost per deployment.", opad, h,
				"" + (int) CREW_REQ,
				"1",
				"" + (int) + FIGHTER_OP_PER_DP,
				"" + (int) + MIN_DP);
		

		
		MutableShipStatsAPI stats = ship.getMutableStats();
		boolean crewIncrease = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_CREW_INCREASE).computeEffective(0f) <= 0;
		boolean rearmIncrease = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_REARM_INCREASE).computeEffective(0f) <= 0;
		boolean dpIncrease = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_DP_INCREASE).computeEffective(0f) <= 0;
		boolean refitPenalty = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_REFIT_PENALTY).computeEffective(0f) <= 0;
		
		int dpMod = computeDPModifier(getFighterOPCost(stats));
		

		
		if(hullSize == HullSize.CRUISER || hullSize == HullSize.CAPITAL_SHIP || bays > 0) dpIncrease = false;
		if (dpMod > 0) {
			//tooltip.addSectionHeading("Fighter wings", Alignment.MID, opad);
			//tooltip.addPara("%s points for the currently installed fighter wing.", opad, h, "+" + dpMod);
			if (dpIncrease) {
//				float perDep = stats.getFleetMember().getHullSpec().getCRToDeploy();
//				1f + dpMod / perDep);
				tooltip.addPara("Deployment cost: %s", opad, h, "+" + dpMod);
			}

			float numW = 160f;
			float sizeW = width - numW - 10f;
			
			if (!getFighterWings(stats).isEmpty() && rearmIncrease) {
				tooltip.beginTable(Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
						   20f, true, true, 
						   new Object [] {"Wing", sizeW, "Seconds to relaunch", numW});
				
				for (String wingId : getFighterWings(stats)) {
					FighterWingSpecAPI spec = Global.getSettings().getFighterWingSpec(wingId);
					float refitPortion = spec.getRefitTime() * 
							ship.getMutableStats().getDynamic().getValue(Stats.FIGHTER_REARM_TIME_EXTRA_FRACTION_OF_BASE_REFIT_TIME_MOD, 0f);
	
					Color c = Misc.getTextColor();
					//c = Misc.getHighlightColor();
					tooltip.addRow(Alignment.MID, c, spec.getWingName(),
								   Alignment.MID, h, Misc.getRoundedValueOneAfterDecimalIfNotWhole(refitPortion)
								   );
				}
				tooltip.addTable("", 0, opad);
			}
			
		}
		
//		boolean crewIncrease = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_CREW_INCREASE).computeEffective(0f) <= 0;
//		boolean rearmIncrease = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_REARM_INCREASE).computeEffective(0f) <= 0;
//		boolean dpIncrease = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_DP_INCREASE).computeEffective(0f) <= 0;
//		boolean refitPenalty = stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_REFIT_PENALTY).computeEffective(0f) <= 0;
		
		List<String> negated = new ArrayList<String>();
		if (!refitPenalty) negated.add("refit time and rate recovery modifiers");
		if (!rearmIncrease) negated.add("relaunch delay");
		if (!crewIncrease) negated.add("increased crew requirement");
		if (!dpIncrease) negated.add("deployment cost increase");
		
		if (!negated.isEmpty()) {
			Color c = Misc.getPositiveHighlightColor();
			String isOrAre = "is";
			if (negated.size() > 1) isOrAre = "are";
			if (negated.size() >= 4) isOrAre += " all";
			tooltip.addPara("The " + Misc.getAndJoined(negated) + " " + isOrAre + " negated on this ship.", c, opad);
		}
		
		//tooltip.setBgAlpha(0.9f);
		

	}
	
	@Override
	public String getSModDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
		if (index == 0) return "" + (int) SMOD_CRUISER + "%";
		if (index == 1) return "" + (int) SMOD_CAPITAL + "%";
		return null;
	}
	
}



