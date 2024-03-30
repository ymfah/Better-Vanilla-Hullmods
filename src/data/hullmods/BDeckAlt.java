package data.hullmods;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;

public class BDeckAlt extends BaseHullMod {

	public static float REPLACEMENT_RATE_THRESHOLD = 0.4f;
	public static float REPLACEMENT_RATE_RESET = 1f;
	public static float CR_COST_MULT = 0f;
	
	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite") || stats.getVariant().getHullMods().contains("ill_advised");
		if(sMod){
			stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).modifyMult(id, 2f);
		}

	}
	
	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		super.advanceInCombat(ship, amount);
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.addListener(new BDeckListener(ship));
	}
	
	public static Object STATUS_KEY = new Object();
	
	public class BDeckListener implements AdvanceableListener {
		protected ShipAPI ship;
		protected boolean fired = false;
		public BDeckListener(ShipAPI ship) {
			this.ship = ship;
		}

		public void advance(float amount) {


			boolean sMod = isSMod(ship.getMutableStats()) || ship.getMutableStats().getVariant().getHullMods().contains("integrationsuite") || ship.getMutableStats().getVariant().getHullMods().contains("ill_advised");
			if(!sMod){

				float cr = ship.getCurrentCR();
				float crCost = ship.getDeployCost() * CR_COST_MULT;

				if (!fired && cr >= crCost) {
					if (ship.getSharedFighterReplacementRate() <= REPLACEMENT_RATE_THRESHOLD) {
						fired = true;

						for (FighterLaunchBayAPI bay : ship.getLaunchBaysCopy()) {
							if (bay.getWing() == null) continue;

							float rate = REPLACEMENT_RATE_RESET;
							bay.setCurrRate(rate);

							bay.makeCurrentIntervalFast();
							FighterWingSpecAPI spec = bay.getWing().getSpec();

							int maxTotal = spec.getNumFighters();
							int actualAdd = maxTotal - bay.getWing().getWingMembers().size();
							if (actualAdd > 0) {
								bay.setFastReplacements(bay.getFastReplacements() + actualAdd);
							}

							if (crCost > 0) {
								ship.setCurrentCR(ship.getCurrentCR() - crCost);
							}
						}
					}
				}

				if (Global.getCurrentState() == GameState.COMBAT &&
						Global.getCombatEngine() != null && Global.getCombatEngine().getPlayerShip() == ship) {

					String status = "ON STANDBY";
					boolean penalty = false;
					if (fired) status = "OPERATIONAL";
					if (!fired && cr < crCost) {
						status = "NOT READY";
						penalty = true;
					}
					Global.getCombatEngine().maintainStatusForPlayerShip(STATUS_KEY,
							Global.getSettings().getSpriteName("ui", "icon_tactical_bdeck"),
							"B-DECK", status, penalty);
				}
			} else {
				float minRate = Global.getSettings().getFloat("minFighterReplacementRate");

				for (FighterLaunchBayAPI bay : ship.getLaunchBaysCopy()) {
					if (bay.getWing() == null) continue;

					float rate = Math.max(minRate, bay.getCurrRate());
					bay.setCurrRate(rate);

					//bay.makeCurrentIntervalFast();
					FighterWingSpecAPI spec = bay.getWing().getSpec();

					int maxTotal = spec.getNumFighters() + 1;
					int actualAdd = maxTotal - bay.getWing().getWingMembers().size();
					if (actualAdd > 0) {
						//bay.setFastReplacements(bay.getFastReplacements() + 1);
						bay.setExtraDeployments(actualAdd);
						bay.setExtraDeploymentLimit(maxTotal);
						bay.setExtraDuration(30f);
					}
				}
			}

		}
		
	}

	
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) {
			return (int) Math.round(REPLACEMENT_RATE_THRESHOLD * 100f) + "%";
		}
		if (index == 1) {
			return (int) Math.round(REPLACEMENT_RATE_RESET * 100f) + "%";
		}
		return null;
	}
	
	public boolean isApplicableToShip(ShipAPI ship) {
		int baysModified = (int) ship.getMutableStats().getNumFighterBays().getModifiedValue();
		if (baysModified <= 0) return false; // only count removed bays, not added bays for this
		
		int bays = (int) ship.getMutableStats().getNumFighterBays().getBaseValue();
		return ship != null && bays > 0; 
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		return "Ship does not have standard fighter bays";
	}

	
}









