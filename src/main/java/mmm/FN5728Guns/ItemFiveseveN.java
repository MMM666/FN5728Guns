package mmm.FN5728Guns;

import mmm.lib.guns.ItemGunsBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFiveseveN extends ItemGunsBase {

	public ItemFiveseveN() {
		setMaxDamage(20);
	}

	public boolean checkAmmo(ItemStack pItemStack) {
		return pItemStack.getItem() instanceof ItemSS190;
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

	@Override
	public float getEfficiency(ItemStack pGun, EntityPlayer pPlayer, int pUseCount) {
		return 0.908F;
	}

	@Override
	public float getStability(ItemStack pGun, EntityPlayer pPlayer, int pUseCount) {
		// しゃがみの時は少し早く照準が安定する
		float lf = (pPlayer.isSneaking() ? 30F : 40F);
		lf = lf - Math.min((float)pUseCount, lf);
		lf = lf * lf / 100F + 0.1F;
		FN5728Guns.Debug("%f, %d", lf, pUseCount);
		return lf;
	}

	@Override
	public void onRecoile(ItemStack pGun, World pWorld, EntityPlayer pPlayer, int pUseCount) {
		
	}

}
