package mod.pilot.the_swine_flu.event;

import mod.pilot.the_swine_flu.TheSwineFlu;
import mod.pilot.the_swine_flu.effects.SwineEffects;
import mod.pilot.the_swine_flu.systems.PigAttackManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = TheSwineFlu.MOD_ID)
public class SwineEvents {
    @SubscribeEvent
    public static void WeaponizeThePigs(EntityJoinLevelEvent event){
        if (event.getEntity() instanceof Pig pig){
            pig.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(pig, LivingEntity.class, false,
                    PigAttackManager::isNOTBlacklisted));
            pig.goalSelector.addGoal(1, new MeleeAttackGoal(pig, 1 + pig.level().random.nextDouble(), true){
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
}
