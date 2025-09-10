package com.loficostudios.forgified.paper.items.armor;

import org.bukkit.inventory.EquipmentSlot;

public interface ArmorMaterial {

    int getDurabilityForSlot(EquipmentSlot slot);

    int getDefenseForSlot(EquipmentSlot slot);

    float getToughness();

    float getKnockbackResistance();

    enum Vanilla implements ArmorMaterial {
        LEATHER(5,  new int[]{1, 2, 3, 1}, 0.0f, 0.0f),
        CHAINMAIL(15, new int[]{2, 5, 6, 2}, 0.0f, 0.0f),
        IRON(15, new int[]{2, 5, 6, 2}, 0.0f, 0.0f),
        GOLD(7, new int[]{2, 5, 6, 2}, 0.0f, 0.0f),
        DIAMOND(33, new int[]{3, 6, 8, 3}, 2.0f, 0.0f),
        NETHERITE(37, new int[]{3, 6, 8, 3}, 3.0f, 0.1f);

        private static final int[] DURABILITY_PER_SLOT = new int[]{13, 15, 16, 11};

        private final int durabilityMultiplier;
        private final int[] slotProtections;
        private final float toughness;
        private final float knockbackResistance;

        Vanilla(int durabilityMultiplier, int[] slotProtections, float toughness, float knockbackResistance) {
            this.durabilityMultiplier = durabilityMultiplier;
            this.slotProtections = slotProtections;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
        }

        @Override
        public int getDurabilityForSlot(EquipmentSlot slot) {
            return DURABILITY_PER_SLOT[getSlotIndex(slot)] * this.durabilityMultiplier;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot slot) {
            return this.slotProtections[getSlotIndex(slot)];
        }

        @Override
        public float getToughness() {
            return toughness;
        }

        @Override
        public float getKnockbackResistance() {
            return knockbackResistance;
        }

        private int getSlotIndex(EquipmentSlot slot) {
            int index;
            switch (slot) {
                case HEAD -> index = 3;
                case CHEST -> index = 2;
                case LEGS -> index = 1;
                case FEET -> index = 0;
                default -> throw new IllegalArgumentException("Invalid EquipmentSlot");
            }
            return index;
        }
    }
}
