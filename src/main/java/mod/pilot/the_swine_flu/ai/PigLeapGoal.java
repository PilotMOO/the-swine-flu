package mod.pilot.the_swine_flu.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Pig;

public class PigLeapGoal extends Goal {
    private final Pig parent;
    private final double range;
    private final double delta;
    private final double yDelta;
    private final int CD;
    private int ticker;
    public PigLeapGoal(Pig pig, double range, double deltaScale, double yVelocity, int cooldown){
        this.parent = pig;
        this.range = range;
        this.delta = deltaScale;
        this.yDelta = yVelocity;
        this.CD = cooldown;
    }

    @Override
    public boolean canUse() {
        return parent.getTarget() != null && (parent.onGround() || parent.isInWater());
    }
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = parent.getTarget();
        if (target == null) return;

        if (parent.distanceTo(target) < range && ticker++ >= CD){
            Leap(target);
        }
    }

    private void Leap(LivingEntity target) {
        parent.addDeltaMovement(target.position().subtract(parent.position()).normalize()
                .multiply(delta, 0, delta)
                .add(0, yDelta, 0));
        stop();
    }

    @Override
    public void stop() {
        ticker = 0;
    }
}
