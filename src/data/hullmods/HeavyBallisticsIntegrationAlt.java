package data.hullmods;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;


import com.fs.starfarer.api.Global;

import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.loading.WeaponGroupSpec;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import com.fs.starfarer.api.loading.WeaponSpecAPI;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HeavyBallisticsIntegrationAlt extends BaseHullMod {

	public static final float COST_REDUCTION  = 10f;
	public static final float SMOD_ARC_BONUS  = 50f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats) || stats.getVariant().getHullMods().contains("integrationsuite"); // || stats.getVariant().getHullMods().contains("ill_advised")
		if (!sMod) {
			if (stats.getVariant().getHullSpec().getHullId().contains("_hbismod")) {
				//ShipHullSpecAPI hullSpec = Global.getSettings().getHullSpec(stats.getVariant().getHullSpec().getBaseHullId() + "_alt");
				ShipHullSpecAPI hullSpec;
				String BaseHullID = stats.getVariant().getHullSpec().getBaseHullId();
				ShipHullSpecAPI shipHullSpecAPI = Global.getSettings().getHullSpec(BaseHullID);
				List<WeaponGroupSpec> weaponGroups = new ArrayList<>(stats.getVariant().getWeaponGroups());
				if (BaseHullID.equals("conquest")) {
					hullSpec = Global.getSettings().getHullSpec("conquest");
					stats.getVariant().setHullSpecAPI(hullSpec);

					stats.getVariant().getWeaponGroups().clear();
					stats.getVariant().getWeaponGroups().addAll(weaponGroups);
				} else if (BaseHullID.equals("onslaught")) {
					if (stats.getVariant().getHullSpec().getHullId().startsWith("onslaught_xiv")) {
						hullSpec = Global.getSettings().getHullSpec("onslaught_xiv");
						stats.getVariant().setHullSpecAPI(hullSpec);

						stats.getVariant().getWeaponGroups().clear();
						stats.getVariant().getWeaponGroups().addAll(weaponGroups);
					} else {
						hullSpec = Global.getSettings().getHullSpec("onslaught");
						stats.getVariant().setHullSpecAPI(hullSpec);

						stats.getVariant().getWeaponGroups().clear();
						stats.getVariant().getWeaponGroups().addAll(weaponGroups);
					}
				} else if (BaseHullID.equals("invictus")) {
					hullSpec = Global.getSettings().getHullSpec("invictus");
					stats.getVariant().setHullSpecAPI(hullSpec);

					stats.getVariant().getWeaponGroups().clear();
					stats.getVariant().getWeaponGroups().addAll(weaponGroups);
				} else {
					removeHBIRefund(stats,0);
				}
            }


			stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(id, -COST_REDUCTION);
			
		}
		if (sMod) {

            if (!stats.getVariant().getHullSpec().getHullId().contains("_hbismod")) {
				//ShipHullSpecAPI hullSpec = Global.getSettings().getHullSpec(stats.getVariant().getHullSpec().getBaseHullId() + "_alt");
				ShipHullSpecAPI hullSpec;
				String BaseHullID = stats.getVariant().getHullSpec().getBaseHullId();
				ShipHullSpecAPI shipHullSpecAPI = Global.getSettings().getHullSpec(BaseHullID);
				List<WeaponGroupSpec> weaponGroups = new ArrayList<>(stats.getVariant().getWeaponGroups());
				if (BaseHullID.equals("conquest")) {
					hullSpec = Global.getSettings().getHullSpec("conquest" + "_hbismod");
					stats.getVariant().setHullSpecAPI(hullSpec);

					stats.getVariant().getWeaponGroups().clear();
					stats.getVariant().getWeaponGroups().addAll(weaponGroups);
				} else if (BaseHullID.equals("onslaught")) {
					if (stats.getVariant().getHullSpec().getHullId().startsWith("onslaught_xiv")) {
						hullSpec = Global.getSettings().getHullSpec("onslaught_xiv" + "_hbismod");
						stats.getVariant().setHullSpecAPI(hullSpec);

						stats.getVariant().getWeaponGroups().clear();
						stats.getVariant().getWeaponGroups().addAll(weaponGroups);
					} else {
						hullSpec = Global.getSettings().getHullSpec("onslaught" + "_hbismod");
						stats.getVariant().setHullSpecAPI(hullSpec);

						stats.getVariant().getWeaponGroups().clear();
						stats.getVariant().getWeaponGroups().addAll(weaponGroups);
					}
				} else if (BaseHullID.equals("invictus")) {
					hullSpec = Global.getSettings().getHullSpec("invictus" + "_hbismod");
					stats.getVariant().setHullSpecAPI(hullSpec);

					stats.getVariant().getWeaponGroups().clear();
					stats.getVariant().getWeaponGroups().addAll(weaponGroups);
				} else {
					int smodOPrefund = 0;

/* 					for (String weaponSlot : stats.getVariant().getFittedWeaponSlots()) {
						WeaponSpecAPI weapon = stats.getVariant().getWeaponSpec(weaponSlot);
						if(stats.getVariant().getSlot(weaponSlot).getWeaponType() == WeaponAPI.WeaponType.BALLISTIC && weapon.getSize() == WeaponAPI.WeaponSize.LARGE) {
							smodOPrefund +=1;
						}
					} */

					for (WeaponSlotAPI modSlot : stats.getVariant().getHullSpec().getAllWeaponSlotsCopy()){
						boolean statMod = modSlot.getSlotSize().equals(WeaponAPI.WeaponSize.LARGE) && modSlot.getWeaponType().equals(WeaponAPI.WeaponType.BALLISTIC);
						if (statMod){
							smodOPrefund +=1;
						}
					}

					if(smodOPrefund >0){

						if(smodOPrefund>14) smodOPrefund = 14;
						stats.getVariant().addMod("hbi_refund_" + smodOPrefund);
						removeHBIRefund(stats, smodOPrefund);
					}
				}
            }
		}
		
		
		
	}

	public void removeHBIRefund(MutableShipStatsAPI stats, int notRemove) {
		for (int i = 1; i < 15; i++) {
			if(i != notRemove)stats.getVariant().getHullMods().remove("hbi_refund_" + i);
		}

	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) COST_REDUCTION + "";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) SMOD_ARC_BONUS + "%";
		return null;
	}

	public void addSModEffectSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec, boolean isForBuildInList) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();;

		tooltip.addPara("Negates the ordnance point cost reduction of all large ballistic weapons.", opad, h);
		if (isForModSpec || ship == null || ship.getMutableStats() == null) return; //prevents crash from viewing it as modspec/progressive smod

		
		String BaseHullID = ship.getVariant().getHullSpec().getBaseHullId();

		String ShipName = ship.getVariant().getHullSpec().getHullName();
		
		
		if (BaseHullID.equals("invictus") || BaseHullID.equals("onslaught") || BaseHullID.equals("onslaught_xiv") || BaseHullID.equals("conquest") || BaseHullID.contains("_hbismod")) {
			tooltip.addPara("Change all large ballistic weapon slots into hybrid slots and increases weapon arc by %s.", opad, h, "" + (int) Math.round(SMOD_ARC_BONUS) + "%");
			
			if (BaseHullID.equals("invictus") || BaseHullID.equals("invictus_hbismod")) {
				tooltip.addPara("For the %s, beam weapons interfere with the LIDAR Array. Beam weapons are disabled while the system is active.", opad, h, ShipName);
			}
			
			tooltip.addPara("Select a different ship and come back to see the effect.", opad, h);
		} else {
			tooltip.addPara("For the %s, refund 10 OP per lage ballistic slot.", opad, h, ShipName);
		}
	}

	@Override
	public boolean affectsOPCosts() {

		return true;
	}

}








