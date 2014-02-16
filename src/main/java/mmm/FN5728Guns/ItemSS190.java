package mmm.FN5728Guns;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import mmm.lib.Guns.ItemBulletBase;

public class ItemSS190 extends ItemBulletBase {

	@Override
	public float getSpeed(ItemStack pBullet) {
		// 716m/s
		return 35.8F;
//		return 5.8F;
	}

	@Override
	public float getReaction(ItemStack pBullet) {
		return 1.0F;
	}

	public void soundFire(World pWorld, EntityPlayer pPlayer, ItemStack pBullet) {
		pWorld.playSoundAtEntity(pPlayer, "mmm:FN5728.57.fire",
				0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
	}

}
