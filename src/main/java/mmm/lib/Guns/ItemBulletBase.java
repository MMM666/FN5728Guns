package mmm.lib.Guns;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBulletBase extends Item {

	public ItemBulletBase() {
		setCreativeTab(CreativeTabs.tabCombat);
	}

	/**
	 * 基準発射速度
	 * @param pBullet
	 * @return
	 */
	public float getSpeed(ItemStack pBullet) {
		// 500m/s
		return 25F;
	}

	/**
	 * 集弾性
	 * @param pBullet
	 * @return
	 */
	public float getReaction(ItemStack pBullet) {
		return 1.0F;
	}


	/**
	 * 弾薬に関連付けられたEntityを返す。
	 * @param pGun
	 * @param pBullet
	 * @param pWorld
	 * @param pPlayer
	 * @return
	 */
	public EntityBulletBase getBulletEntity(ItemStack pGun, ItemStack pBullet, World pWorld, EntityPlayer pPlayer) {
		// 標準弾体
		ItemGunsBase lgun = ((ItemGunsBase)pGun.getItem());
		return new EntityBulletBase(pWorld, pPlayer, pGun, pBullet,
				getSpeed(pBullet) * lgun.getEfficiency(pGun),
				getReaction(pBullet) * lgun.getStability(pGun));
	}

	/**
	 * 発射音
	 * @param pWorld
	 * @param pPlayer
	 * @param pBullet
	 */
	public void soundFire(World pWorld, EntityPlayer pPlayer, ItemStack pBullet) {
	}

}
