package data.hullmods;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

public class StealthMinefieldDoom extends BaseHullMod {

	public static final float DOOM_MINE_RANGE  = 1500f;
	public static class IncomingMine {
		Vector2f mineLoc;
		float delay;
		ShipAPI target;
	}
	public static class MinefieldData {
		ShipAPI source;
		IntervalUtil tracker = new IntervalUtil(0.5f, 1.5f);
		List<IncomingMine> incoming = new ArrayList<IncomingMine>();
	}
	
	
	@Override
	public void advanceInCombat(ShipAPI ship, float amount) { 
		super.advanceInCombat(ship, amount);

		//if (true) return;
		
		CombatEngineAPI engine = Global.getCombatEngine();


		String MINEFIELD_YMFAH_DATA_KEY = ship.getId() + "minefield_ymfah_data_key";

		MinefieldData data = (MinefieldData) engine.getCustomData().get(MINEFIELD_YMFAH_DATA_KEY);
		if (data == null) {
			data = new MinefieldData();
			data.source = ship;
			engine.getCustomData().put(MINEFIELD_YMFAH_DATA_KEY, data);
		}
		
		if (data.source != ship) return;
		if (!ship.isAlive()) return;
		
		
		for (IncomingMine inc : new ArrayList<IncomingMine>(data.incoming)) {
			inc.delay -= amount;
			if (inc.delay <= 0) {
				spawnMine(ship, inc.mineLoc, inc.target);
				data.incoming.remove(inc);
			}
		}
		
		data.tracker.advance(amount);
		if (!data.tracker.intervalElapsed()) return;
		
		WeightedRandomPicker<IncomingMine> picker = new WeightedRandomPicker<IncomingMine>();
		float nearestEnemyRange = DOOM_MINE_RANGE;
		ShipAPI nearestEnemy = ship;
		for (ShipAPI enemy : engine.getShips()) {
			if (enemy == ship) continue;
			if (enemy.isHulk()) continue;
			if (enemy.getOwner() == ship.getOwner()) continue;
			if (enemy.isFighter()) continue;
			if (enemy.isDrone()) continue;
			if (enemy.isStation()) continue;
			if (enemy.isStationModule()) continue;
			if (enemy.getTravelDrive() != null && enemy.getTravelDrive().isActive()) continue;
			if (Misc.getDistance(enemy.getLocation(), ship.getLocation()) > nearestEnemyRange) continue;
			if ((float) Math.random() > 0.25f) continue;
			nearestEnemyRange = Misc.getDistance(enemy.getLocation(), ship.getLocation());
			nearestEnemy = enemy;
		}
		if (nearestEnemy != ship){
			Vector2f mineLoc = Misc.getPointAtRadius(nearestEnemy.getLocation(),
					nearestEnemy.getCollisionRadius() + 400f + 200f * (float) Math.random());
			float minOk = 400f + nearestEnemy.getCollisionRadius();
			if (isAreaClear(mineLoc, minOk))
			{
				IncomingMine inc = new IncomingMine();
				inc.delay = (float) Math.random() * 1.5f;
				inc.target = nearestEnemy;
				inc.mineLoc = mineLoc;

				picker.add(inc);

			}
		}

		int numToSpawn = Math.max(1, Math.min(new Random().nextInt(6) + 5, picker.getItems().size()));
		
		for (int i = 0; i < numToSpawn && !picker.isEmpty(); i++) {
			IncomingMine inc = picker.pickAndRemove();
			data.incoming.add(inc);
		}
		
	}


	public void spawnMine(ShipAPI source, Vector2f mineLoc, ShipAPI target) {
		float mineDir = Misc.getAngleInDegrees(mineLoc, target.getLocation());
		CombatEngineAPI engine = Global.getCombatEngine();
		Vector2f currLoc = Misc.getPointAtRadius(mineLoc, 50f + (float) Math.random() * 50f);
		MissileAPI mine = (MissileAPI) engine.spawnProjectile(source, null, 
															  getWeaponId(), 
															  currLoc, 
															  mineDir, null);
		if (source != null) {
			Global.getCombatEngine().applyDamageModifiersToSpawnedProjectileWithNullWeapon(
											source, WeaponType.MISSILE, false, mine.getDamage());
//			float extraDamageMult = source.getMutableStats().getMissileWeaponDamageMult().getModifiedValue();
//			mine.getDamage().setMultiplier(mine.getDamage().getMultiplier() * extraDamageMult);
		}
		
		mine.setFlightTime((float) Math.random());
		mine.fadeOutThenIn(1f);
		
		Global.getSoundPlayer().playSound("mine_spawn", 1f, 1f, mine.getLocation(), mine.getVelocity());
	}

	protected String getWeaponId() {
		return "minelayer2";
	}
	
	public boolean isAreaClear(Vector2f loc, float range) {
		CombatEngineAPI engine = Global.getCombatEngine();
		for (ShipAPI other : engine.getShips()) {
			if (other.isFighter()) continue;
			if (other.isDrone()) continue;
			
			float dist = Misc.getDistance(loc, other.getLocation());
			if (dist < range) {
				return false;
			}
		}
		
		for (CombatEntityAPI other : Global.getCombatEngine().getAsteroids()) {
			float dist = Misc.getDistance(loc, other.getLocation());
			if (dist < other.getCollisionRadius() + 100f) {
				return false;
			}
		}
		
		return true;
	}
	public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
		if (index == 0) return "" + (int) DOOM_MINE_RANGE + "";
		return null;
	}

}




