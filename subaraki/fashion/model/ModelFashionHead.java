package subaraki.fashion.model;

public class ModelFashionHead extends ModelFashion{

	public ModelFashionHead(float modelSize) {
		super(modelSize, 64, 16, false);

		this.bipedBody.showModel = false;
		this.bipedRightArm.showModel = false;
		this.bipedLeftArm.showModel = false;
		this.bipedRightLeg.showModel = false;
		this.bipedLeftLeg.showModel = false;
		
		this.bipedBodyWear.showModel = false;
		this.bipedRightArmwear.showModel = false;
		this.bipedLeftArmwear.showModel = false;
		this.bipedRightLegwear.showModel = false;
		this.bipedLeftLegwear.showModel = false;
	}
}