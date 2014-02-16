package mmm.FN5728Guns;

import mmm.lib.Guns.ItemGunsBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFiveseveN extends ItemGunsBase {

	public ItemFiveseveN() {
		setMaxDamage(20);
	}

	@Override
	public void reloadMagazin(ItemStack pGun, World pWorld, EntityPlayer pPlayer) {
		while (getDamage(pGun) > 0) {
			loadBullet(pGun, new ItemStack(FN5728Guns.fn_SS190));
		}
		soundReload(pWorld, pPlayer, pGun);
	}

	public void soundEmpty(World pWorld, EntityPlayer pPlayer, ItemStack pGun) {
		pWorld.playSoundAtEntity(pPlayer, "mmm:FN5728.57.empty",
				0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
	}

	public void soundRelease(World pWorld, EntityPlayer pPlayer, ItemStack pGun) {
		pWorld.playSoundAtEntity(pPlayer, "mmm:FN5728.57.release",
				0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
	}

	public void soundReload(World pWorld, EntityPlayer pPlayer, ItemStack pGun) {
		pWorld.playSoundAtEntity(pPlayer, "mmm:FN5728.57.reload",
				0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
	}

	public int getReloadTime(ItemStack pGun) {
		// 2.0 sec
		return 40;
	}

	public int getHoldTime(ItemStack pGun) {
		return 10;
	}


}
