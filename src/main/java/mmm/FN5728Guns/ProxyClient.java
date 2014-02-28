package mmm.FN5728Guns;

import cpw.mods.fml.client.registry.RenderingRegistry;
import mmm.lib.ProxyCommon;
import mmm.lib.guns.EntityBulletBase;

public class ProxyClient extends ProxyCommon {

	@Override
	public void init() {
		// レンダラの登録
		RenderingRegistry.registerEntityRenderingHandler(EntityBulletBase.class, new RenderSS190());
	}

}
