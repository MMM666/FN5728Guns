package mmm.FN5728Guns;

import mmm.lib.guns.EntityBulletBase;
import mmm.lib.guns.RenderBulletBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class RenderSS190 extends RenderBulletBase {

	@Override
	public int getColor(EntityBulletBase pEntity) {
		if (pEntity.bullet != null) {
			if (pEntity.bullet.getItemDamage() == 1) {
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glDisable(GL11.GL_LIGHTING);
				OpenGlHelper.setLightmapTextureCoords(
						OpenGlHelper.lightmapTexUnit, 240F, 240F);
			}
		}
		return super.getColor(pEntity);
	}

	@Override
	public void renderOptional(EntityBulletBase pEntity,
			double pX, double pY, double pZ, float var8, float var9) {
		if (pEntity.bullet != null && pEntity.bullet.getItemDamage() == 1) {
			// 曳光弾
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			// 描画色
			int lcolor = getColor(pEntity);
			Tessellator tessellator = Tessellator.instance;
			double ll = (double)-pEntity.speed * 2D;
			for (int j = 0; j < 2; j++) {
				GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
//				GL11.glNormal3f(0.0F, 0.0F, f10);
				tessellator.startDrawing(GL11.GL_TRIANGLES);
				tessellator.setColorOpaque_I(lcolor);
				tessellator.addVertex(0.0D, -0.5D, 0.0D);
				tessellator.addVertex(ll, 0.0D, 0.0D);
				tessellator.addVertex(0.0D, 0.5D, 0.0D);
				tessellator.draw();
			}
			
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

}
