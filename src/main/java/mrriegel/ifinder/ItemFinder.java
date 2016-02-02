package mrriegel.ifinder;

import java.awt.Color;
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
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@Mod(modid = ItemFinder.MODID, version = ItemFinder.VERSION, name = ItemFinder.MODNAME)
public class ItemFinder {
	public static final String MODID = "ifinder";
	public static final String MODNAME = "ItemFinder";
	public static final String VERSION = "1.0";

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(
			ItemFinder.MODID);

	public static List<BlockPos> lis = new ArrayList<BlockPos>();
	public static long last = 0;

	public static Configuration config;
	public static int range, color;
	public static boolean alwaysOn;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		range = config.get(Configuration.CATEGORY_GENERAL, "range", 16)
				.getInt();
		color = config.get(Configuration.CATEGORY_CLIENT, "color", -1).getInt();
		alwaysOn = config.get(Configuration.CATEGORY_CLIENT, "alwaysOn", false)
				.getBoolean();
		if (config.hasChanged())
			config.save();

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (event.getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(new Client());
			ClientRegistry.registerKeyBinding(Client.light);
		}
		INSTANCE.registerMessage(KeyMessage.class, KeyMessage.class, 0,
				Side.SERVER);
		INSTANCE.registerMessage(SyncMessage.class, SyncMessage.class, 1,
				Side.CLIENT);
	}

	// @SubscribeEvent
	// public void onKey(InputEvent.KeyInputEvent e) {
	// if (last == 0) {
	// ItemFinder.INSTANCE.sendToServer(new KeyMessage());
	// last = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
	// }
	// if (light.isPressed()
	// && Minecraft.getMinecraft().theWorld.getTotalWorldTime() - last >= 30) {
	// ItemFinder.INSTANCE.sendToServer(new KeyMessage());
	// last = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
	// }
	// }

}
