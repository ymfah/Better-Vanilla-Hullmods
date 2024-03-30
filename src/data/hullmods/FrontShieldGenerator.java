package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.util.IntervalUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class FrontShieldGenerator extends BaseHullMod {

	public static float FLUXRESERVE = 0.25f;
	public static float SHIELD_ARC = 120f;

	public static float ARC_DEBUFF = 0.75f;

	public static float SHIELD_EFF = 1.2f;
	public static float EFF_BUFF = 0.5f;
	public boolean fired = false;

	public ShipAPI drone;



	public Color shieldColor;


	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		stats.getFluxCapacity().modifyFlat("shielddrone",-stats.getFluxCapacity().getBaseValue()*FLUXRESERVE);
		stats.getFluxDissipation().modifyFlat("shielddrone",-stats.getFluxDissipation().getBaseValue()*FLUXRESERVE);
		
	}

	public void advanceInCombat(ShipAPI ship, float amount) {
		CombatEngineAPI engine = Global.getCombatEngine();
		if (engine == null) return;
		if (engine.isPaused()) return;
		String ShieldDroneDataKey = ship.getId() + "shielddrone_ymfah_data_key";

		ShipAPI drone = (ShipAPI) engine.getCustomData().get(ShieldDroneDataKey); //TODO: change to ship.setcustomdata

		//Create shield drone
		if(drone == null){
			engine.getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(true);
			drone = engine.getFleetManager(ship.getOwner()).spawnShipOrWing("ymfah_shielddrone_base",ship.getLocation(), ship.getFacing());
			engine.getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(false);
			engine.getCustomData().put(ShieldDroneDataKey, drone);
			drone.setCollisionClass(CollisionClass.FIGHTER);
			drone.getShield().setRadius(ship.getShieldRadiusEvenIfNoShield()+25f);
			drone.setCollisionRadius(ship.getCollisionRadius()+25f);
			drone.getMutableStats().getFluxCapacity().modifyFlat("shielddrone",ship.getMutableStats().getFluxCapacity().getBaseValue()*FLUXRESERVE*2);
			drone.getMutableStats().getFluxDissipation().modifyFlat("shielddrone",ship.getMutableStats().getFluxDissipation().getBaseValue()*FLUXRESERVE*2);
			shieldColor = drone.getShield().getInnerColor();
			drone.getShield().setRingColor(shieldColor.brighter());
			drone.getShield().setInnerColor(shieldColor.brighter());
			fired = true;
		}

		//Configure shield properties everyframe
		drone.getShield().setArc(SHIELD_ARC*(1-(drone.getFluxLevel()*ARC_DEBUFF)));
		drone.getMutableStats().getShieldDamageTakenMult().modifyMult("droneshield",1-(drone.getFluxLevel()*EFF_BUFF));
		drone.getLocation().set(ship.getLocation().x, ship.getLocation().y);
		drone.setFacing(ship.getFacing());
		//exceptions
		if (!ship.isAlive()) engine.removeEntity(drone);
		if (drone.getShield().isOff()) drone.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, null, 0);
		if(ship.isPhased())drone.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, null, 0);
		if(ship.getFluxTracker().isVenting() && drone.getFluxTracker().getFluxLevel() > 0.5f)drone.giveCommand(ShipCommand.VENT_FLUX, null, 0);
	}


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) {
			return "" + (int) (FLUXRESERVE * 100) + "%";
		}
		if (index == 1) {
			return "" + SHIELD_EFF;
		}
		if (index == 2) {
			return "" + (int) SHIELD_ARC;
		}
		if (index == 3) {
			return "" + (SHIELD_EFF * (1-EFF_BUFF));
		}
		if (index == 4) {
			return "" + (int) (SHIELD_ARC * (1-ARC_DEBUFF));
		}
		return null;
	}
}
