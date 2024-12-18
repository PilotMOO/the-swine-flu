package mod.pilot.the_swine_flu;

import mod.pilot.the_swine_flu.effects.SwineEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(TheSwineFlu.MOD_ID)
public class TheSwineFlu
{
    public static final String MOD_ID = "the_swine_flu";
    public TheSwineFlu()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);

        SwineEffects.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_SPEC, "swine_flu_config.toml");
        Config.loadConfig(Config.SERVER_SPEC, FMLPaths.CONFIGDIR.get().resolve("swine_flu_config.toml").toString());
    }
}
