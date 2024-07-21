package com.tom.peripherals;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import com.tom.peripherals.client.KeyboardScreen;
import com.tom.peripherals.client.MonitorBlockEntityRenderer;

public class Client {

	public static void preInit(ModContainer mc, IEventBus bus) {
		bus.addListener(Client::registerScreens);

		/*try {
			mc.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		} catch (Throwable e) {
		}*/
	}

	private static void registerScreens(RegisterMenuScreensEvent e) {
		e.register(Content.keyboardMenu.get(), KeyboardScreen::new);
	}

	public static void setup() {
		BlockEntityRenderers.register(Content.monitorBE.get(), MonitorBlockEntityRenderer::new);

		ItemProperties.register(Content.portableKeyboard.get(), ResourceLocation.tryBuild(PeripheralsMod.ID, "portable_keyboard"), (stack, level, player, p_174643_) -> {
			Boolean in = stack.get(Content.inUseComponent.get());
			return in != null && in ? 1F : 0F;
		});
	}
}
