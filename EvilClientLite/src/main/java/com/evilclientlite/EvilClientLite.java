\
package com.evilclientlite;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class EvilClientLite implements ClientModInitializer {

    private static KeyBinding toggleSprintKey;
    private static KeyBinding zoomKey;
    private static boolean toggleSprintEnabled = false;
    private static double originalFov = -1;

    // keystroke tracking
    private static final int[] KEYS = new int[] {GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_LEFT_SHIFT};
    private static final String[] KEY_NAMES = new String[] {"W","A","S","D","SPACE","SHIFT"};

    @Override
    public void onInitializeClient() {
        toggleSprintKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.evilclientlite.togglesprint",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.evilclientlite.keys"
        ));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.evilclientlite.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.evilclientlite.keys"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleSprintKey.wasPressed()) {
                toggleSprintEnabled = !toggleSprintEnabled;
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("ToggleSprint: " + (toggleSprintEnabled ? "ON" : "OFF")), true);
                }
            }
            if (client.player != null) {
                if (toggleSprintEnabled && client.player.input != null) {
                    client.player.setSprinting(true);
                }
            }

            // Zoom: change FOV while key held
            if (MinecraftClient.getInstance().options != null) {
                if (zoomKey.isPressed()) {
                    if (originalFov < 0) {
                        originalFov = MinecraftClient.getInstance().options.getFov().getValue();
                    }
                    MinecraftClient.getInstance().options.getFov().setValue(30.0D);
                } else {
                    if (originalFov >= 0) {
                        MinecraftClient.getInstance().options.getFov().setValue(originalFov);
                        originalFov = -1;
                    }
                }
            }
        });

        HudRenderCallback.EVENT.register(EvilClientLite::onHudRender);
    }

    private static void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getWindow() == null) return;

        int x = 8;
        int y = 8;

        // Simple FPS display
        String fpsText = "FPS: " + mc.getCurrentFps();
        context.drawText(mc.textRenderer, fpsText, x, y, 0xFFFFFF, true);
        y += 12;

        // ToggleSprint status
        String ts = "ToggleSprint: " + (toggleSprintEnabled ? "ON" : "OFF");
        context.drawText(mc.textRenderer, ts, x, y, toggleSprintEnabled ? 0x00FF00 : 0xFF5555, true);
        y += 12;

        // Keystrokes
        int keyX = x;
        int keyY = y;
        for (int i = 0; i < KEYS.length; i++) {
            boolean down = InputUtil.isKeyPressed(mc.getWindow().getHandle(), KEYS[i]);
            String name = KEY_NAMES[i];
            int color = down ? 0x00FF00 : 0xAAAAAA;
            context.drawText(mc.textRenderer, "[" + name + "]", keyX, keyY, color, true);
            keyX += mc.textRenderer.getWidth("[" + name + "]") + 6;
            if (i == 3) { // wrap after D
                keyX = x;
                keyY += 12;
            }
        }
    }
}
