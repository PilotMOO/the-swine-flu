package mod.pilot.the_swine_flu;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = TheSwineFlu.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    public static class Server{
        public final ForgeConfigSpec.ConfigValue<Double> pig_damage;
        public final ForgeConfigSpec.ConfigValue<Double> pig_kb;
        public final ForgeConfigSpec.ConfigValue<Integer> pig_searchrange;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklisted_targets;


        public Server(ForgeConfigSpec.Builder builder){
            builder.push("The Swine Flu Config");

            builder.push("Mob Targeting");
            this.blacklisted_targets = builder.defineList("Mobs the Pigs Ignore",
                    Lists.newArrayList(
                            "minecraft:pig","minecraft:bat","minecraft:armor_stand") , o -> o instanceof String);
            builder.pop();

            builder.push("Pig attack stats");
            this.pig_damage = builder.defineInRange("How much damage pigs deal", 4, 0, Double.MAX_VALUE);
            this.pig_kb = builder.defineInRange("How much knockback pigs deal", 0.25, 0, Double.MAX_VALUE);
            this.pig_searchrange = builder.defineInRange("How far away pigs can target and follow", 64, 0, Integer.MAX_VALUE);
        }
    }


    static {
        Pair<Server, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER = commonSpecPair.getLeft();
        SERVER_SPEC = commonSpecPair.getRight();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }
}
