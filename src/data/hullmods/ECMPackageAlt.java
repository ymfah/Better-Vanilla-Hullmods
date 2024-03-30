package data.hullmods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.BattleObjectives;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import static com.fs.starfarer.api.impl.campaign.skills.ElectronicWarfareScript.BASE_MAXIMUM;
import static com.fs.starfarer.api.impl.campaign.skills.ElectronicWarfareScript.PER_JAMMER;

public class ECMPackageAlt extends BaseHullMod {

	public static float ECM_RANGE_MULT = 1f;
	private CombatEngineAPI engine;

	public static String ECM_RANGE_BONUS_ID = "electronic_warfare_range_bonus";

	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 1f);
		mag.put(HullSize.DESTROYER, 2f);
		mag.put(HullSize.CRUISER, 3f);
		mag.put(HullSize.CAPITAL_SHIP, 4f);
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat(id, (Float) mag.get(hullSize));
	}


	public void advanceInCombat(ShipAPI ship, float amount) {
		engine = Global.getCombatEngine();
		if (engine == null) return;
		if (engine.isPaused()) return;

		boolean sMod = isSMod(ship.getMutableStats()) || ship.getMutableStats().getVariant().getHullMods().contains("integrationsuite") || ship.getMutableStats().getVariant().getHullMods().contains("ill_advised");

		if(sMod){
			int [] player = getTotalAndMaximum(engine.getFleetManager(0));
			int [] enemy = getTotalAndMaximum(engine.getFleetManager(1));
			float pTotal = player[0];
			float eTotal = enemy[0];
			float ecmeangebonus = (int) Math.round(Math.max(0, (pTotal - eTotal)*ECM_RANGE_MULT));

			ship.getMutableStats().getBallisticWeaponRangeBonus().modifyMult(ECM_RANGE_BONUS_ID, 1f + ecmeangebonus/100f);
			ship.getMutableStats().getEnergyWeaponRangeBonus().modifyMult(ECM_RANGE_BONUS_ID, 1f + ecmeangebonus/100f);
			ship.getMutableStats().getMissileWeaponRangeBonus().modifyMult(ECM_RANGE_BONUS_ID, 1f + ecmeangebonus/100f);
			ship.getMutableStats().getSystemRangeBonus().modifyMult(ECM_RANGE_BONUS_ID, 1f + ecmeangebonus/100f);
			String icon = Global.getSettings().getSpriteName("ui", "icon_tactical_electronic_warfare");
			if (ship == Global.getCombatEngine().getPlayerShip()) Global.getCombatEngine().maintainStatusForPlayerShip("ECMPackageBonus", icon, "ECM Package", (int)ecmeangebonus + "% increased range based on ECM difference", false);
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue() + "%";
		if (index == 1) return "" + ((Float) mag.get(HullSize.DESTROYER)).intValue() + "%";
		if (index == 2) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue() + "%";
		if (index == 3) return "" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue() + "%";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		return null;
	}


	//yoinked from ElectronicWarfareScript.java
	private int [] getTotalAndMaximum(CombatFleetManagerAPI manager) {

		float max = 0f;
		for (PersonAPI commander : manager.getAllFleetCommanders()) {
			max = Math.max(max, BASE_MAXIMUM + commander.getStats().getDynamic().getValue(Stats.ELECTRONIC_WARFARE_MAX, 0f));
		}


		float total = 0f;
		List<DeployedFleetMemberAPI> deployed = manager.getDeployedCopyDFM();
		float canCounter = 0f;
		for (DeployedFleetMemberAPI member : deployed) {
			if (member.isFighterWing()) continue;
			if (member.isStationModule()) continue;
			float curr = member.getShip().getMutableStats().getDynamic().getValue(Stats.ELECTRONIC_WARFARE_FLAT, 0f);
			total += curr;

			canCounter += member.getShip().getMutableStats().getDynamic().getValue(Stats.SHIP_BELONGS_TO_FLEET_THAT_CAN_COUNTER_EW, 0f);
		}

		for (BattleObjectiveAPI obj : engine.getObjectives()) {
			if (obj.getOwner() == manager.getOwner() && BattleObjectives.SENSOR_JAMMER.equals(obj.getType())) {
				total += PER_JAMMER;
			}
		}

		int counter = 0;
		if (canCounter > 0) counter = 1;

		return new int [] {(int) total, (int) max, counter};
	}

}




