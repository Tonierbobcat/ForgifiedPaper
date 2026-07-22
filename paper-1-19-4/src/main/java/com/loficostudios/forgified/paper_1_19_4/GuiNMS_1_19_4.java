package com.loficostudios.forgified.paper_1_19_4;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.loficostudios.forgified.paper.gui.FloralGui;
import com.loficostudios.forgified.paper.gui.GuiNMS;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class GuiNMS_1_19_4 implements GuiNMS {

    @Override
    public void updateTitle(List<HumanEntity> viewers, FloralGui gui, Component title) {
        for (HumanEntity viewer : viewers) {
            if (viewer instanceof Player player) {
                // Safely grab the active container ID via PacketEvents

                int containerId = getActiveContainerId(player);

                // Fetch the integer ID for the specific chest size
                int windowType = getWindowTypeBySlots(gui.getSize());

                // Construct the window opening packet wrapper
                WrapperPlayServerOpenWindow packet = new WrapperPlayServerOpenWindow(
                        containerId,
                        windowType,
                        title
                );

                // Send the packet out
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);

                // Forces client side inventory synchronization sync
                player.updateInventory();
            }
        }
    }

    /**
     * Maps slot counts to the standard Minecraft protocol window type IDs.
     * 0 to 5 represent Generic 9x1 through 9x6 containers.
     */
    private int getWindowTypeBySlots(int slots) {
        return switch (slots / 9) {
            case 1 -> 0; // Generic 9x1
            case 2 -> 1; // Generic 9x2
            case 3 -> 2; // Generic 9x3
            case 4 -> 3; // Generic 9x4
            case 5 -> 4; // Generic 9x5
            case 6 -> 5; // Generic 9x6
            default -> throw new IllegalArgumentException("Invalid inventory slot sizing: " + slots);
        };
    }

    private int getActiveContainerId(Player player) {
        try {
            // Get net.minecraft.server.level.ServerPlayer
            Method getHandle = player.getClass().getMethod("getHandle");
            Object serverPlayer = getHandle.invoke(player);

            // Get the active containerMenu field
            Field containerMenuField = serverPlayer.getClass().getField("containerMenu");
            Object containerMenu = containerMenuField.get(serverPlayer);

            // Get the containerId integer
            Field containerIdField = containerMenu.getClass().getField("containerId");
            return containerIdField.getInt(containerMenu);
        } catch (Exception e) {
            // Safe fallback rule: window ID 0 is the player inventory,
            // 1 is almost always the active top open container screen.
            return 1;
        }
    }

//    @Override
//    public void updateTitle(List<HumanEntity> viewers, FloralGui gui, Component title) {
//
//        for (HumanEntity viewer : viewers) {
//            if (viewer instanceof Player player) {
//                ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
//                net.minecraft.network.chat.Component nmsTitle = PaperAdventure.asVanilla(title);
//
//                WrapperPlayServerOpenScreen
//
//                ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(
//                        serverPlayer.containerMenu.containerId,
//                        getContainerBySlots(gui.getSize()),
//                        nmsTitle
//                );
//
//                serverPlayer.connection.send(packet);
//                serverPlayer.initMenu(serverPlayer.containerMenu);
//            }
//        }
//    }
//
//    private @NotNull MenuType<ChestMenu> getContainerBySlots ( int slots){
//        return switch (slots / 9) {
//            case 1 -> MenuType.GENERIC_9x1;
//            case 2 -> MenuType.GENERIC_9x2;
//            case 3 -> MenuType.GENERIC_9x3;
//            case 4 -> MenuType.GENERIC_9x4;
//            case 5 -> MenuType.GENERIC_9x5;
//            case 6 -> MenuType.GENERIC_9x6;
//            default -> throw new IllegalArgumentException();
//        };
//    }
}
