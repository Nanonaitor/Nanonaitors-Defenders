package com.vnano.defenders.potion;

import com.vnano.defenders.DefendersMod;
import net.minecraft.potion.Potion;

public final class PotionVulnerable extends Potion {
    public PotionVulnerable() {
        super(true, 0xE34B4B);
        setRegistryName(DefendersMod.MOD_ID, "vulnerable");
        setPotionName("effect.defenders.vulnerable");
        setIconIndex(5, 0);
    }
}
