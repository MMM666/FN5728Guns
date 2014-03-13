package mmm.FN5728Guns;

import mmm.lib.guns.ItemGunsBase;

public class ItemFiveseveN extends ItemGunsBase {

	public ItemFiveseveN() {
		setMaxDamage(20);
		
		volume = 0.5F;
		soundEmpty = "mmm:FN5728.57.empty";
		soundRelease = "mmm:FN5728.57.release";
		soundReload = "mmm:FN5728.57.reload";
		
		// 2.0 sec
		reloadTime = 40;
		burstCount = 0;
		cycleCount = 2;
		efficiency = 0.908F;
		stability = 0.01F;
		stabilityPitch = 5.0F;
		stabilityPitchOffset = 5.0F;
		stabilityYaw = 6.0F;
		stabilityYawOffset = -3.0F;
		accuracy = 1.0F;
		
		iconNames = new String[] {"mmm:FiveseveN", "mmm:FiveseveN_Empty" ,"mmm:FiveseveN_Release"};
		bullets = new String[] {"FN5728Guns:SS190"};
	}

}
