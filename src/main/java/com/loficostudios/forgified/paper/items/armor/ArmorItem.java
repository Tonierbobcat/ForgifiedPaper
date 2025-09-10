package com.loficostudios.forgified.paper.items.armor;

import com.loficostudios.forgified.paper.ForgifiedPaper;
import com.loficostudios.forgified.paper.items.JItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;


@SuppressWarnings("UnstableApiUsage")
public class ArmorItem extends JItem {
    private final ArmorMaterial armorMaterial;
    private final EquipmentSlot slot;

    public ArmorItem(EquipmentSlot slot, ArmorMaterial armorMaterial) {
        super(getMaterial(armorMaterial, slot), new Properties()
                .custom(item -> {
                    var armor = new AttributeModifier(new NamespacedKey(ForgifiedPaper.NAMESPACE, "armor"), armorMaterial.getDefenseForSlot(slot), AttributeModifier.Operation.ADD_NUMBER);
                    var armorToughness = new AttributeModifier(new NamespacedKey(ForgifiedPaper.NAMESPACE, "armor_toughness"), armorMaterial.getToughness(), AttributeModifier.Operation.ADD_NUMBER);
                    var knockBack = new AttributeModifier(new NamespacedKey(ForgifiedPaper.NAMESPACE, "knockback"), armorMaterial.getKnockbackResistance(), AttributeModifier.Operation.ADD_NUMBER);

                    item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.KNOCKBACK_RESISTANCE, knockBack)
                            .addModifier(Attribute.ARMOR_TOUGHNESS, armorToughness)
                            .addModifier(Attribute.ARMOR, armor).build());
                })
                .durability(armorMaterial.getDurabilityForSlot(slot)));
        this.armorMaterial = armorMaterial;
        this.slot = slot;
    }

    public ArmorMaterial getArmorMaterial() {
        return armorMaterial;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    /// THIS CODE IS SO SHIT I WILL FIX LATER
    private static Material getMaterial(ArmorMaterial material, EquipmentSlot slot) {
        if (material instanceof ArmorMaterial.Vanilla vanillaMaterial) {
            return switch (slot) {
                case HEAD -> switch (vanillaMaterial) {
                    case LEATHER -> Material.LEATHER_HELMET;
                    case CHAINMAIL -> Material.CHAINMAIL_HELMET;
                    case IRON -> Material.IRON_HELMET;
                    case GOLD -> Material.GOLDEN_HELMET;
                    case DIAMOND -> Material.DIAMOND_HELMET;
                    case NETHERITE -> Material.NETHERITE_HELMET;
                };
                case CHEST -> switch (vanillaMaterial) {
                    case LEATHER -> Material.LEATHER_CHESTPLATE;
                    case CHAINMAIL -> Material.CHAINMAIL_CHESTPLATE;
                    case IRON -> Material.IRON_CHESTPLATE;
                    case GOLD -> Material.GOLDEN_CHESTPLATE;
                    case DIAMOND -> Material.DIAMOND_CHESTPLATE;
                    case NETHERITE -> Material.NETHERITE_CHESTPLATE;
                };
                case LEGS -> switch (vanillaMaterial) {
                    case LEATHER -> Material.LEATHER_LEGGINGS;
                    case CHAINMAIL -> Material.CHAINMAIL_LEGGINGS;
                    case IRON -> Material.IRON_LEGGINGS;
                    case GOLD -> Material.GOLDEN_LEGGINGS;
                    case DIAMOND -> Material.DIAMOND_LEGGINGS;
                    case NETHERITE -> Material.NETHERITE_LEGGINGS;
                };
                case FEET -> switch (vanillaMaterial) {
                    case LEATHER -> Material.LEATHER_BOOTS;
                    case CHAINMAIL -> Material.CHAINMAIL_BOOTS;
                    case IRON -> Material.IRON_BOOTS;
                    case GOLD -> Material.GOLDEN_BOOTS;
                    case DIAMOND -> Material.DIAMOND_BOOTS;
                    case NETHERITE -> Material.NETHERITE_BOOTS;
                };
                default -> throw new IllegalArgumentException("Invalid EquipmentSlot: " + slot);
            };
        } else {
            /// DEFAULT IRON ARMOR
            return switch (slot) {
                case HEAD -> Material.IRON_HELMET;
                case CHEST -> Material.IRON_CHESTPLATE;
                case LEGS -> Material.IRON_LEGGINGS;
                case FEET -> Material.IRON_BOOTS;
                default -> throw new IllegalArgumentException("Invalid EquipmentSlot: " + slot);
            };
        }
    }

}
