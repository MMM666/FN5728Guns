package mmm.lib.Guns;

import mmm.FN5728Guns.FN5728Guns;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemGunsBase extends ItemBow {

	/**
	 * ダミーの弾、いらんか？
	 */
	public static Item bulletBase = new ItemBulletBase().setUnlocalizedName("dammyBullet");

	public ItemGunsBase() {
		maxStackSize = 1;
		
	}

	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		// ItemBowで再定義されているので標準に戻す。
		// 必要なら上書きすること。
		itemIcon = par1IconRegister.registerIcon(this.getIconString());
	}

	@Override
	public IIcon getItemIconForUseDuration(int par1) {
		// ItemBowからの継承、引き時間によってアイコンを変更する場合は此処でアイコンを返す。
		// 但し、有効かどうかは不明。
		return itemIcon;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		// TODO Forgeのイベントハンドラどうする？
		if (par3EntityPlayer.capabilities.isCreativeMode
				|| hasAmmo(par1ItemStack, par2World, par3EntityPlayer)) {
			par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
		}
		
		return par1ItemStack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer, int par4) {
		fireBullet(par1ItemStack, par2World, par3EntityPlayer, par4);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		// TODO Auto-generated method stub
		super.onUsingTick(stack, player, count);
	}

	/**
	 * 発射可能状態か？
	 * てか、弾があるか？
	 * @param par1ItemStack
	 * @param par2World
	 * @param par3EntityPlayer
	 * @return
	 */
	public boolean hasAmmo(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		return true;
	}

	public void fireBullet(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer, int par4) {
		// 実験コード
		int ldamage = getDamage(par1ItemStack);
		ItemStack lbullet = getBullet(par1ItemStack, ldamage);
		ItemBulletBase libullet = null;
		if (lbullet.getItem() instanceof ItemBulletBase) {
			libullet = (ItemBulletBase)lbullet.getItem();
		}
		setDamage(par1ItemStack, ldamage + 1);
		if (par3EntityPlayer.capabilities.isCreativeMode) {
			if (getDamage(par1ItemStack) > getMaxDamage(par1ItemStack)) {
				reload(par1ItemStack, par3EntityPlayer);
//				par1ItemStack.setItemDamage(0);
			}
		}
		// 発射音
//		par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F,
//				0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		if (libullet != null) {
			libullet.soundFire(par2World, par3EntityPlayer, lbullet);
		}
		
		// 弾体を発生させる
		if (!par2World.isRemote) {
			Entity lentity;
			FN5728Guns.Debug("Bulle: %s", lbullet == null ? "NULL" : lbullet.toString());
			if (libullet != null) {
				lentity = libullet.getBulletEntity(par1ItemStack, lbullet, par2World, par3EntityPlayer);
				par2World.spawnEntityInWorld(lentity);
			}
		}
	}

	/**
	 * 装填されている弾を返す。
	 * @param pGun
	 * @param pIndex
	 * @return
	 */
	public ItemStack getBullet(ItemStack pGun, int pIndex) {
		if (pGun.hasTagCompound()) {
			NBTTagCompound ltag = pGun.getTagCompound();
			NBTTagCompound lbullet = ltag.getCompoundTag("Magazin");
			String ls = String.format("%04d", pIndex);
			if (lbullet.hasKey(ls)) {
				return ItemStack.loadItemStackFromNBT(lbullet.getCompoundTag(ls));
			}
		}
		return new ItemStack(bulletBase);
	}

	public void setBullet(ItemStack pGun, int pIndex, ItemStack pBullet) {
		if (!pGun.hasTagCompound()) {
			pGun.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound ltag = pGun.getTagCompound();
		NBTTagCompound lmagazin = ltag.getCompoundTag("Magazin");
		ltag.setTag("Magazin", lmagazin);
		String ls = String.format("%04d", pIndex);
		NBTTagCompound lbullet = ltag.getCompoundTag(ls);
		lmagazin.setTag(ls, lbullet);
		pBullet.writeToNBT(lbullet);
	}

	/**
	 * 弾を装填する、装填する際にはスタックから減らす。
	 * @param pGun
	 * @param pIndex
	 * @param pBullet
	 */
	public void loadBullet(ItemStack pGun, ItemStack pBullet) {
		int li = getDamage(pGun);
		if (li > 0) {
			li--;
			setBullet(pGun, li, pBullet);
			pBullet.stackSize--;
			pGun.setItemDamage(li);
		}
	}

	/**
	 * リロード
	 * @param stack
	 * @param player
	 */
	public void reload(ItemStack pGun, EntityPlayer pPlayer) {
		while (getDamage(pGun) > 0) {
			loadBullet(pGun, new ItemStack(bulletBase));
		}
	}

}
