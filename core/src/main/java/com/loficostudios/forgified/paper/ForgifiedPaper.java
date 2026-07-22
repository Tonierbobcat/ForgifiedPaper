package com.loficostudios.forgified.paper;

import com.loficostudios.forgified.paper.gui.GuiManager;
import com.loficostudios.forgified.paper.gui.GuiNMS;
import com.loficostudios.forgified.paper.items.JItem;
import com.loficostudios.forgified.paper.nametags.NameTagNMS;
import com.loficostudios.forgified.paper.nametags.NametagManager;
import com.loficostudios.forgified.paper.utils.ChatEditQueueManager;
import com.loficostudios.forgified.paper.utils.VersionHandler;

public class ForgifiedPaper {
    public static final String NAMESPACE = "forgifiedpaper";
    public static VersionHandler<JItem.Properties> PROPERTIES = null;
    public static VersionHandler<GuiNMS> GUI = null;
    public static VersionHandler<NameTagNMS> NAMETAG = null;

    private static ForgifiedPaperPlugin plugin;

    private static boolean initialized;

    public static void init(ForgifiedPaperPlugin plugin) {
        if (initialized)
            throw new IllegalArgumentException();
        ForgifiedPaper.plugin = plugin;

        GUI = new VersionHandler<>(
                new VersionHandler.VersionTarget("com.loficostudios.forgified.paper_1_19_4.GuiNMS_1_19_4", VersionHandler.VersionTarget.Type.GREATER_THAN_EQUALS, 1,19,4)
        );

        NAMETAG = new VersionHandler<>(
                new VersionHandler.VersionTarget("com.loficostudios.forgified.paper_1_19_4.NameTagNMS_1_19_4", VersionHandler.VersionTarget.Type.GREATER_THAN_EQUALS, 1,20,6)
        );

        PROPERTIES = new VersionHandler<>(
                new VersionHandler.VersionTarget("com.loficostudios.forgified.paper_1_20_6.Properties1_20_6", VersionHandler.VersionTarget.Type.EQUALS, 1,20,6)
        );

        GUI.init();
        PROPERTIES.init();
        NAMETAG.init();

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
