package mmm.lib.Guns;

import mmm.FN5728Guns.FN5728Guns;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class ItemGunsBase extends ItemBow {

	protected static byte State_Ready		= 0x00;
	protected static byte State_Empty		= 0x10;
	protected static byte State_ReloadTac	= 0x20;
	protected static byte State_Reload		= 0x30;
	protected static byte State_ReleseMag	= 0x40;
	protected static byte State_ReloadEnd	= 0x60;
	
	/**
	 * ダミーの弾、いらんか？
	 */
	public static Item bulletBase = new ItemBulletBase().setUnlocalizedName("dammyBullet");
	protected IIcon[] iconArray;


	public ItemGunsBase() {
		maxStackSize = 1;
		setFull3D();
	}

	/**
	 * 空打ちした時の音
	 * @param pWorld
	 * @param pPlayer
	 * @param pGun
	 */
	public abstract void soundEmpty(World pWorld, EntityPlayer pPlayer, ItemStack pGun);

	/**
	 * マガジンを外した時（リロード開始時）の音
	 * @param pWorld
	 * @param pPlayer
	 * @param pGun
	 */
	public abstract void soundRelease(World pWorld, EntityPlayer pPlayer, ItemStack pGun);

	/**
	 * マガジンを入れた時（リロード完了時）の音
	 * @param pWorld
	 * @param pPlayer
	 * @param pGun
	 */
	public abstract void soundReload(World pWorld, EntityPlayer pPlayer, ItemStack pGun);

	/**
	 * リロードに掛る時間
	 * @return
	 */
	public abstract int getReloadTime(ItemStack pGun);

	/**
	 * 残心の時間
	 * 未実装
	 * @param pGun
	 * @return
	 */
	public int getHoldTime(ItemStack pGun) {
		return 10;
	}

	/**
	 * 弾速のエネルギー効率
	 * @param pGun
	 * @return
	 */
	public float getEfficiency(ItemStack pGun) {
		return 1.0F;
	}

	/**
	 * 銃身の安定性
	 * @param pGun
	 * @return
	 */
	public float getStability(ItemStack pGun) {
		return 1.0F;
	}


	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		// ItemBowで再定義されているので標準に戻す。
		// 必要なら上書きすること。
		iconArray = new IIcon[3];
		iconArray[0] = par1IconRegister.registerIcon(getIconString());
		iconArray[1] = par1IconRegister.registerIcon(getIconString() + "_Empty");
		iconArray[2] = par1IconRegister.registerIcon(getIconString() + "_Release");
		itemIcon = iconArray[0];
	}

	@Override
	public IIcon getItemIconForUseDuration(int par1) {
		// ItemBowからの継承、引き時間によってアイコンを変更する場合は此処でアイコンを返す。
		// 但し、有効かどうかは不明。
		return itemIcon;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		int li = getState(stack);
		if (li >= State_ReleseMag && li < State_ReloadEnd) {
			return iconArray[2];
		} else
		if (li >= State_Empty && li < State_ReleseMag) {
			return iconArray[1];
		} else {
			return iconArray[0];
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		// TODO Forgeのイベントハンドラどうする？
		FN5728Guns.Debug("trigger");
		int li = getState(par1ItemStack);
		if (par3EntityPlayer.isSwingInProgress) {
			setState(par1ItemStack, State_ReloadTac);
			GunsBase.Debug("Tactical Reload.");
		} else {
			if (li >= State_Empty && li < State_Reload) {
				if (hasAmmo(par1ItemStack, par2World, par3EntityPlayer)) {
					setState(par1ItemStack, State_Reload);
					GunsBase.Debug("Reload.");
				}
			}
		}
		GunsBase.setUncheckedItemStack(par1ItemStack, par3EntityPlayer);
//		if (par3EntityPlayer.capabilities.isCreativeMode
//				|| hasAmmo(par1ItemStack, par2World, par3EntityPlayer)) {
//		}
		// 撃つ
		par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
		
		return par1ItemStack;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		int li = getState(stack);
		if (li == State_Reload) {
			setState(stack, State_ReleseMag);
			releaseMagazin(stack, player.worldObj, player);
			GunsBase.setUncheckedItemStack(stack, player);
		}
		if (li == State_ReloadTac) {
			setState(stack, State_ReleseMag);
			releaseMagazin(stack, player.worldObj, player);
			GunsBase.setUncheckedItemStack(stack, player);
		}
		onFireTick(stack, player.worldObj, player, count, li);
	}

	/**
	 * タイマー処理の独自記述はこちらに書くと良し
	 * @param pGun
	 * @param pWorld
	 * @param pPlayer
	 * @param count
	 * @param pState
	 */
	public void onFireTick(ItemStack pGun, World pWorld, EntityPlayer pPlayer, int count, int pState) {
	}

	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		int li = getState(par1ItemStack);
		if (li >= State_ReleseMag && li < State_ReloadEnd) {
			reloadMagazin(par1ItemStack, par2World, par3EntityPlayer);
			setState(par1ItemStack, State_ReloadEnd);
			GunsBase.setUncheckedItemStack(par1ItemStack, par3EntityPlayer);
		}
		return par1ItemStack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer, int par4) {
		int li = getState(par1ItemStack);
		if (li == State_ReloadEnd) {
			setState(par1ItemStack, State_Ready);
		} else if (!isAmmoEmpty(par1ItemStack)) {
			// 弾があるので発射
			if (fireBullet(par1ItemStack, par2World, par3EntityPlayer, par4) <= 0) {
				// 撃ち尽くした
				setState(par1ItemStack, State_Empty);
			}
		} else if (li < State_Reload) {
			// 弾がないので空打ち
			soundEmpty(par2World, par3EntityPlayer, par1ItemStack);
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		// 射撃時と装填時で挙動を変える
		int li = getState(par1ItemStack);
		if (li >= State_Empty && li < State_ReloadEnd) {
			return getReloadTime(par1ItemStack);
		}
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		int li = getState(par1ItemStack);
		if (li < State_ReloadTac) {
			return EnumAction.bow;
		}
		return EnumAction.block;
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
		return par3EntityPlayer.capabilities.isCreativeMode || false;
	}

	/**
	 * 弾体の発射
	 * @param pGun
	 * @param pWorld
	 * @param pPlayer
	 * @param par4
	 * @return 射撃後の残弾数
	 */
	public int fireBullet(ItemStack pGun, World pWorld, EntityPlayer pPlayer, int par4) {
		// 残弾数を減らす
		int ldamage = getDamage(pGun);
		setDamage(pGun, ldamage + 1);
		ItemStack lbullet = getBullet(pGun, ldamage);
//		GunsBase.setUncheckedItemStack(pGun, pPlayer);
		// 戻り値として再設定、残弾数を返す。
		ldamage = getMaxDamage(lbullet) - ldamage - 1;
		
		// 弾薬を取り出す
		if (lbullet == null) return ldamage;
		ItemBulletBase libullet = null;
		if (lbullet.getItem() instanceof ItemBulletBase) {
			libullet = (ItemBulletBase)lbullet.getItem();
		}
		// 発射音、弾薬ごとに音声を設定
		if (libullet != null) {
			libullet.soundFire(pWorld, pPlayer, lbullet);
		}
		
		// 弾体を発生させる
		if (!pWorld.isRemote) {
			Entity lentity;
			FN5728Guns.Debug("Bulle: %s", lbullet == null ? "NULL" : lbullet.toString());
			if (libullet != null) {
				lentity = libullet.getBulletEntity(pGun, lbullet, pWorld, pPlayer);
				pWorld.spawnEntityInWorld(lentity);
			}
		}
		return ldamage;
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
		NBTTagCompound ltag = getTagCompound(pGun);
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
	 * 残弾確認
	 * @param pGun
	 * @return
	 */
	public boolean isAmmoEmpty(ItemStack pGun) {
		return getDamage(pGun) >= getMaxDamage(pGun);
	}

	/**
	 * マガジンを取り外す。
	 * @param pGun
	 * @param pWorld
	 * @param pPlayer
	 */
	public void releaseMagazin(ItemStack pGun, World pWorld, EntityPlayer pPlayer) {
		soundRelease(pWorld, pPlayer, pGun);
		setDamage(pGun, getMaxDamage(pGun));
	}

	/**
	 * リロード完了
	 * @param stack
	 * @param player
	 */
	public void reloadMagazin(ItemStack pGun, World pWorld, EntityPlayer pPlayer) {
		while (getDamage(pGun) > 0) {
			loadBullet(pGun, new ItemStack(bulletBase));
		}
		soundReload(pWorld, pPlayer, pGun);
	}

	public void setState(ItemStack pGun, byte pState) {
		NBTTagCompound ltag = getTagCompound(pGun);
		ltag.setByte("State", pState);
	}

	public byte getState(ItemStack pGun) {
		NBTTagCompound ltag = getTagCompound(pGun);
		return ltag.getByte("State");
	}

	protected NBTTagCompound getTagCompound(ItemStack pGun) {
		if (!pGun.hasTagCompound()) {
			pGun.setTagCompound(new NBTTagCompound());
		}
		return pGun.getTagCompound();
	}

}
