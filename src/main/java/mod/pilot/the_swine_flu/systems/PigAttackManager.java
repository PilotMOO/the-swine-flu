package mod.pilot.the_swine_flu.systems;

import mod.pilot.the_swine_flu.Config;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;

public class PigAttackManager {
    private static final double pigDamage = Config.SERVER.pig_damage.get();
    private static final double pigKB = Config.SERVER.pig_kb.get();
    public static boolean PigAttack(Pig pig, Entity target){
        float f = (float)pigDamage;
        float f1 = (float)pigKB;
        if (target instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(pig.getMainHandItem(), ((LivingEntity)target).getMobType());
            f1 += (float)EnchantmentHelper.getKnockbackBonus(pig);
        }

        int i = EnchantmentHelper.getFireAspect(pig);
        if (i > 0) {
            target.setSecondsOnFire(i * 4);
        }

        boolean flag = target.hurt(pig.damageSources().mobAttack(pig), f);
        if (flag) {
            if (f1 > 0.0F && target instanceof LivingEntity) {
                ((LivingEntity)target).knockback((double)(f1 * 0.5F), (double) Mth.sin(pig.getYRot() * ((float)Math.PI / 180F)),
                        (double)(-Mth.cos(pig.getYRot() * ((float)Math.PI / 180F))));
                pig.setDeltaMovement(pig.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }

            if (target instanceof Player player) {
                if (!pig.getMainHandItem().isEmpty() && !player.getUseItem().isEmpty()
                        && pig.getMainHandItem().getItem() instanceof AxeItem && player.getUseItem().is(Items.SHIELD)) {
                    float block = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(pig) * 0.05F;
                    if (pig.level().random.nextFloat() < block) {
                        player.getCooldowns().addCooldown(Items.SHIELD, 100);
                        pig.level().broadcastEntityEvent(player, (byte)30);
                    }
                }
            }

            pig.doEnchantDamageEffects(pig, target);
            pig.setLastHurtMob(target);
        }

        return flag;
    }

    private static final ArrayList<String> blacklist = new ArrayList<>(Config.SERVER.blacklisted_targets.get());
    public static boolean isNOTBlacklisted(String ID){
        boolean flag = false;
        for (String s : blacklist){
            if (flag) break;
            if (s.endsWith(":")) {
                flag = ID.split(":")[0].equals(s.replace(":", ""));
            }
            else{
                flag = s.equals(ID);
            }
        }

        return !flag;
    }
    public static boolean isNOTBlacklisted(LivingEntity target){
        return isNOTBlacklisted(target.getEncodeId());
    }
}
