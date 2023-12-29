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

public class IllAdvised extends BaseHullMod {

	private static final float RECOIL_MULT = 2f;
	private static final float WEAPON_MALFUNCTION_PROB = 0.05f;
	private static final float ENGINE_MALFUNCTION_PROB = 0.005f;

	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
		stats.getWeaponMalfunctionChance().modifyFlat(id, WEAPON_MALFUNCTION_PROB * effect);
		stats.getEngineMalfunctionChance().modifyFlat(id, ENGINE_MALFUNCTION_PROB);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) (WEAPON_MALFUNCTION_PROB*100f) + "%";
		if (index == 1) return "" + ENGINE_MALFUNCTION_PROB*100f + "%";
		if (index == 2) return "" + (int) (0.5f*100f) + "%";
		return null;
	}


	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.addListener(new IllAdvised.CritMalfunctionListener(ship));
	}


	public static class CritMalfunctionListener implements AdvanceableListener {
		protected ShipAPI ship;
		protected boolean fired = false;
		public CritMalfunctionListener(ShipAPI ship) {
			this.ship = ship;
		}

		public void advance(float amount) {
			if (!fired && ship.getPeakTimeRemaining() == 0) {
				fired = true;
				float effect = ship.getMutableStats().getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
				ship.getMutableStats().getCriticalMalfunctionChance().modifyFlat(ship.getId(), 0.5f * effect);
			}
		}

	}


}
