package mrriegel.ifinder;

import java.awt.Color;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class Client {
	public static KeyBinding light = new KeyBinding(ItemFinder.MODNAME,
			Keyboard.KEY_G, ItemFinder.MODNAME);

	@SubscribeEvent
	public void tick(PlayerTickEvent e) {
		if (e.player.worldObj.isRemote
				&& e.player.worldObj.getTotalWorldTime() % 10 == 0
				&& (Client.light.isKeyDown() ^ ItemFinder.alwaysOn)) {
			ItemFinder.INSTANCE.sendToServer(new KeyMessage());
		}
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		if (!(Client.light.isKeyDown() ^ ItemFinder.alwaysOn))
			return;
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if (player.getHeldItemMainhand() == null
				&& player.getHeldItemOffhand() == null)
			return;

		double doubleX = player.lastTickPosX
				+ (player.posX - player.lastTickPosX) * event.getPartialTicks();
		double doubleY = player.lastTickPosY
				+ (player.posY - player.lastTickPosY) * event.getPartialTicks();
		double doubleZ = player.lastTickPosZ
				+ (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

		GlStateManager.pushMatrix();

		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
		// code
		long c = (System.currentTimeMillis() / 15l) % 360l;
		Color color = ItemFinder.color == -1 ? Color.getHSBColor(c / 360f, 1f,
				1f) : new Color(ItemFinder.color);

		for (BlockPos p : ItemFinder.instance.lis) {
			renderBlockPos(p, color);

		}

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.color(1f, 1f, 1f, 1f);

		GlStateManager.popMatrix();
	}

	void renderBlockPos(BlockPos p, Color color) {
		float x = p.getX(), y = p.getY(), z = p.getZ();
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f,
				color.getBlue() / 255f, 1f);
		GL11.glLineWidth(2.5f);
		GlStateManager.pushAttrib();
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		float offset = 1f;
		renderer.pos(x, y, z).endVertex();
		renderer.pos(x + offset, y, z).endVertex();

		renderer.pos(x, y, z).endVertex();
		renderer.pos(x, y + offset, z).endVertex();

		renderer.pos(x, y, z).endVertex();
		renderer.pos(x, y, z + offset).endVertex();

		renderer.pos(x + offset, y + offset, z + offset).endVertex();
		renderer.pos(x, y + offset, z + offset).endVertex();

		renderer.pos(x + offset, y + offset, z + offset).endVertex();
		renderer.pos(x + offset, y, z + offset).endVertex();

		renderer.pos(x + offset, y + offset, z + offset).endVertex();
		renderer.pos(x + offset, y + offset, z).endVertex();

		renderer.pos(x, y + offset, z).endVertex();
		renderer.pos(x, y + offset, z + offset).endVertex();

		renderer.pos(x, y + offset, z).endVertex();
		renderer.pos(x + offset, y + offset, z).endVertex();

		renderer.pos(x + offset, y, z).endVertex();
		renderer.pos(x + offset, y, z + offset).endVertex();

		renderer.pos(x + offset, y, z).endVertex();
		renderer.pos(x + offset, y + offset, z).endVertex();

		renderer.pos(x, y, z + offset).endVertex();
		renderer.pos(x + offset, y, z + offset).endVertex();

		renderer.pos(x, y, z + offset).endVertex();
		renderer.pos(x, y + offset, z + offset).endVertex();
		tessellator.draw();
		GlStateManager.popAttrib();
	}
}
