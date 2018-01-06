package gui_control.DataHandler;

import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import gui_control.Database.Database;
import gui_control.MovingBarSideModel.MovingBarSideModel;
import gui_control.ScenarioModel.ScenarioModel;


public class DataHandler {
	ArrayList<ScenarioModel> scenarios = new ArrayList<ScenarioModel>();
	ArrayList<String> modes = new ArrayList<String>();
	MovingBarSideModel frontSide;
	MovingBarSideModel leftSide;
	MovingBarSideModel rightSide;
	MovingBarSideModel backSide;
	
	public DataHandler(){
		addDefaultScenarios();
		addDefaultModes();
		
		//TODO: load data from database
	}
	
	private void addDefaultScenarios(){
		
		ScenarioModel temperatureScenario = new ScenarioModel();
		temperatureScenario.setName("Temperature");
		temperatureScenario.setRequestURL("http://api.openweathermap.org/data/2.5/weather?q=Hannover&APPID=d1e9d70bdb58b701b0366495d128403d&mode=xml");
		temperatureScenario.setPathToXML("current/temperature/value");
		temperatureScenario.setCreationDate(new Date());
		scenarios.add(temperatureScenario);
		
		ScenarioModel rainScenario = new ScenarioModel();
		rainScenario.setName("Rainfall");
		rainScenario.setRequestURL("http://api.openweathermap.org/data/2.5/weather?q=Hannover&APPID=d1e9d70bdb58b701b0366495d128403d&mode=xml");
		rainScenario.setPathToXML("current/...");
		rainScenario.setCreationDate(new Date());
		scenarios.add(rainScenario);
		
	}
	
	public void loadDataFromDatabase(Database database) throws SQLException{
		MovingBarSideModel savedFrontSide = database.getMovingBarSide("A");
		MovingBarSideModel savedLeftSide = database.getMovingBarSide("B");
		MovingBarSideModel savedRightSide = database.getMovingBarSide("C");
		MovingBarSideModel savedBackSide = database.getMovingBarSide("D");
		
		if(savedFrontSide!=null){
			System.out.println("loaded from db:");
			System.out.println(savedFrontSide);
			frontSide = savedFrontSide;
		}else{
			frontSide = new MovingBarSideModel("A",modes.get(0));
			database.insertMovingBarSideModel(frontSide);
		}
		
		if(savedLeftSide!=null){
			System.out.println("loaded from db:");
			System.out.println(savedLeftSide);
			leftSide = savedLeftSide;
		}else{
			leftSide = new MovingBarSideModel("B",modes.get(0));
			database.insertMovingBarSideModel(leftSide);
		}
		
		if(savedRightSide!=null){
			System.out.println("loaded from db:");
			System.out.println(savedRightSide);
			rightSide = savedRightSide;
		}else{
			rightSide = new MovingBarSideModel("C",modes.get(0));
			database.insertMovingBarSideModel(rightSide);
		}
		
		if(savedBackSide!=null){
			System.out.println("loaded from db:");
			System.out.println(savedBackSide);
			backSide = savedBackSide;
		}else{
			backSide = new MovingBarSideModel("D",modes.get(0));
			database.insertMovingBarSideModel(backSide);
		}
		
		
		ArrayList<String> savedModes = database.getModes();
		if(savedModes!=null){
			modes=savedModes;
		}else{
			database.insertModes(modes);
		}
		
		
		ArrayList<ScenarioModel> savedScenarios = database.getScenarios();
		if(savedScenarios!=null){
			scenarios=savedScenarios;
		}else{
			database.insertScenarios(scenarios);
			System.out.println("insert scenarios");
		}
		
		
	}
	
	private void addDefaultModes(){
		modes.add("From bottom to top");
		modes.add("Relative");
		modes.add("Moving height");
	}
	
	
	
	public void setFrontSide(Color quantityColor,Color referenceColor, int referenceValue,int stepSize,String scenarioName,String mode){
		frontSide.setQuantityColor(quantityColor);
		frontSide.setReferenceColor(referenceColor);
		frontSide.setReferenceValue(referenceValue);
		frontSide.setStepSize(stepSize);
		frontSide.setMode(mode);
		frontSide.setScenarioName(scenarioName);
	}
	
	public void setLeftSide(Color quantityColor,Color referenceColor, int referenceValue,int stepSize,String scenarioName,String mode){
		leftSide.setQuantityColor(quantityColor);
		leftSide.setReferenceColor(referenceColor);
		leftSide.setReferenceValue(referenceValue);
		leftSide.setStepSize(stepSize);
		leftSide.setMode(mode);
		leftSide.setScenarioName(scenarioName);
	}
	
	public void setRightSide(Color quantityColor,Color referenceColor, int referenceValue,int stepSize,String scenarioName,String mode){
		rightSide.setQuantityColor(quantityColor);
		rightSide.setReferenceColor(referenceColor);
		rightSide.setReferenceValue(referenceValue);
		rightSide.setStepSize(stepSize);
		rightSide.setMode(mode);
		rightSide.setScenarioName(scenarioName);
	}
	
	public void setBackSide(Color quantityColor,Color referenceColor, int referenceValue,int stepSize,String scenarioName,String mode){
		backSide.setQuantityColor(quantityColor);
		backSide.setReferenceColor(referenceColor);
		backSide.setReferenceValue(referenceValue);
		backSide.setStepSize(stepSize);
		backSide.setMode(mode);
		backSide.setScenarioName(scenarioName);
	}

	public ArrayList<ScenarioModel> getScenarios() {
		return scenarios;
	}
	
	public ScenarioModel getScenario(String scenarioName){
		for(ScenarioModel s : scenarios){
			if(s.getName() == scenarioName){
				return s;
			}
		}
		return null;
	}

	public void addScenario(ScenarioModel scenario) {
		scenarios.add(scenario);
	}
	
	public void deleteScenario(String scenarioName) {
		for(int i = 0 ; i<this.scenarios.size() ; i++){
			if(this.scenarios.get(i).getName() == scenarioName){
				this.scenarios.remove(i);
				return;
			}
		}
	}

	public MovingBarSideModel getFrontSide() {
		return frontSide;
	}

	public MovingBarSideModel getLeftSide() {
		return leftSide;
	}

	public MovingBarSideModel getRightSide() {
		return rightSide;
	}

	public MovingBarSideModel getBackSide() {
		return backSide;
	}

	public ArrayList<String> getModes() {
		return modes;
	}
	
	
}