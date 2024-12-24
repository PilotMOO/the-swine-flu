package mod.pilot.the_swine_flu.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.phys.Vec3;

public class PigVerticalLeapGoal extends Goal {
    private final Pig parent;
    private final double range;
    private final double XZScale;
    private final double yScale;
    private final int CD;
    private int ticker;
    public PigVerticalLeapGoal(Pig pig, double range, double XZscale, double yScale, int cooldown){
        this.parent = pig;
        this.range = range;
        this.XZScale = XZscale;
        this.yScale = yScale;
        this.CD = cooldown;
    }

    @Override
    public boolean canUse() {
        return parent.getTarget() != null && Math.abs(parent.getTarget().position().y - parent.position().y) >= 2 && (parent.onGround() || parent.isInWater());
    }
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = parent.getTarget();
        if (target == null) return;

        if (checkHorizontalDistance(target) && ticker++ >= CD){
            Leap(target);
        }
    }
    private boolean checkHorizontalDistance(LivingEntity target) {
        Vec3 pPos = parent.position();
        Vec3 tPos = target.position();
        double flatDistance = (Math.abs(tPos.x - pPos.x) + Math.abs(tPos.z - pPos.z)) / 2;
        return flatDistance < range;
    }

    private void Leap(LivingEntity target) {
        parent.addDeltaMovement(target.position().subtract(parent.position()).normalize()
                .multiply(XZScale, yScale, XZScale));
        stop();
    }

    @Override
    public void stop() {
        ticker = 0;
    }
}
