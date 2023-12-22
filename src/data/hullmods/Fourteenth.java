package data.hullmods;

import java.awt.Color;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import com.fs.starfarer.api.combat.ShipAPI;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;

import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import com.fs.starfarer.api.Global;


public class Fourteenth extends BaseHullMod {

	/* A Ship of the 14th Domain Battlegroup
	 * well-maintained survivor of the original battlegroup which founded the Hegemony
	 * Sterling example of the Domain Navy's traditional "decisive battle" doctrine
	 * focus on superior armour and firepower on ships of the line to destroy the enemy
	 * - slightly better flux handling
	 * - slightly better armour
	 * - slightly worse speed/maneuver
	 * - 
	 */
	
	//private static final float ARMOR_BONUS_MULT = 1.1f;
	private static final float ARMOR_BONUS = 100f;
	private static final float CAPACITY_MULT = 1.05f;
	private static final float DISSIPATION_MULT = 1.05f;
	private static final float HANDLING_MULT = 0.92f;
	private static final float SMOD_ARMOR_BONUS = -100f;
	private static final float SMOD_HANDLING_MULT = 1.16f;
	
	/*private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FIGHTER, 0.0f);
		mag.put(HullSize.FRIGATE, 0.25f);
		mag.put(HullSize.DESTROYER, 0.15f);
		mag.put(HullSize.CRUISER, 0.10f);
		mag.put(HullSize.CAPITAL_SHIP, 0.05f);
	}*/	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		// 5% better flux stats
		stats.getFluxCapacity().modifyMult(id, CAPACITY_MULT);
		stats.getFluxDissipation().modifyMult(id, DISSIPATION_MULT);
		
		// just generally better armour - and structure!
		//stats.getArmorBonus().modifyMult(id, (Float) mag.get(hullSize) + 1.00f); // * ARMOR_BONUS_MULT); 
		//stats.getHullBonus().modifyPercent(id, (Float) mag.get(hullSize) * 0.5f); // some hull. 
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite");
		if (!sMod) {
			stats.getArmorBonus().modifyFlat(id, (Float) ARMOR_BONUS);
		
		
		// 8% worse handling
			stats.getMaxSpeed().modifyMult(id, HANDLING_MULT);
			stats.getAcceleration().modifyMult(id, HANDLING_MULT);
			stats.getDeceleration().modifyMult(id, HANDLING_MULT);
			stats.getMaxTurnRate().modifyMult(id, HANDLING_MULT);
			stats.getTurnAcceleration().modifyMult(id, HANDLING_MULT);
		}
		if (sMod) {
			stats.getArmorBonus().modifyFlat(id, (Float) SMOD_ARMOR_BONUS);
			stats.getMaxSpeed().modifyMult(id, SMOD_HANDLING_MULT);
			stats.getAcceleration().modifyMult(id, SMOD_HANDLING_MULT);
			stats.getDeceleration().modifyMult(id, SMOD_HANDLING_MULT);
			stats.getMaxTurnRate().modifyMult(id, SMOD_HANDLING_MULT);
			stats.getTurnAcceleration().modifyMult(id, SMOD_HANDLING_MULT);
		}
		
		
		String hullId = stats.getVariant().getHullSpec().getHullId();
		
		if(hullId.startsWith("enforcer_xiv")){
			if(!sMod) stats.getVariant().getHullSpec().setShipSystemId("burndrive"); //revert to base forced
			if(sMod) stats.getVariant().getHullSpec().setShipSystemId("ammofeed");
		} else if(hullId.startsWith("dominator_xiv")){
			if(!sMod) stats.getVariant().getHullSpec().setShipSystemId("burndrive");
			if(sMod) stats.getVariant().getHullSpec().setShipSystemId("ammofeed");
			
		} else if(hullId.startsWith("eagle_xiv")){
			if(!sMod) stats.getVariant().getHullSpec().setShipSystemId("maneuveringjets");
			if(sMod) stats.getVariant().getHullSpec().setShipSystemId("microburn");
			
		} else if(hullId.startsWith("falcon_xiv")){
			if(!sMod) stats.getVariant().getHullSpec().setShipSystemId("maneuveringjets");
			if(sMod) stats.getVariant().getHullSpec().setShipSystemId("plasmajets");
			
		} else if(hullId.startsWith("legion_xiv")){
			if(!sMod) stats.getVariant().getHullSpec().setShipSystemId("burndrive");
			if(sMod) stats.getVariant().getHullSpec().setShipSystemId("fastmissileracks");
			
		} else if(hullId.startsWith("onslaught_xiv")){
			if(!sMod) stats.getVariant().getHullSpec().setShipSystemId("burndrive");
			if(sMod) stats.getVariant().getHullSpec().setShipSystemId("displacer_degraded");
			
		} 
		
		
		
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return Misc.getRoundedValue(ARMOR_BONUS);
		if (index == 1) return "" + (int) Math.round((1f - HANDLING_MULT) * 100f) + "%"; // + Strings.X;
		if (index == 2) return "" + (int) Math.round((CAPACITY_MULT - 1f) * 100f) + "%"; 
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return Misc.getRoundedValue(-SMOD_ARMOR_BONUS);
		if (index == 1) return "" + (int) Math.round((SMOD_HANDLING_MULT - 1f) * 100f) + "%"; // + Strings.X;
		return null;
	}
	
	
	public void addSModEffectSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec, boolean isForBuildInList) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();;
		String hullId = ship.getVariant().getHullSpec().getHullId();
		
		tooltip.addPara("The heavier armor plating is completely removed. Negates the armor bonus and reduces it by %s. Increases speed and maneuverability by %s instead of decreasing it.", opad, h, new String[]{Misc.getRoundedValue(-SMOD_ARMOR_BONUS),"" + (int) Math.round((SMOD_HANDLING_MULT - 1f) * 100f) + "%"});
		
		String systemName = "";
		
		if(hullId.startsWith("enforcer_xiv")){
			systemName = "Accelerated Ammo Feeder";
		} else if(hullId.startsWith("dominator_xiv")){
			systemName = "Accelerated Ammo Feeder";
			
		} else if(hullId.startsWith("eagle_xiv")){
			systemName = "Plasma Burn";
			
		} else if(hullId.startsWith("falcon_xiv")){
			systemName = "Plasma Jets";
			
		} else if(hullId.startsWith("legion_xiv")){
			systemName = "Fast Missile Racks";
			
		} else if(hullId.startsWith("onslaught_xiv")){
			systemName = "Degraded Phase Skimmer";
		}
		
		if(systemName != "") {

			tooltip.addPara("For the %s, change the ships system to %s.", opad, h, new String[]{ship.getVariant().getHullSpec().getHullName(), systemName});

		}	
	}
	
	public boolean hasSModEffect() {
//		return Global.getSector().getCharacterData().getMemoryWithoutUpdate().getBoolean("$global.pkCacheDefendersDefeated");

//		return Global.getSector().getMemoryWithoutUpdate().getBoolean("$global.pkCacheDefendersDefeated");
		
//		return Global.getSector().getMemoryWithoutUpdate().getBoolean("$pk_recovered");

		return Global.getSector().getMemoryWithoutUpdate().getBoolean("$pkCacheDefendersDefeated");
    }
	

}
