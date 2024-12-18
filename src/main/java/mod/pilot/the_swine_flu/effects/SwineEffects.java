package mod.pilot.the_swine_flu.effects;

import mod.pilot.the_swine_flu.TheSwineFlu;
import net.minecraft.world.effect.MobEffect;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SwineEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TheSwineFlu.MOD_ID);

    public static final RegistryObject<MobEffect> SWINE_FLU = MOB_EFFECTS.register("swine_flu",
            SwineFluEffect::new);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
