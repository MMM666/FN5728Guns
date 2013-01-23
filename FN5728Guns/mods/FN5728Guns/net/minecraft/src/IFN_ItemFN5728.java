package net.minecraft.src;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IFN_ItemFN5728 extends ItemBow {
	
	/*
	 * リロードのシーケンス
	 * 0x0000	:射撃状態
	 * 0x1000	:リロード開始
	 * 0x2000	:マガジンリリース、下位24bitはリロード時の残弾
	 * 0x8000	:リロード完了
	 */
	
	
	public IFN_ItemFN5728(int i) {
		super(i);
		setMaxDamage(0);
		maxStackSize = 1;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		// リロード完了
		reloadMagazin(itemstack, world, entityplayer);
		return itemstack;
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i) {
		// リロード中止
		cancelReload(itemstack, 0x8000);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world,	EntityPlayer entityplayer) {
		// トリガー
		int li = getReload(itemstack);
		if (li <= 0) {
			if (canReload(itemstack, entityplayer)) {
				// ノーマルリロード
				if (isEmpty(itemstack)) {
					releaseMagazin(itemstack, world, entityplayer);
					MMM_Helper.updateCheckinghSlot(entityplayer, itemstack);
				}
			}
		}
		if (li == 0x0010) {
			if (canReload(itemstack, entityplayer)) {
				// タクティカルリロード
				releaseMagazin(itemstack, world, entityplayer);
				MMM_Helper.updateCheckinghSlot(entityplayer, itemstack);
			}
		}
		entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		return itemstack;
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
		if (world.isRemote) {
			if (MMM_Helper.mc.thePlayer != entity) {
				// クライアントの保持しているプレーヤー以外は処理する必要がない
				return;
			}
			if (entity instanceof EntityPlayer) {
				if (((EntityPlayer)entity).getCurrentEquippedItem() != itemstack) {
					return;
				}
			}
			// マルチ用タクティカルリロード判定処理
			int li = getReload(itemstack);
			try {
				// クアライアント専用コードなのでForgeMPだとエラーが出る
				// というか何でマルチ側でModloaderがよべるん・・・。
				if (MMM_Helper.mc.gameSettings.keyBindAttack.pressed) {
					if (li == 0x0000) {
//						System.out.println("tacticalIFN");
						li = 0x0010;
						setReload(itemstack, li);
						ModLoader.clientSendPacket(new Packet250CustomPayload("IFN", new byte[] {(byte)((li >>> 8) & 0xff), (byte)(li & 0xff)}));
					}
				} else {
					if (li == 0x0010) {
//						System.out.println("nomalIFN");
						li = 0x000;
						setReload(itemstack, li);
						ModLoader.clientSendPacket(new Packet250CustomPayload("IFN", new byte[] {(byte)((li >>> 8) & 0xff), (byte)(li & 0xff)}));
					}
				}
			} catch (Error e) {
			} catch (Exception e) {
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		// リロード時は時間を変更
		int li = getReload(itemstack);
		if ((li >= 0x1000) && (li & 0xf000) < 0x8000) {
			return reloadTime();
		} else {
			return super.getMaxItemUseDuration(itemstack);
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		// リロード時は構えが違う
		return isReload(itemstack) ? EnumAction.block : EnumAction.bow;
	}


	// 独自
	protected boolean fireBullet(ItemStack itemstack, World world, EntityPlayer entityplayer, float f, float f2, float f3) {
		// 発射（エンチャント対応）
		if (!world.isRemote) {
			IFN_EntitySS190 entityss190 = null;
			try {
				Constructor<IFN_EntitySS190> lconstructor = mod_IFN_FN5728Guns.classSS190.getConstructor(World.class, EntityLiving.class, float.class, float.class);
				entityss190 = lconstructor.newInstance(world, entityplayer, 2.0F - f, f3);
			} catch (Exception e) {
			}
			
			// Power
			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemstack);
			if (k > 0) {
				entityss190.setDamage(entityss190.getDamage() + (double)k * 0.5D + 0.5D);
			}
			// Punch
			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemstack);
			if (l > 0) {
				entityss190.setKnockbackStrength(l);
			}
			// Fire
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemstack) > 0) {
				entityss190.setFire(100);
//				entityss190.setFlag(0, true);
			}
			// Infinity
			entityss190.isInfinity = (EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemstack) > 0);
			
			world.playSoundAtEntity(entityplayer, "FN5728.fnP90_s", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
			world.spawnEntityInWorld(entityss190);
		}
		entityplayer.rotationPitch += f2;
		// 無限弾のエンチャントに対応、弾薬使用時はtrueを返す
		return (!mod_IFN_FN5728Guns.UnlimitedInfinity 
				|| EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemstack) <= 0)
				&& !entityplayer.capabilities.isCreativeMode;
	}

	protected void cancelReload(ItemStack itemstack, int force) {
		if (getReload(itemstack) >= force) {
			// リロードのキャンセル
			setReload(itemstack, 0);
		}
	}

	protected boolean canReload(ItemStack itemstack, EntityPlayer entityplayer) {
		// リロードが可能かどうかの判定（エンチャント対応）
//		if (entityplayer.capabilities.depleteBuckets || EnchantmentHelper.getEnchantmentLevel(Enchantment.field_46042_v.effectId, itemstack) > 0) return true;
		if (entityplayer.capabilities.isCreativeMode) return true;
		for (ItemStack is : entityplayer.inventory.mainInventory) {
			if (is != null && is.getItem().itemID == getBulletID(itemstack)) return true;
		}
		return false;
	}

	protected boolean isEmpty(ItemStack itemstack) {
		// 残弾ゼロ？
		return itemstack.getItemDamage() >= getMaxDamage();
	}

	protected void releaseMagazin(ItemStack itemstack, World world, Entity entity) {
		// マガジンをリリースしたときの動作、残弾を記録
		setReload(itemstack, (0x2000 | (itemstack.getItemDamage() & 0x0fff)));
		itemstack.setItemDamage(getMaxDamage());
	}

	protected void reloadMagazin(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		// マガジンを入れたときの動作
//		if (!world.isRemote) 
		{
			// リロード
			if (entityplayer == null || entityplayer.capabilities.isCreativeMode
					|| EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemstack) > 0) {
				itemstack.setItemDamage(0);
			} else {
				// インベントリから弾薬を減らす
				int k = getReload(itemstack);
				k = (k > 0) ? k & 0x0fff : 0;
				for (int l = 0; l < entityplayer.inventory.mainInventory.length; l++) {
					ItemStack is = entityplayer.inventory.mainInventory[l];
					if (is != null && is.getItem().itemID == getBulletID(itemstack)) {
						if (is.stackSize > k) {
							is.stackSize -= k;
							itemstack.setItemDamage(0);
							break;
						} else {
							k -= is.stackSize;
							itemstack.setItemDamage(k);
							is.stackSize = 0;
							entityplayer.inventory.mainInventory[l] = null;
						}
					}
				}
			}
		}
		setReload(itemstack, 0x8000);
		MMM_Helper.updateCheckinghSlot(entityplayer, itemstack);
	}

	// リロードにかかる時間
	public abstract int reloadTime();
	
	public int getBulletID(ItemStack itemstack) {
		// 弾薬の種類
		return mod_IFN_FN5728Guns.fn_SS190.itemID;
	}
	
	// littleMaidMobはこのメソッドを参照して特殊動作を行います
	public boolean isWeaponReload(ItemStack itemstack, EntityPlayer entityplayer) {
		// リロード実行するべきか？
		cancelReload(itemstack, 0x8000);
		return isEmpty(itemstack) && canReload(itemstack, entityplayer);
	}

	public boolean isWeaponFullAuto(ItemStack itemstack) {
		// フルオート武器か？
		// （右クリックした時点で射撃開始されるもの）
		return false;
	}

	/**
	 * リロードカウンタ読み取り
	 */
	public int getReload(ItemStack pItemstack) {
		checkTags(pItemstack);
		return pItemstack.getTagCompound().getInteger("Reload");
	}

	/**
	 * リロードカウンタのセット
	 */
	public void setReload(ItemStack pItemstack, int pValue) {
		checkTags(pItemstack);
		NBTTagCompound lnbt = pItemstack.getTagCompound();
		lnbt.setInteger("Reload", pValue);
	}

	/**
	 * リロード中かね？
	 */
	public boolean isReload(ItemStack pItemstack) {
		return getReload(pItemstack) > 0;
	}

	// 連射用のタイミング回路
	/**
	 * 連射タイミングの設定。
	 * 1=50ms、20=1000ms=1s。
	 */
	public byte getCycleCount(ItemStack pItemstack) {
		return (byte)1;
	}

	public void resetBolt(ItemStack pItemstack) {
		checkTags(pItemstack);
		pItemstack.getTagCompound().setByte("Bolt", getCycleCount(pItemstack));
	}

	/**
	 * 発射タイミングの確認
	 */
	public boolean cycleBolt(ItemStack pItemstack) {
		checkTags(pItemstack);
		NBTTagCompound lnbt = pItemstack.getTagCompound();
		byte lb = lnbt.getByte("Bolt");
		if (lb <= 0) {
//			if (pReset) resetBolt(pItemstack);
			return true;
		} else {
			lnbt.setByte("Bolt", --lb);
			return false;
		}
	}

	public int getBolt(ItemStack pItemstack) {
		checkTags(pItemstack);
		NBTTagCompound lnbt = pItemstack.getTagCompound();
		return lnbt.getByte("Bolt");
	}

	public boolean checkTags(ItemStack pitemstack) {
		// NBTTagの初期化
		if (pitemstack.hasTagCompound()) {
			return true;
		}
		NBTTagCompound ltags = new NBTTagCompound();
		pitemstack.setTagCompound(ltags);
		ltags.setInteger("Reload", 0x0000);
		ltags.setByte("Bolt", (byte)0);
		return false;
	}

	public static Entity checkMaid(Entity entity) {
		// メイドさんチェック
		try {
			Field field = entity.getClass().getField("maidAvatar");
			entity = (Entity)field.get(entity);
		}
		catch (NoSuchFieldException e) {
		}
		catch (Exception e) {
		}
		return entity;
	}

}
