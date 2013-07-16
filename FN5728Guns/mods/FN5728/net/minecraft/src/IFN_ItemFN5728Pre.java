package net.minecraft.src;

/**
 * エラー回避用のダミークラス。
 */
public class IFN_ItemFN5728Pre extends ItemBow {

	public IFN_ItemFN5728Pre(int par1) {
		super(par1);
	}

	@Override
	public void registerIcons(IconRegister par1IconRegister) {
		// bowで再定義しているので戻す
		itemIcon = par1IconRegister.registerIcon(func_111208_A());
	}

	@Override
	public Icon getItemIconForUseDuration(int par1) {
		// 多分意味ない
		return itemIcon;
	}

}
