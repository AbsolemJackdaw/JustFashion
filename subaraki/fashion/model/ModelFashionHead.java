package subaraki.fashion.model;

public class ModelFashionHead extends ModelFashion{

	public ModelFashionHead(float modelSize) {
		super(modelSize, 0.0f, 64, 16);

		this.bipedBody.showModel = false;
		this.bipedRightArm.showModel = false;
		this.bipedLeftArm.showModel = false;
		this.bipedRightLeg.showModel = false;
		this.bipedLeftLeg.showModel = false;
	}
}