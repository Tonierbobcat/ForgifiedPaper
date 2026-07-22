package com.loficostudios.forgified.paper.items.properties;

import org.bukkit.potion.PotionEffect;

import java.util.List;

public class Consumable {
    int nutrition;
    int saturation;
    boolean canAlwaysEat;
    float consumeSeconds;

    List<Effects> effects;

    public Consumable(int nutrition, int saturation, boolean canAlwaysEat, float consumeSeconds, List<Effects> effects) {
        this.nutrition = nutrition;
        this.saturation = saturation;
        this.canAlwaysEat = canAlwaysEat;
        this.consumeSeconds = consumeSeconds;
        this.effects = effects;
    }

    public List<Effects> getEffects() {
        return effects;
    }

    public int getNutrition() {
        return nutrition;
    }

    public int getSaturation() {
        return saturation;
    }

    public boolean isCanAlwaysEat() {
        return canAlwaysEat;
    }

    public float getConsumeSeconds() {
        return consumeSeconds;
    }

    public record Effects(PotionEffect effect, float probability) {
    }
}
