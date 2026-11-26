package com.dayofpi.lucys_sloths.client;

import com.dayofpi.lucys_sloths.LucysSloths;
import com.dayofpi.lucys_sloths.common.SlothEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class SlothModel<T extends SlothEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(LucysSloths.MODID, "sloth"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart snout;

    public SlothModel(ModelPart root) {
        this.root = root.getChild("root");
        ModelPart body = this.root.getChild("body");
        this.head = body.getChild("head");
        this.snout = this.head.getChild("snout");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition meshRoot = mesh.getRoot();
        CubeDeformation none = new CubeDeformation(0.0F);

        PartDefinition root = meshRoot.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 0)
                .addBox(-2.5F, -2.0F, 2.5F, 5.0F, 4.0F, 6.0F, none), PartPose.offset(2.5F, -2.0F, 5.5F));
        PartDefinition leftToes = leftLeg.addOrReplaceChild("left_toes", CubeListBuilder.create(), PartPose.offset(-2.5F, 0.0F, 5.5F));
        leftToes.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0)
                .addBox(13.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, none),
                PartPose.offsetAndRotation(0.0F, 0.0F, -11.0F, -1.5708F, -1.5708F, 0.0F));

        PartDefinition rightLeg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 0)
                .addBox(-2.5F, -2.0F, 2.5F, 5.0F, 4.0F, 6.0F, none), PartPose.offset(-2.5F, -2.0F, 5.5F));
        PartDefinition rightToes = rightLeg.addOrReplaceChild("right_toes", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        rightToes.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0)
                .addBox(13.0F, -5.0F, -2.0F, 3.0F, 5.0F, 0.0F, none),
                PartPose.offsetAndRotation(2.5F, 2.0F, -5.5F, -1.5708F, -1.5708F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-5.0F, -3.0F, -11.0F, 10.0F, 6.0F, 12.0F, none), PartPose.offset(0.0F, -3.0F, 7.0F));

        PartDefinition rightArm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(30, 23).mirror()
                .addBox(-7.75F, -2.0F, -2.5F, 10.0F, 4.0F, 5.0F, none).mirror(false), PartPose.offset(-7.25F, 1.0F, -9.5F));
        rightArm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).mirror()
                .addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 0.0F, none).mirror(false),
                PartPose.offsetAndRotation(-9.25F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition leftArm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(30, 23)
                .addBox(-2.25F, -2.0F, -2.5F, 10.0F, 4.0F, 5.0F, none), PartPose.offset(7.25F, 1.0F, -9.5F));
        leftArm.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 0.0F, none),
                PartPose.offsetAndRotation(9.25F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 18)
                .addBox(-4.0F, -4.5F, -6.5F, 8.0F, 7.0F, 7.0F, none), PartPose.offset(0.0F, 0.5F, -11.5F));
        head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(54, 0)
                .addBox(-2.0F, 0.5F, -7.5F, 4.0F, 2.0F, 1.0F, none), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.crawlingAnimationState, SlothAnimations.CRAWLING, ageInTicks);
        this.animate(entity.swimmingAnimationState, SlothAnimations.SWIMMING, ageInTicks);
        this.animate(entity.idlingAnimationState, SlothAnimations.IDLING, ageInTicks);
        this.animate(entity.idlingInWaterAnimationState, SlothAnimations.IDLING_IN_WATER, ageInTicks);
        this.animate(entity.sittingAnimationState, SlothAnimations.SITTING, ageInTicks);

        // El hocico solo se ve en el perezoso de dos dedos
        this.snout.visible = entity.getSlothType().equals(SlothEntity.TWO_TOED);

        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        if (entity.isBaby()) {
            this.head.xScale = 2.0F;
            this.head.yScale = 2.0F;
            this.head.zScale = 2.0F;
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
