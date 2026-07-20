package com.loficostudios.forgified.paper;

import com.loficostudios.forgified.paper.gui.GuiManager;
import com.loficostudios.forgified.paper.nametags.NametagManager;
import com.loficostudios.forgified.paper.utils.ChatEditQueueManager;

public class ForgifiedPaper {
    private static ForgifiedPaperPlugin plugin;

    private static boolean initialized;

    public static void init(ForgifiedPaperPlugin plugin) {
        if (initialized)
            throw new IllegalArgumentException();
        ForgifiedPaper.plugin = plugin;
        initialized = true;
    }

    public static NametagManager getNametagManager() {
        return plugin.getNametagManager();
    }

    public static GuiManager getGuiManager() {
        return plugin.getGuiManager();
    }

    public static ChatEditQueueManager getChatEditQueueManager() {
        return plugin.getChatEditQueueManager();
    }
}
