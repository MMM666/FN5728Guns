package net.minecraft.src;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;

public class IFN_ItemP90 extends IFN_ItemFN5728 {

	public IFN_ItemP90(int i) {
		super(i);
		setMaxDamage(50);
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
		boolean lflag = cycleBolt(itemstack);
		entity = checkMaid(entity);
		
		if (entity != null && entity instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer)entity;
			if (entityplayer.isUsingItem() && itemstack == entityplayer.getCurrentEquippedItem()) {
//			if (itemstack != null && entityplayer.getItemInUse() == itemstack) {
				if (lflag && !isReload(itemstack)) {
					// 発射
					if (itemstack.getItemDamage() < getMaxDamage()) {
						// 発射
//						if (fireBullet(itemstack, world, entityplayer, 0F, itemRand.nextFloat() * -0.3F, 13.8F)) {
						if (fireBullet(itemstack, world, entityplayer, 0F, itemRand.nextFloat() * -0.3F, 35.8F)) {
							itemstack.damageItem(1, entityplayer);
						}
						resetBolt(itemstack);
					} else {
						// 弾切れ
						if (canReload(itemstack, entityplayer)) {
							entityplayer.stopUsingItem();
						}
					}
				}
				MMM_Helper.updateCheckinghSlot(entityplayer, itemstack);
			}
		}
		super.onUpdate(itemstack, world, entity, i, flag);
	}

	@Override
	public byte getCycleCount(ItemStack pItemstack) {
		return 1;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		itemstack = super.onItemRightClick(itemstack, world, entityplayer);
		if (!isReload(itemstack) && itemstack.getItemDamage() >= getMaxDamage()) {
			// 弾切れ
			world.playSoundAtEntity(entityplayer, "FN5728.emptyP90s", 1.0F, 1.0F);
		}
		return itemstack;
	}

	@Override
	public int reloadTime() {
		// 3.0Sec
		return 60;
	}

	@Override
	public void releaseMagazin(ItemStack itemstack, World world, Entity entity) {
		world.playSoundAtEntity(entity, "FN5728.releaseP90s", 1.0F, 1.0F);
		super.releaseMagazin(itemstack, world, entity);
	}

	@Override
	public void reloadMagazin(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		world.playSoundAtEntity(entityplayer, "FN5728.reloadP90s", 1.0F, 1.0F);
		super.reloadMagazin(itemstack, world, entityplayer);
	}

	@Override
	public boolean isWeaponFullAuto(ItemStack itemstack) {
		return true;
	}

}
