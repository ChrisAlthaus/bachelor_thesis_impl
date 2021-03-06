package gui_control.MovingBarSideModel;

import java.awt.Color;

public class MovingBarSideModel {
	String name;
	Color quantityColor;
	Color referenceColor;
	String mode;
	String scenarioName;
	int referenceValue;
	int stepSize;
	
	//default constructor for first start of application
	public MovingBarSideModel(String name, String mode) {
		this.name = name;
		this.mode=mode;
		this.quantityColor = new Color(255,255,255);
		this.referenceColor = new Color(255,255,255);
		this.scenarioName="Temperature";
		this.referenceValue=0;
		this.stepSize = 10;
	}
	
	

	public MovingBarSideModel(String name, String scenarioName, String quantityColorRGB, 
			String referenceColorRBG, String mode, int referenceValue,int stepSize) {
		
		this.name = name;
		this.scenarioName=scenarioName;
		this.quantityColor = new Color(Integer.parseInt(quantityColorRGB));
		this.referenceColor = new Color(Integer.parseInt(referenceColorRBG));
		this.mode = mode;
		this.referenceValue = referenceValue;
		this.stepSize = stepSize;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getQuantityColor() {
		if(quantityColor == null){
			return new Color(255,255,255);
		}else{
			return quantityColor;
		}
		
	}

	public void setQuantityColor(Color quantityColor) {
		this.quantityColor = quantityColor;
	}

	public Color getReferenceColor() {
		if(referenceColor == null){
			return new Color(255,255,255);
		}else{
			return referenceColor;
		}
	}

	public void setReferenceColor(Color referenceColor) {
		this.referenceColor = referenceColor;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public int getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(int referenceValue) {
		this.referenceValue = referenceValue;
	}

	public int getStepSize() {
		return stepSize;
	}

	public void setStepSize(int stepSize) {
		this.stepSize = stepSize;
	}



	public String getScenarioName() {
		return scenarioName;
	}



	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}
	
	
	public String toString(){
    	return ""+name +","+ quantityColor + "," + referenceColor+ "," +mode + "," + scenarioName  + "," + referenceValue
    			 + "," + stepSize;
    }
	
}
