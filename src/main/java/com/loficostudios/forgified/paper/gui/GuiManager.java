/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @Link https://github.com/Tonierbobcat/MelodyAPI
 * @version 0.1.3
 */

package com.loficostudios.forgified.paper.gui;

import com.loficostudios.forgified.paper.ForgifiedPaper;
import com.loficostudios.forgified.paper.ForgifiedPaperPlugin;
import com.loficostudios.forgified.paper.utils.ChatEditQueueManager;
import io.papermc.paper.adventure.PaperAdventure;

import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GuiManager implements Listener {

    private final Map<UUID, FloralGui> openedMenus = new HashMap<>();
    private final long interval = 125;
    private final ConcurrentHashMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    @Deprecated
    public static GuiManager getInstance() {
        return ForgifiedPaper.getGuiManager();
    }

    public boolean hasCooldownSimple(UUID uuid) {
        var has = hasCooldown(uuid);
        if (!has)
            setCooldown(uuid);
        return has;
    }

    public boolean hasCooldown(UUID uuid) {
        Long last = cooldowns.get(uuid);
        if (last == null) {
            return false;
        }
        if ((System.currentTimeMillis() - last) >= interval) {
            cooldowns.remove(uuid);
            return false;
        }

        return true;
    }

    public void setCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }

    private final ForgifiedPaperPlugin plugin;

    public ForgifiedPaperPlugin getPlugin() {
        return plugin;
    }

    public GuiManager(ForgifiedPaperPlugin plugin) {
        this.plugin = plugin;
    }

    public FloralGui getGui(@NotNull Player player) {
        return this.openedMenus.get(player.getUniqueId());
    }

    public void setGui(@NotNull Player player, @Nullable FloralGui gui) {
        UUID uuid = player.getUniqueId();
        if (gui == null && openedMenus.containsKey(uuid))
            this.openedMenus.remove(uuid);
        this.openedMenus.put(uuid, gui);
    }

    @EventHandler
    protected void onClick(InventoryClickEvent e) {
        if (e.isCancelled())
            return;
        if (!(e.getInventory().getHolder() instanceof FloralGui gui))
            return;
        Player player = (Player) e.getWhoClicked();

//        IGui gui = getGui(player);
//        if (gui == null) {
//            Debug.log("Gui is null");
//            return;
//        }

        var slot = e.getRawSlot();

        if (gui instanceof MutableGui) {
            handleMutableGui(e, ((MutableGui) gui));
            return;
        }
        e.setCancelled(true);
        handleClick(e, player, gui, slot);
    }

    private void handleMutableGui(InventoryClickEvent e, MutableGui gui) {
        Player player = (Player) e.getWhoClicked();

        var slot = e.getRawSlot();

        if (gui.getMutableSlots().contains(slot)) {
//                Debug.log("clicked on item is mutable");
            var action = e.getAction();
            if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                e.setCancelled(true);
//                    var contents = e.getInventory().getContents();
//                    Integer targetSlot = null;
//                    for (int i = 0; i < contents.length; i++) {
//                        if (contents[i] == null || contents[i].getType().equals(Material.AIR)) {
//                            targetSlot = i;
//                            break;
//                        }
//                    }
//                    if (!((MutableGui) gui).getMutableSlots().contains(targetSlot))
//                        e.setCancelled(true);
            }
            return;
        }
        e.setCancelled(true);

//            Debug.log("clicked on item is not mutable");

        handleClick(e, player, gui, slot);
    }

    private void handleClick(InventoryClickEvent e, Player player, FloralGui gui, int slot) {
        var icon = gui.getIcon(slot);

        if (icon == null)
            return;

        if (hasCooldownSimple(player.getUniqueId()))
            return;

        var event = new GuiIconClickEvent(player, gui, icon);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            icon.consume(e);
        }
    }

    private final Set<Player> transitioningPlayers = new HashSet<>();

    @EventHandler
    private void onOpen(GuiOpenEvent e) {
        var player = e.getPlayer();
        setGui(player, e.getGui());

        transitioningPlayers.add(player);
        plugin.runTaskLater(() -> transitioningPlayers.remove(player), 2L);
    }

    private final Set<UUID> popoutTransitions = new HashSet<>();

    private final Set<UUID> activelyUpdating = new HashSet<>();

    @EventHandler
    private void onClose(GuiCloseEvent e) {
        var player = e.getPlayer();
        var gui = e.getGui();
        var uuid = player.getUniqueId();

        setGui(player, null);

//        var guiInQueue = titleUpdateQueues.get(uuid);
//        var guisEqual = guiInQueue != null && guiInQueue.equals(gui);
//
//        if (guisEqual) {
//            titleUpdateQueues.remove(uuid);
//            plugin.runTaskLater(() -> player.openInventory(gui.getInventory()), 1);
//            return;
//        }

        if ((e.getGui() instanceof PopOutGui popout))
            handlePopOutGui(e, popout);
    }

    private void handlePopOutGui(GuiCloseEvent e, PopOutGui gui) {
        if (ChatEditQueueManager.isQueued(e.getPlayer()))
            return;
        plugin.runTaskLater(() -> {
            gui.onClose(e.getPlayer());
        }, 1);
    }
    public boolean isTransitioning(Player player) {
        return transitioningPlayers.contains(player);
    }
    @EventHandler
    private void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof FloralGui gui))
            return;
        if (!(e.getPlayer() instanceof Player player))
            return;
        var uuid = player.getUniqueId();
        if (isTransitioning(player)) {
            return;
        }
        if (activelyUpdating.contains(uuid)) {
            activelyUpdating.remove(uuid);
            return;
        }

        var event = new GuiCloseEvent((player), gui);
        Bukkit.getPluginManager().callEvent(event);
    }

//    private final Map<UUID, FloralGui> titleUpdateQueues = new HashMap<>();

    public void updateTitle(List<HumanEntity> viewers, FloralGui gui, Component title) {

        for (HumanEntity viewer : viewers) {
            if (viewer instanceof Player player) {
                ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                net.minecraft.network.chat.Component nmsTitle = PaperAdventure.asVanilla(title);

                ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(
                        serverPlayer.containerMenu.containerId,
                        getContainerBySlots(gui.getSize()),
                        nmsTitle
                );

                serverPlayer.connection.send(packet);
                serverPlayer.initMenu(serverPlayer.containerMenu);
            }
        }




//        for (HumanEntity viewer : viewers) {
//            if (viewer instanceof Player player) {
//                titleUpdateQueues.put(player.getUniqueId(), gui);
//                player.closeInventory();
//            }
//        }
    }

    private @NotNull MenuType<ChestMenu> getContainerBySlots(int slots) {
        return switch (slots / 9) {
            case 1 -> MenuType.GENERIC_9x1;
            case 2 -> MenuType.GENERIC_9x2;
            case 3 -> MenuType.GENERIC_9x3;
            case 4 -> MenuType.GENERIC_9x4;
            case 5 -> MenuType.GENERIC_9x5;
            case 6 -> MenuType.GENERIC_9x6;
            default ->
                    throw new IllegalArgumentException();
        };
    }
}