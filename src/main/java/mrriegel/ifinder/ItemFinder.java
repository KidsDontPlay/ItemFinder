package mrriegel.ifinder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

@Mod(modid = ItemFinder.MODID, version = ItemFinder.VERSION, name = ItemFinder.MODNAME)
public class ItemFinder {
	public static final String MODID = "ifinder";
	public static final String MODNAME = "ItemFinder";
	public static final String VERSION = "1.0";
	public static KeyBinding light = new KeyBinding(ItemFinder.MODID + ".find",
			Keyboard.KEY_F, ItemFinder.MODNAME);
	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(
			ItemFinder.MODID);
	public static List<BlockPos> lis = new ArrayList<BlockPos>();
	public static boolean visible;
	public static long last = 0;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		ClientRegistry.registerKeyBinding(ItemFinder.light);
		INSTANCE.registerMessage(KeyMessage.class, KeyMessage.class, 0,
				Side.SERVER);
	}

	@SubscribeEvent
	public void onKey(InputEvent.KeyInputEvent e) {
		if (last == 0) {
			ItemFinder.INSTANCE.sendToServer(new KeyMessage());
			last = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
		}
		if (light.isPressed()
				&& Minecraft.getMinecraft().theWorld.getTotalWorldTime() - last >= 40) {
			ItemFinder.INSTANCE.sendToServer(new KeyMessage());
			last = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
			System.out.println("send");
		}
	}

	// @SubscribeEvent
	// public void key(LivingUpdateEvent e) {
	// if (e.entityLiving.worldObj.isRemote
	// && e.entityLiving instanceof EntityPlayer) {
	// if (light.isKeyDown() && !visible) {
	// visible = true;
	// ItemFinder.INSTANCE.sendToServer(new KeyMessage());
	// } else if (!light.isKeyDown())
	// visible = false;
	// // EntityPlayer player = (EntityPlayer) e.entityLiving;
	// // if (player.getHeldItem() != null
	// // && !player.getHeldItem().toString().equals(last)) {
	// // last = player.getHeldItem().toString();
	// // ItemFinder.INSTANCE.sendToServer(new KeyMessage());
	// // }
	// }
	// }

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		if (!ItemFinder.light.isKeyDown())
			return;
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if (player.getHeldItem() == null)
			return;

		double doubleX = player.lastTickPosX
				+ (player.posX - player.lastTickPosX) * event.partialTicks;
		double doubleY = player.lastTickPosY
				+ (player.posY - player.lastTickPosY) * event.partialTicks;
		double doubleZ = player.lastTickPosZ
				+ (player.posZ - player.lastTickPosZ) * event.partialTicks;
		GlStateManager.pushAttrib();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();

		GlStateManager.pushMatrix();
		GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
		// code
		Color color = (Color) Color.ORANGE;
		for (BlockPos p : lis) {
			float x = p.getX(), y = p.getY(), z = p.getZ();
			RenderHelper.enableStandardItemLighting();
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer renderer = tessellator.getWorldRenderer();
			renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
			GlStateManager.color(color.getRed(), color.getGreen(),
					color.getBlue(), 1f);
			GL11.glLineWidth(2f);
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
			RenderHelper.disableStandardItemLighting();
		}

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.color(1f, 1f, 1f, 1f);

		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}
}
