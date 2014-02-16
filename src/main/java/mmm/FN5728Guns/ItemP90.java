package mmm.FN5728Guns;

import mmm.lib.Guns.ItemGunsBurstBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemP90 extends ItemGunsBurstBase {

	public ItemP90() {
		setMaxDamage(50);
	}

	@Override
	public void reloadMagazin(ItemStack pGun, World pWorld, EntityPlayer pPlayer) {
		while (getDamage(pGun) > 0) {
			loadBullet(pGun, new ItemStack(FN5728Guns.fn_SS190));
		}
		soundReload(pWorld, pPlayer, pGun);
	}

	public void soundEmpty(World pWorld, EntityPlayer pPlayer, ItemStack pGun) {
		pWorld.playSoundAtEntity(pPlayer, "mmm:FN5728.P90.empty",
				0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
	}

	public void soundRelease(World pWorld, EntityPlayer pPlayer, ItemStack pGun) {
		pWorld.playSoundAtEntity(pPlayer, "mmm:FN5728.P90.release",
				0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
	}

	public void soundReload(World pWorld, EntityPlayer pPlayer, ItemStack pGun) {
		pWorld.playSoundAtEntity(pPlayer, "mmm:FN5728.P90.reload",
				0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
	}

	public int getReloadTime(ItemStack pGun) {
		// 3.0 sec
		return 60;
	}

	public int getHoldTime(ItemStack pGun) {
		return 10;
	}

	public int getBurstCount(ItemStack pGun) {
		// 無制限
		return 72000;
	}

	public short getCycleCount(ItemStack pGun) {
		// 秒間１０発
		return 2;
	}

	@Override
	public float getStability(ItemStack pGun) {
		return 0.5F;
	}

}
