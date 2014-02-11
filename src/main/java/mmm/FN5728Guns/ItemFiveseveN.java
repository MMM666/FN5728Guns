package mmm.FN5728Guns;

import mmm.lib.Guns.ItemGunsBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemFiveseveN extends ItemGunsBase {

	public ItemFiveseveN() {
		setMaxDamage(20);
	}

	@Override
	public void reload(ItemStack pGun, EntityPlayer pPlayer) {
		while (getDamage(pGun) > 0) {
			loadBullet(pGun, new ItemStack(FN5728Guns.fn_SS190));
		}
	}

}
