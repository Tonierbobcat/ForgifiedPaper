/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @Link https://github.com/Tonierbobcat/MelodyAPI
 * @version 0.1.3
 */

package com.loficostudios.forgified.paper.gui;

import com.loficostudios.forgified.paper.ForgifiedPaper;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
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
    private final long interval = 250;
    private final ConcurrentHashMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();

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

    private static GuiManager instance;

    public static GuiManager getInstance() {
        return instance;
    }

    private final ForgifiedPaper plugin;

    public ForgifiedPaper getPlugin() {
        return plugin;
    }

    public GuiManager(ForgifiedPaper plugin) {
        Validate.isTrue(instance == null);
        instance = this;
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
            icon.onClick(e);
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

    @EventHandler
    private void onClose(GuiCloseEvent e) {
        var player = e.getPlayer();
        setGui(player, null);
        if ((e.getGui() instanceof PopOutGui gui))
            handlePopOutGui(e, gui);
    }

    private void handlePopOutGui(GuiCloseEvent e, PopOutGui gui) {
        plugin.runTaskLater(() -> gui.onClose(e.getPlayer()), 1);
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
        if (isTransitioning(player)) {
            return;
        }

        var event = new GuiCloseEvent((player), gui);
        Bukkit.getPluginManager().callEvent(event);
    }
}