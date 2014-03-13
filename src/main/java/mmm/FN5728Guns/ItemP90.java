package mmm.FN5728Guns;

import mmm.lib.guns.ItemGunsBase;

public class ItemP90 extends ItemGunsBase {

	public ItemP90() {
		setMaxDamage(50);
		
		volume = 0.5F;
		soundEmpty = "mmm:FN5728.P90.empty";
		soundRelease = "mmm:FN5728.P90.release";
		soundReload = "mmm:FN5728.P90.reload";
		
		// 3.0 sec
		reloadTime = 60;
		// 無制限
		burstCount = 72000;
		// 秒間１０発
		cycleCount = 2;
		efficiency = 1.0F;
		stability = 0.01F;
		stabilityPitch = 2.0F;
		stabilityPitchOffset = 1.0F;
		stabilityYaw = 2.0F;
		stabilityYawOffset = -1.0F;
		accuracy = 1.0F;
		
		iconNames = new String[] {"mmm:P90", "mmm:P90_Empty" ,"mmm:P90_Release"};
		bullets = new String[] {"FN5728Guns:SS190"};
	}

}
