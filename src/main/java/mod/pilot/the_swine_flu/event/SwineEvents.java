package mod.pilot.the_swine_flu.event;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import mod.pilot.the_swine_flu.TheSwineFlu;
import mod.pilot.the_swine_flu.ai.PigLeapGoal;
import mod.pilot.the_swine_flu.ai.PigVerticalLeapGoal;
import mod.pilot.the_swine_flu.effects.SwineEffects;
import mod.pilot.the_swine_flu.systems.PigAttackManager;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import static mod.pilot.the_swine_flu.systems.PigAttackManager.pigSearchRange;

@Mod.EventBusSubscriber(modid = TheSwineFlu.MOD_ID)
public class SwineEvents {

    @SubscribeEvent
    public static void WeaponizeThePigs(EntityJoinLevelEvent event){
        if (event.getEntity() instanceof Pig pig){
            pig.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(pig, LivingEntity.class, false,
                    PigAttackManager::isNOTBlacklisted){
                @Override
                protected @NotNull AABB getTargetSearchArea(double pTargetDistance) {
                    return this.mob.getBoundingBox().inflate(pigSearchRange, pigSearchRange, pigSearchRange);
                }
            });
            pig.goalSelector.addGoal(2, new MeleeAttackGoal(pig, 1 + pig.level().random.nextDouble(), true){
                @Override
                protected void checkAndPerformAttack(@NotNull LivingEntity pEnemy, double pDistToEnemySqr) {
                    double d0 = this.getAttackReachSqr(pEnemy);
                    if (pDistToEnemySqr <= d0 && this.getTicksUntilNextAttack() <= 0) {
                        this.resetAttackCooldown();
                        this.mob.swing(InteractionHand.MAIN_HAND);
                        PigAttackManager.PigAttack((Pig) this.mob, pEnemy);
                        pEnemy.addEffect(new MobEffectInstance(SwineEffects.SWINE_FLU.get(), 600));
                    }
                }
            });
            pig.goalSelector.addGoal(2, new PigLeapGoal(pig, 16, 1, 0.5, 40));
            pig.goalSelector.addGoal(2, new PigVerticalLeapGoal(pig, 5, 0.25, 0.75, 10));

            pig.goalSelector.addGoal(1, new BreakDoorGoal(pig, 1, (difficulty -> true)){
                @Override
                protected int getDoorBreakTime() {
                    return this.doorBreakTime;
                }
            });
        }
    }
    @SubscribeEvent
    public static void PigConversion(EntityLeaveLevelEvent event){
        Entity target = event.getEntity();
        if (target instanceof Pig || !(target instanceof LivingEntity LE) || !LE.isDeadOrDying() ||
                !LE.hasEffect(SwineEffects.SWINE_FLU.get())) return;

        if (!(target.level() instanceof ServerLevel server)) return;

        Pig pig = EntityType.PIG.create(server);
        if (pig == null) return;
        pig.copyPosition(target);
        server.addFreshEntity(pig);

        server.playSound(null, target.blockPosition(), SoundEvents.PIG_DEATH, SoundSource.HOSTILE, 1f, 2f);
    }

    @SubscribeEvent
    public static void RegisterCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("removePig").executes(arguments ->
                event.getDispatcher().execute("kill @e[type=minecraft:pig]", arguments.getSource())));
    }
}
