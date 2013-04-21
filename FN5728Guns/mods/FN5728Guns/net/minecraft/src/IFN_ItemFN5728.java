package net.minecraft.src;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IFN_ItemFN5728 extends ItemBow {
	
	/*
	 * �����[�h�̃V�[�P���X
	 * 0x0000	:�ˌ����
	 * 0x1000	:�����[�h�J�n
	 * 0x2000	:�}�K�W�������[�X�A����24bit�̓����[�h���̎c�e
	 * 0x8000	:�����[�h����
	 */
	public static int IFNValFire		= 0x0000;
	public static int IFNValReloadTac	= 0x0010;
	public static int IFNValReloadStart	= 0x1000;
	public static int IFNValReleaseMag	= 0x2000;
	public static int IFNValReloadEnd	= 0x8000;



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
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		// �����[�h����
		mod_IFN_FN5728Guns.Debug(String.format("onFoodEaten-remort:%b", par2World.isRemote));
		reloadMagazin(par1ItemStack, par2World, par3EntityPlayer);
		return par1ItemStack;
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i) {
		// �����[�h���~
		mod_IFN_FN5728Guns.Debug(String.format("onPlayerStoppedUsing-remort:%b", world.isRemote));
		cancelReload(itemstack, IFNValReloadEnd);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world,	EntityPlayer entityplayer) {
		// �g���K�[
		int li = getReload(itemstack);
		mod_IFN_FN5728Guns.Debug(String.format("onItemRightClick-remort:%b, val:%04x", world.isRemote, li));
		if (li <= IFNValFire) {
			if (canReload(itemstack, entityplayer)) {
				// �m�[�}�������[�h
				if (isEmpty(itemstack)) {
					mod_IFN_FN5728Guns.Debug(String.format("reloadNomal-remort:%b", world.isRemote));
					releaseMagazin(itemstack, world, entityplayer);
					MMM_Helper.updateCheckinghSlot(entityplayer, itemstack);
				}
			}
		}
		if (li == IFNValReloadTac) {
			if (canReload(itemstack, entityplayer)) {
				// �^�N�e�B�J�������[�h
				mod_IFN_FN5728Guns.Debug(String.format("reloadTac-remort:%b", world.isRemote));
				releaseMagazin(itemstack, world, entityplayer);
				MMM_Helper.updateCheckinghSlot(entityplayer, itemstack);
			}
		}
		entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		return itemstack;
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
//		int li = getReload(itemstack);
//		if (li != 0 && entity instanceof EntityPlayer) {
//			EntityPlayer lep = (EntityPlayer)entity;
//			mod_IFN_FN5728Guns.Debug(String.format("onUpdate-remort:%b, val:%x, dam:%d, using:%b, dt:%d",
//					world.isRemote, li, itemstack.getItemDamage(), lep.getItemInUse() == itemstack, lep.getItemInUseDuration()));
//		}
		if (world.isRemote) {
			if (MMM_Helper.mc.thePlayer != entity) {
				// �N���C�A���g�̕ێ����Ă���v���[���[�ȊO�͏�������K�v���Ȃ�
				return;
			}
			if (isReload(itemstack)) {
				// �����[�h���ł��I��
				return;
			}
			if (entity instanceof EntityPlayer) {
				EntityPlayer lep = (EntityPlayer)entity;
				if (lep.getCurrentEquippedItem() != itemstack) {
					// ����Ɏ����Ă��Ȃ���ΏI��
					return;
				}
				if (lep.getItemInUse() == itemstack) {
					// �g�p���ł��I��
					return;
				}
				// �}���`�p�^�N�e�B�J�������[�h���菈��
				int li = getReload(itemstack);
				try {
					// �N�A���C�A���g��p�R�[�h�Ȃ̂�ForgeMP���ƃG���[���o��
					// �Ƃ��������Ń}���`����Modloader����ׂ��E�E�E�B
					if (MMM_Helper.mc.gameSettings.keyBindAttack.pressed) {
						if (li == IFNValFire) {
							mod_IFN_FN5728Guns.Debug("tacticalIFN");
							li = IFNValReloadTac;
							ModLoader.clientSendPacket(new Packet250CustomPayload("IFN", new byte[] {(byte)((li >>> 8) & 0xff), (byte)(li & 0xff)}));
						}
					} else {
						if (li == IFNValReloadTac) {
							mod_IFN_FN5728Guns.Debug("nomalIFN");
							li = IFNValFire;
							ModLoader.clientSendPacket(new Packet250CustomPayload("IFN", new byte[] {(byte)((li >>> 8) & 0xff), (byte)(li & 0xff)}));
						}
					}
				} catch (Error e) {
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		// �����[�h���͎��Ԃ�ύX
		int li = getReload(itemstack);
		if ((li >= IFNValReloadStart) && (li & 0xf000) < IFNValReloadEnd) {
			return reloadTime();
		} else {
			return super.getMaxItemUseDuration(itemstack);
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		// �����[�h���͍\�����Ⴄ
		return isReload(itemstack) ? EnumAction.block : EnumAction.bow;
	}


	// �Ǝ�
	/**
	 * �e�̔��ˁB
	 * �e���ʃN���X�ɂ��Ă������Ɉڂ����H
	 * @param f1:�e�����萫�B
	 * @param f2:�������
	 * @param f3:�������䗦
	 */
	protected void fireBullet(ItemStack itemstack, World world, EntityPlayer entityplayer, float f1, float f2, float f3) {
		// ����
		ItemStack lis = getAmmo(itemstack, itemstack.getMaxDamage() - itemstack.getItemDamage());
		if (lis == null) {
			lis = new ItemStack(getBulletID(itemstack), 1, 0);
		}
		boolean lflag = true;
		if (lis.getItem() instanceof IFN_ItemSS190) {
			IFN_ItemSS190 lib = (IFN_ItemSS190)lis.getItem();
			lflag = lib.fireBullet(itemstack, world, entityplayer, lis.getItemDamage(), f1, f2, f3);
		}
		if (lflag) {
			clearAmmo(itemstack, itemstack.getMaxDamage() - itemstack.getItemDamage());
			itemstack.damageItem(1, entityplayer);
		}
	}

	protected void cancelReload(ItemStack itemstack, int force) {
		if (getReload(itemstack) >= force) {
			// �����[�h�̃L�����Z��
			setReload(itemstack, IFNValFire);
		}
	}

	protected boolean canReload(ItemStack itemstack, EntityPlayer entityplayer) {
		// �����[�h���\���ǂ����̔���i�G���`�����g�Ή��j
		if (entityplayer.capabilities.isCreativeMode) return true;
		for (ItemStack is : entityplayer.inventory.mainInventory) {
			if (isConformityBullet(is)) return true;
		}
		return false;
	}

	protected boolean isEmpty(ItemStack itemstack) {
		// �c�e�[���H
		return itemstack.getItemDamage() >= getMaxDamage();
	}

	protected void releaseMagazin(ItemStack itemstack, World world, Entity entity) {
		// �}�K�W���������[�X�����Ƃ��̓���A�c�e���L�^
		setReload(itemstack, (IFNValReleaseMag | (itemstack.getItemDamage() & 0x0fff)));
		itemstack.setItemDamage(getMaxDamage());
	}

	protected void reloadMagazin(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		// �}�K�W������ꂽ�Ƃ��̓���
//		if (!world.isRemote) 
		{
			// �����[�h
			if (entityplayer == null || entityplayer.capabilities.isCreativeMode) {
				itemstack.setItemDamage(0);
			} else {
				// �C���x���g������e������炷
				int lk = getReload(itemstack);
				lk = (lk > 0) ? lk & 0x0fff : 0;
				itemstack.setItemDamage(lk);
				for (int ll = 0; lk > 0 && ll < entityplayer.inventory.mainInventory.length; ll++) {
					ItemStack lis = entityplayer.inventory.mainInventory[ll];
					if (isConformityBullet(lis)) {
						for (;lk > 0 && lis.stackSize > 0;) {
							setAmmo(itemstack, lk--, lis);
							itemstack.setItemDamage(itemstack.getItemDamage() - 1);
							if (EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemstack) == 0) {
								// ����ӂ����t���ĂȂ�������e�����炷�B
								lis.stackSize--;
							}
							if (lis.stackSize <= 0) {
								entityplayer.inventory.mainInventory[ll] = null;
							}
						}
					}
				}
			}
		}
		mod_IFN_FN5728Guns.Debug(String.format("AmmoList."));
		for (int li = 0; li < itemstack.getMaxDamage(); li++) {
			ItemStack lis = getAmmo(itemstack, li);
			if (lis != null) {
				mod_IFN_FN5728Guns.Debug(String.format("Ammo:%03d=%s(%d, %d)", li, lis.getItemName(), lis.itemID, lis.getItemDamage()));
			}
		}
		setReload(itemstack, IFNValReloadEnd);
		MMM_Helper.updateCheckinghSlot(entityplayer, itemstack);
	}

	// �����[�h�ɂ����鎞��
	public abstract int reloadTime();

	public int getBulletID(ItemStack itemstack) {
		// �e��̎��
		return mod_IFN_FN5728Guns.fn_SS190.itemID;
	}

	/**
	 * �g�p�o����e�򂩂ǂ����̔���
	 */
	public boolean isConformityBullet(ItemStack pItemstack) {
		if (pItemstack != null && pItemstack.itemID == mod_IFN_FN5728Guns.fn_SS190.itemID) {
			return true;
		}
		return false;
	}

	// littleMaidMob�͂��̃��\�b�h���Q�Ƃ��ē��ꓮ����s���܂�
	public boolean isWeaponReload(ItemStack itemstack, EntityPlayer entityplayer) {
		// �����[�h���s����ׂ����H
		cancelReload(itemstack, IFNValReloadEnd);
		return isEmpty(itemstack) && canReload(itemstack, entityplayer);
	}

	public boolean isWeaponFullAuto(ItemStack itemstack) {
		// �t���I�[�g���킩�H
		// �i�E�N���b�N�������_�Ŏˌ��J�n�������́j
		return false;
	}

	/**
	 * �����[�h�J�E���^�ǂݎ��
	 */
	public int getReload(ItemStack pItemstack) {
		checkTags(pItemstack);
		return pItemstack.getTagCompound().getInteger("Reload");
	}

	/**
	 * �����[�h�J�E���^�̃Z�b�g
	 */
	public void setReload(ItemStack pItemstack, int pValue) {
		checkTags(pItemstack);
		NBTTagCompound lnbt = pItemstack.getTagCompound();
		lnbt.setInteger("Reload", pValue);
	}

	/**
	 * �����[�h�����ˁH
	 */
	public boolean isReload(ItemStack pItemstack) {
		return getReload(pItemstack) >= IFNValReloadStart;
	}

	// �A�˗p�̃^�C�~���O��H
	/**
	 * �A�˃^�C�~���O�̐ݒ�B
	 * 1=50ms�A20=1000ms=1s�B
	 */
	public byte getCycleCount(ItemStack pItemstack) {
		return (byte)1;
	}

	public void resetBolt(ItemStack pItemstack) {
		checkTags(pItemstack);
		pItemstack.getTagCompound().setByte("Bolt", getCycleCount(pItemstack));
	}

	/**
	 * ���˃^�C�~���O�̊m�F
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

	/**
	 * �}�K�W���ɒe�����߂�
	 */
	public void setAmmo(ItemStack pGun, int pIndex, ItemStack pAmmo) {
		if (!pGun.getTagCompound().hasKey("Ammo")) {
			pGun.getTagCompound().setCompoundTag("Ammo", new NBTTagCompound());
		}
		NBTTagCompound lnbt = pGun.getTagCompound().getCompoundTag("Ammo");
		lnbt.setInteger(Integer.toString(pIndex) + "i", pAmmo.itemID);
		lnbt.setInteger(Integer.toString(pIndex) + "d", pAmmo.getItemDamage());
	}

	/**
	 * ���e����Ă���e�����o��
	 */
	public ItemStack getAmmo(ItemStack pGun, int pIndex) {
		NBTTagCompound lnbt = pGun.getTagCompound().getCompoundTag("Ammo");
		int lid = lnbt.getInteger(Integer.toString(pIndex) + "i");
		int ldam = lnbt.getInteger(Integer.toString(pIndex) + "d");
		return lid == 0 ? null : new ItemStack(lid, 1, ldam);
	}

	public void clearAmmo(ItemStack pGun, int pIndex) {
		NBTTagCompound lnbt = pGun.getTagCompound().getCompoundTag("Ammo");
		String ls = Integer.toString(pIndex);
		lnbt.removeTag(ls + "i");
		lnbt.removeTag(ls + "d");
	}

	public boolean checkTags(ItemStack pitemstack) {
		// NBTTag�̏�����
		if (pitemstack.hasTagCompound()) {
			return true;
		}
		NBTTagCompound ltags = new NBTTagCompound();
		pitemstack.setTagCompound(ltags);
		ltags.setInteger("Reload", 0x0000);
		ltags.setByte("Bolt", (byte)0);
		NBTTagCompound lammo = new NBTTagCompound();
		for (int li = 0; li < getMaxDamage(); li++) {
			lammo.setLong(Integer.toString(li), 0L);
		}
		ltags.setCompoundTag("Ammo", lammo);
		return false;
	}

}
