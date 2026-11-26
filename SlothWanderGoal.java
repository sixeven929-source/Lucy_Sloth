package com.dayofpi.lucys_sloths.common;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SlothWanderGoal extends WaterAvoidingRandomStrollGoal {
    private final float chance;

    public SlothWanderGoal(PathfinderMob mob, double speed) {
        this(mob, speed, 0.001F);
    }

    public SlothWanderGoal(PathfinderMob mob, double speed, float chance) {
        super(mob, speed, chance);
        this.chance = chance;
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        if (this.mob.isInWater()) {
            Vec3 pos = LandRandomPos.getPos(this.mob, 15, 15);
            return pos == null ? super.getPosition() : pos;
        }
        if (this.mob.getRandom().nextFloat() >= this.chance) {
            return LandRandomPos.getPos(this.mob, 15, 15);
        }
        return super.getPosition();
    }
}
