package mod.pilot.the_swine_flu.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SwineFluEffect extends MobEffect {
    protected SwineFluEffect() {
        super(MobEffectCategory.HARMFUL, 15834776);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity target, int amp) {
        if (!(target.level() instanceof ServerLevel server)) return;

        if (target.tickCount % 150 == 0){
            server.playSound(null, target.blockPosition(), SoundEvents.PIG_AMBIENT, SoundSource.HOSTILE,
                    1.0f, (float)server.random.nextInt(5, 21) / 10);
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration > 0;
    }
}
