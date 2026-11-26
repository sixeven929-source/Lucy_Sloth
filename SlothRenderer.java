package com.dayofpi.lucys_sloths.client;

import com.dayofpi.lucys_sloths.LucysSloths;
import com.dayofpi.lucys_sloths.common.SlothEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SlothRenderer extends MobRenderer<SlothEntity, SlothModel<SlothEntity>> {
    private static final ResourceLocation TWO_TOED =
            ResourceLocation.fromNamespaceAndPath(LucysSloths.MODID, "textures/entity/sloth/two_toed.png");
    private static final ResourceLocation THREE_TOED =
            ResourceLocation.fromNamespaceAndPath(LucysSloths.MODID, "textures/entity/sloth/three_toed.png");

    public SlothRenderer(EntityRendererProvider.Context context) {
        super(context, new SlothModel<>(context.bakeLayer(SlothModel.LAYER)), 0.7F);
        this.addLayer(new SlothPlantLayer(this));
    }

    @Override
    protected void scale(SlothEntity sloth, PoseStack poseStack, float partialTick) {
        if (sloth.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
        if (sloth.getSlothType().equals(SlothEntity.TWO_TOED)) {
            poseStack.scale(0.8F, 0.8F, 0.8F);
        }
        if (sloth.getVehicle() instanceof Player) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        }
        super.scale(sloth, poseStack, partialTick);
    }

    @Override
    public ResourceLocation getTextureLocation(SlothEntity sloth) {
        return sloth.getSlothType().equals(SlothEntity.TWO_TOED) ? TWO_TOED : THREE_TOED;
    }
}
