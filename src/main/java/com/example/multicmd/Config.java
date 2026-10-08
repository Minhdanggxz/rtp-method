package com.example.multicmd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Cấu hình lưu ở .minecraft/config/multicmd.json */
public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public List<Step> steps = defaultSteps();

    public static class Step {
        /** Lệnh, không cần dấu / ở đầu. */
        public String cmd;
        /** Số tick chờ sau khi gửi lệnh này (20 tick = 1 giây). */
        public int wait;

        public Step() {}

        public Step(String cmd, int wait) {
            this.cmd = cmd;
            this.wait = wait;
        }
    }

    private static List<Step> defaultSteps() {
        List<Step> list = new ArrayList<>();
        list.add(new Step("delhome 2", 10));
        list.add(new Step("sethome 2", 10));
        list.add(new Step("rtp", 30));
        list.add(new Step("home 2", 0));
        return list;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("multicmd.json");
    }

    public static Config load() {
        Path p = path();
        if (Files.exists(p)) {
            try (Reader r = Files.newBufferedReader(p)) {
                Config c = GSON.fromJson(r, Config.class);
                if (c != null && c.steps != null && !c.steps.isEmpty()) {
                    return c;
                }
            } catch (Exception e) {
                MultiCmdClient.LOGGER.warn("Không đọc được multicmd.json, dùng cấu hình mặc định", e);
            }
        }
        Config c = new Config();
        c.save();
        return c;
    }

    public void save() {
        try (Writer w = Files.newBufferedWriter(path())) {
            GSON.toJson(this, w);
        } catch (Exception e) {
            MultiCmdClient.LOGGER.warn("Không ghi được multicmd.json", e);
        }
    }
                 }
