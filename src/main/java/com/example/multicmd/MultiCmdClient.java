package com.example.multicmd;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;

public class MultiCmdClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("multicmd");

    private static KeyBinding key;
    private final Queue<Config.Step> queue = new ArrayDeque<>();
    private int delay = 0;
    private boolean running = false;

    @Override
    public void onInitializeClient() {
        Config.load(); // tạo file config mặc định nếu chưa có

        key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.multicmd.run", GLFW.GLFW_KEY_R, "category.multicmd"));

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        while (key.wasPressed()) {
            if (client.player == null) continue;
            if (running) {
                stop(client, "§cMultiCmd: đã huỷ");
            } else {
                start(client);
            }
        }

        if (!running) return;

        // Thoát world / disconnect giữa chừng thì dừng hẳn
        if (client.player == null || client.player.networkHandler == null) {
            queue.clear();
            running = false;
            return;
        }

        if (delay > 0) {
            delay--;
            return;
        }

        Config.Step s = queue.poll();
        if (s == null) {
            stop(client, "§aMultiCmd: xong");
            return;
        }

        client.player.networkHandler.sendChatCommand(s.cmd);
        delay = Math.max(0, s.wait);

        if (queue.isEmpty()) {
            stop(client, "§aMultiCmd: xong");
        }
    }

    private void start(MinecraftClient client) {
        Config config = Config.load(); // đọc lại mỗi lần bấm, sửa file không cần restart game
        queue.clear();
        for (Config.Step s : config.steps) {
            if (s == null || s.cmd == null) continue;
            String cmd = s.cmd.trim();
            while (cmd.startsWith("/")) cmd = cmd.substring(1);
            if (cmd.isEmpty()) continue;
            queue.add(new Config.Step(cmd, s.wait));
        }
        if (queue.isEmpty()) {
            client.player.sendMessage(Text.literal("§cMultiCmd: không có lệnh nào trong config"), true);
            return;
        }
        delay = 0;
        running = true;
        client.player.sendMessage(Text.literal("§eMultiCmd: đang chạy... (bấm lại để huỷ)"), true);
    }

    private void stop(MinecraftClient client, String message) {
        queue.clear();
        delay = 0;
        running = false;
        if (client.player != null) {
            client.player.sendMessage(Text.literal(message), true);
        }
    }
}
