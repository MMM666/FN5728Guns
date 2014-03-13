package mmm.FN5728Guns;

import java.util.List;

import mmm.lib.guns.ItemBulletBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemSS190 extends ItemBulletBase {

	protected IIcon colorIcon;


	public ItemSS190() {
		setHasSubtypes(true);
	}
/*
	public EntityBulletBase getBulletEntity(ItemStack pGun, ItemStack pBullet, World pWorld, EntityPlayer pPlayer, int pUseCount) {
		// 弾体
		ItemGunsBase lgun = ((ItemGunsBase)pGun.getItem());
		return new EntityBulletBase(pWorld, pPlayer, pGun, pBullet,
				getSpeed(pBullet) * lgun.getEfficiency(pGun, pPlayer, pUseCount),
				lgun.getAccuracy(pGun, pPlayer, pUseCount));
	}
*/
	@Override
	public float getSpeed(ItemStack pBullet) {
		// SS190	: 716m/s
		// L191		: 716m/s
		// SS197SR	: 594m/s
		return pBullet.getItemDamage() == 7 ? 29.7F : 35.8F;
	}

	@Override
	public float getReaction(ItemStack pBullet) {
		// 初速に応じて反動を制御
		return getSpeed(pBullet) / 35.8F;
	}

	@Override
	public void playSoundFire(World pWorld, EntityPlayer pPlayer, ItemStack pGun, ItemStack pBullet) {
		if (pGun.getItem() instanceof ItemP90) {
			pWorld.playSoundAtEntity(pPlayer, "mmm:FN5728.P90.fire",
					0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		} else {
			pWorld.playSoundAtEntity(pPlayer, "mmm:FN5728.57.fire",
					0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		}
	}

	@Override
	public int getBulletColor(ItemStack pBullet) {
		if (pBullet != null) {
			if (pBullet.getItemDamage() == 1) {
				return 0x00ffbb99;
			}
			if (pBullet.getItemDamage() == 7) {
				return 0x0099bbff;
			}
		}
		return 0x804000;
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return (metadata > 0) ? 2 : 1;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return pass == 0 ? itemIcon : colorIcon;
	}

	@Override
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
		if (par2 == 1) {
			switch (par1ItemStack.getItemDamage()) {
			case 1:
				return 0x00ff0000;
			case 7:
				return 0x003399ff;
			}
		}
		return 0x00ffffff;
	}

	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		itemIcon = par1IconRegister.registerIcon(getIconString());
		colorIcon = par1IconRegister.registerIcon(getIconString() + "_C");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_) {
		p_150895_3_.add(new ItemStack(p_150895_1_, 1, 0));
		p_150895_3_.add(new ItemStack(p_150895_1_, 1, 1));
		p_150895_3_.add(new ItemStack(p_150895_1_, 1, 7));
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return super.getUnlocalizedName(par1ItemStack) + Integer.toString(par1ItemStack.getItemDamage());
	}

}