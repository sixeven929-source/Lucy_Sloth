package com.dayofpi.lucys_sloths.common;

import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;

/** Las crías se suben al lomo de un adulto cercano. */
public class ClimbParentGoal extends Goal {
    private final SlothEntity animal;
    private SlothEntity parent;
    private int delay;

    public ClimbParentGoal(SlothEntity animal) {
        this.animal = animal;
    }

    @Override
    public boolean canUse() {
        if (this.animal.getAge() >= 0) return false;
        if (this.animal.getRandom().nextInt(this.reducedTickDelay(120)) != 0) return false;
        if (this.animal.isPassenger()) return false;

        List<SlothEntity> list = this.animal.level().getEntitiesOfClass(SlothEntity.class,
                this.animal.getBoundingBox().inflate(8.0, 4.0, 8.0));
        SlothEntity closest = null;
        double best = Double.MAX_VALUE;
        for (SlothEntity other : list) {
            if (other.getAge() < 0 || other == this.animal) continue;
            double d = this.animal.distanceToSqr(other);
            if (d < best) {
                best = d;
                closest = other;
            }
        }
        if (closest == null || closest.getPlantBlock() != null) return false;
        if (best > 2.0) return false;
        this.parent = closest;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.animal.getAge() >= 0) return false;
        if (this.animal.isPassenger()) return false;
        if (this.parent != null && !this.parent.isAlive()) return false;
        return this.animal.distanceToSqr(this.parent) <= 2.0;
    }

    @Override
    public void start() {
        this.delay = 0;
    }

    @Override
    public void stop() {
        this.parent = null;
    }

    @Override
    public void tick() {
        if (--this.delay > 0) return;
        this.delay = this.adjustedTickDelay(10);
        this.animal.getNavigation().moveTo(this.parent, 1.0);
        if (this.animal.distanceToSqr(this.parent) < 1.0) {
            this.animal.startRiding(this.parent);
        }
    }
}
