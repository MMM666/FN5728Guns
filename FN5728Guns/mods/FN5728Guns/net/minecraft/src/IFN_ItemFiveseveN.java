package net.minecraft.src;


public class IFN_ItemFiveseveN extends IFN_ItemFN5728 {

	public IFN_ItemFiveseveN(int i) {
		super(i);
		setMaxDamage(20);
	}

	@Override
    public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i) {
		if (!isReload(itemstack)) {
			// ÉäÉçÅ[ÉhíÜÇ≈ÇÕÇ»Ç¢
			if (itemstack.getItemDamage() < getMaxDamage()) {
				int j = getMaxItemUseDuration(itemstack) - i;
				float f = (float)j / 20F;
				f = (f * f + f * 2.0F) / 3F;
				if (f > 1.0F)
				{
					f = 1.0F;
				}
				if (fireBullet(itemstack, world, entityplayer, f, itemRand.nextFloat() * -3F, 30.0F)) {
					itemstack.damageItem(1, entityplayer);
				}
			} else {
				// íeêÿÇÍ
				world.playSoundAtEntity(entityplayer, "FN5728.emptyFive-seveN", 1.0F, 1.0F);
			}
		}
		super.onPlayerStoppedUsing(itemstack, world, entityplayer, i);
    }
	
	@Override
	public int reloadTime() {
		// 2Sec
		return 40;
	}
	
	@Override
	public void releaseMagazin(ItemStack itemstack, World world, Entity entity) {
		System.out.println(world.isRemote);
		world.playSoundAtEntity(entity, "FN5728.releaseFive-seveN", 1.0F, 1.0F);
		super.releaseMagazin(itemstack, world, entity);
	}

	@Override
	public void reloadMagazin(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		world.playSoundAtEntity(entityplayer, "FN5728.reloadFive-seveN", 1.0F, 1.0F);
		super.reloadMagazin(itemstack, world, entityplayer);
	}
	
}
