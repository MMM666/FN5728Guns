package net.minecraft.src;

/**
 * �G���[���p�̃_�~�[�N���X�B
 */
public class IFN_ItemFN5728Pre extends ItemBow {

	public IFN_ItemFN5728Pre(int par1) {
		super(par1);
	}

	@Override
	public void registerIcons(IconRegister par1IconRegister) {
		// bow�ōĒ�`���Ă���̂Ŗ߂�
		itemIcon = par1IconRegister.registerIcon(func_111208_A());
	}

	@Override
	public Icon getItemIconForUseDuration(int par1) {
		// �����Ӗ��Ȃ�
		return itemIcon;
	}

}
