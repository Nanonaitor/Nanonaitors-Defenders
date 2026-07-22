package com.vnano.defenders.registry;
import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.effect.VulnerableEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.*;
public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS=DeferredRegister.create(ForgeRegistries.MOB_EFFECTS,DefendersMod.MOD_ID);
    public static final RegistryObject<MobEffect> VULNERABLE=EFFECTS.register("vulnerable",VulnerableEffect::new);
    private ModEffects(){}
}
