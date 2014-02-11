package mmm.lib.Guns;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBulletBase extends Item {

	public ItemBulletBase() {
		// TODO Auto-generated constructor stub
		setCreativeTab(CreativeTabs.tabCombat);
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
		return new EntityBulletBase(pWorld, pPlayer);
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
