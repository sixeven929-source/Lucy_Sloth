package com.dayofpi.lucys_sloths.client;

import com.dayofpi.lucys_sloths.LucysSloths;
import com.dayofpi.lucys_sloths.common.SlothEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

/** Dibuja el musgo y la planta que crece sobre el lomo del perezoso. */
public class SlothPlantLayer extends RenderLayer<SlothEntity, SlothModel<SlothEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(LucysSloths.MODID, "textures/entity/sloth/plant_layer.png");

    public SlothPlantLayer(RenderLayerParent<SlothEntity, SlothModel<SlothEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SlothEntity sloth,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        BlockState plant = sloth.getPlantBlock();
        if (sloth.isBaby() || plant == null || sloth.isInvisible()) return;

        // Capa de musgo (misma malla, otra textura)
        renderColoredCutoutModel(this.getParentModel(), TEXTURE, poseStack, buffer, packedLight, sloth, -1);

        // La planta como bloque
        poseStack.pushPose();
        poseStack.scale(-0.6F, -0.6F, 0.6F);
        poseStack.translate(-0.5D, -2.0D, 0.6D);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(plant, poseStack, buffer, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
