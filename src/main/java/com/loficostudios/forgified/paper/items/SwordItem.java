package com.loficostudios.forgified.paper.items;

import com.loficostudios.forgified.paper.ForgifiedPaper;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

public class SwordItem extends JItem {

    private final double attackDamage;
    private final double attackSpeed;

    public SwordItem(Material baseMaterial, double attackDamage, double attackSpeed, Properties properties) {
        super(baseMaterial, properties.custom((item) -> {
            var equipmentSlot = EquipmentSlotGroup.MAINHAND;
            var operation = AttributeModifier.Operation.ADD_NUMBER;
            var damageModifier = new AttributeModifier(new NamespacedKey(ForgifiedPaper.NAMESPACE, "attack_damage"), attackDamage, operation, equipmentSlot);
            var speedModifier = new AttributeModifier(new NamespacedKey(ForgifiedPaper.NAMESPACE, "attack_speed"), attackSpeed, operation, equipmentSlot);
            item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ATTACK_DAMAGE, damageModifier)
                    .addModifier(Attribute.ATTACK_SPEED, speedModifier));
        }));
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }
}
