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

public class ExpandedDeckCrewAlt extends BaseHullMod {

	public static float RATE_DECREASE_MODIFIER = 15f;
	public static float RATE_INCREASE_MODIFIER = 25f;
	
	public static float CREW_PER_DECK = 20f;
	
	public static final float REFIT_TIME_PERCENT = 25f;
	
	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).modifyMult(id, 1f - RATE_DECREASE_MODIFIER / 100f);
		stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).modifyPercent(id, RATE_INCREASE_MODIFIER);
		
		stats.getFighterRefitTimeMult().modifyPercent(id, -REFIT_TIME_PERCENT);
		
		int crew = (int) (stats.getNumFighterBays().getBaseValue() * CREW_PER_DECK);
		stats.getMinCrewMod().modifyFlat(id, crew);

	}
		
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) RATE_DECREASE_MODIFIER + "%";
		if (index == 1) return "" + (int) RATE_INCREASE_MODIFIER + "%";
		if (index == 2) return "" + (int) REFIT_TIME_PERCENT + "%";
		if (index == 3) return "" + (int) CREW_PER_DECK + "";
		return null;
	}
	
	public boolean isApplicableToShip(ShipAPI ship) {
		int baysModified = (int) ship.getMutableStats().getNumFighterBays().getModifiedValue();
		if (baysModified <= 0) return false; // only count removed bays, not added bays for this
		
		int bays = (int) ship.getMutableStats().getNumFighterBays().getBaseValue();
//		if (ship != null && ship.getVariant().getHullSpec().getBuiltInWings().size() >= bays) {
//			return false;
//		}
		return ship != null && bays > 0; 
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		return "Ship does not have standard fighter bays";
	}


	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		super.advanceInCombat(ship, amount);
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		boolean sMod = isSMod(ship.getMutableStats()) || ship.getMutableStats().getVariant().getHullMods().contains("integrationsuite") || ship.getMutableStats().getVariant().getHullMods().contains("ill_advised");
		if(sMod)ship.addListener(new ExpandedDeckCrewListener(ship));
	}

	public static class ExpandedDeckCrewListener implements AdvanceableListener {
		protected ShipAPI ship;
		public ExpandedDeckCrewListener(ShipAPI ship) {
			this.ship = ship;
		}

		public void advance(float amount) {
			float cr = ship.getCurrentCR();

			if (ship.getSharedFighterReplacementRate() < cr/2) {
				for (FighterLaunchBayAPI bay : ship.getLaunchBaysCopy()) {
					if (bay.getWing() == null) continue;

					bay.setCurrRate(cr/2);
				}
			}
		}

	}
}




