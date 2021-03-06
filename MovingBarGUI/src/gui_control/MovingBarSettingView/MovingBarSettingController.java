package gui_control.MovingBarSettingView;

import java.util.ArrayList;

import gui_control.DataHandler.DataHandler;
import gui_control.MovingBarSideModel.MovingBarSideModel;
import gui_control.ScenarioModel.ScenarioModel;
import gui_control.WirelessConnection.MessageHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MovingBarSettingController {
	@FXML
    private ChoiceBox<String> frontScenarioChoicebox;

    @FXML
    private ColorPicker frontDisplayColor;

    @FXML
    private ColorPicker frontReferenceColor;

    @FXML
    private ChoiceBox<String> frontModeChoicebox;

    @FXML
    private TextField frontRefrenceTextfield;

    @FXML
    private TextField frontStepsizeTextfield;

    @FXML
    private ChoiceBox<String> leftScenarioChoicebox;

    @FXML
    private ColorPicker leftDisplayColor;

    @FXML
    private ColorPicker leftReferenceColor;

    @FXML
    private ChoiceBox<String> leftModeChoicebox;

    @FXML
    private TextField leftRefrenceTextfield;

    @FXML
    private TextField leftStepsizeTextfield;

    @FXML
    private ChoiceBox<String> rightScenarioChoicebox;

    @FXML
    private ColorPicker rightDisplayColor;

    @FXML
    private ColorPicker rightReferenceColor;

    @FXML
    private ChoiceBox<String> rightModeChoicebox;

    @FXML
    private TextField rightRefrenceTextfield;

    @FXML
    private TextField rightStepsizeTextfield;

    @FXML
    private ChoiceBox<String> backScenarioChoicebox;

    @FXML
    private ColorPicker backDisplayColor;

    @FXML
    private ColorPicker backReferenceColor;

    @FXML
    private ChoiceBox<String> backModeChoicebox;

    @FXML
    private TextField backRefrenceTextfield;

    @FXML
    private TextField backStepsizeTextfield;
    
    @FXML
    private Accordion accordPane;
    
    @FXML
    private TitledPane frontPane;
    
    
    DataHandler dataHandler;
    MessageHandler messageHandler;
    
    ObservableList<String> scenarios = FXCollections.observableArrayList(); /*TODO:MODEL*/
    
    ObservableList<String> modes;
    
    @FXML
	private void initialize(){
    	
    	applyDataFromHandler();
    	
    	accordPane.setExpandedPane(frontPane);
    	
		
 }
    
    public void applyDataFromHandler(){
    	//TODO: Check if arraylist are length 0
    	if(dataHandler.getScenarios()==null){
    		System.out.println("scenario object is null");
    	}
    	System.out.println("scenario list length="+dataHandler.getScenarios().size());
    	scenarios = scenarioListToNames(dataHandler.getScenarios());
    	modes = FXCollections.observableArrayList(dataHandler.getModes());
    	
    	frontModeChoicebox.setItems(modes);
		leftModeChoicebox.setItems(modes);
		rightModeChoicebox.setItems(modes);
		backModeChoicebox.setItems(modes);	
		
    	frontScenarioChoicebox.setItems(scenarios);
    	leftScenarioChoicebox.setItems(scenarios);
    	rightScenarioChoicebox.setItems(scenarios);
    	backScenarioChoicebox.setItems(scenarios);
    	
    	MovingBarSideModel frontSide = dataHandler.getFrontSide();
    	
    	frontDisplayColor.setValue(getPaintColor(frontSide.getQuantityColor()));
    	frontReferenceColor.setValue(getPaintColor(frontSide.getReferenceColor()));
    	frontRefrenceTextfield.setText(Integer.toString(frontSide.getReferenceValue()));
    	frontStepsizeTextfield.setText(Integer.toString(frontSide.getStepSize()));
    	frontModeChoicebox.setValue(frontSide.getMode());
    	frontScenarioChoicebox.setValue(frontSide.getScenarioName());
    	
    	System.out.println("scenarioName="+frontSide.getScenarioName());
    	
    	MovingBarSideModel leftSide = dataHandler.getLeftSide();
    	leftDisplayColor.setValue(getPaintColor(leftSide.getQuantityColor()));
    	leftReferenceColor.setValue(getPaintColor(leftSide.getReferenceColor()));
    	leftRefrenceTextfield.setText(Integer.toString(leftSide.getReferenceValue()));
    	leftStepsizeTextfield.setText(Integer.toString(leftSide.getStepSize()));
    	leftModeChoicebox.setValue(leftSide.getMode());
    	leftScenarioChoicebox.setValue(leftSide.getScenarioName());
    	
    	MovingBarSideModel rightSide = dataHandler.getRightSide();
    	rightDisplayColor.setValue(getPaintColor(rightSide.getQuantityColor()));
    	rightReferenceColor.setValue(getPaintColor(rightSide.getReferenceColor()));
    	rightRefrenceTextfield.setText(Integer.toString(rightSide.getReferenceValue()));
    	rightStepsizeTextfield.setText(Integer.toString(rightSide.getStepSize()));
    	rightModeChoicebox.setValue(rightSide.getMode());
    	rightScenarioChoicebox.setValue(rightSide.getScenarioName());
    	
    	MovingBarSideModel backSide = dataHandler.getBackSide();
    	backDisplayColor.setValue(getPaintColor(backSide.getQuantityColor()));
    	backReferenceColor.setValue(getPaintColor(backSide.getReferenceColor()));
    	backRefrenceTextfield.setText(Integer.toString(backSide.getReferenceValue()));
    	backStepsizeTextfield.setText(Integer.toString(backSide.getStepSize()));
    	backModeChoicebox.setValue(backSide.getMode());
    	backScenarioChoicebox.setValue(backSide.getScenarioName());
    	
    }
    
  
    public void saveFrontData(){
    	int referenceValue= Integer.parseInt(frontRefrenceTextfield.getText());
    	int stepSize= Integer.parseInt(frontStepsizeTextfield.getText());
    	
    	dataHandler.setFrontSide(getAwtColor(frontDisplayColor.getValue()), getAwtColor(frontReferenceColor.getValue()),
    							referenceValue , stepSize,frontScenarioChoicebox.getValue(), frontModeChoicebox.getValue());
    	
    	ScenarioModel frontScenario = dataHandler.getScenario(frontScenarioChoicebox.getValue());
    	String name = frontScenario.getName();
    	String url= frontScenario.getRequestURL();
    	String pathXML = frontScenario.getPathToXML();
    	String pathJson = frontScenario.getPathToJson();
    	
    	if(name == "(None)"){
    		messageHandler.resetSide("A");
    		return;
    	}
    	
    	messageHandler.updateMovingBarSide("A", getAwtColor(frontDisplayColor.getValue()),getAwtColor(frontReferenceColor.getValue()),
    									referenceValue, stepSize,frontModeChoicebox.getValue(), url,
    									pathXML, pathJson);
    	
    }
    
    public void saveLeftData(){
    	int referenceValue= Integer.parseInt(leftRefrenceTextfield.getText());
    	int stepSize= Integer.parseInt(leftStepsizeTextfield.getText());
    	
    	dataHandler.setLeftSide(getAwtColor(leftDisplayColor.getValue()), getAwtColor(leftReferenceColor.getValue()),
    							referenceValue , stepSize,leftScenarioChoicebox.getValue(), leftModeChoicebox.getValue());
    	
    	ScenarioModel leftScenario = dataHandler.getScenario(leftScenarioChoicebox.getValue());
    	String name = leftScenario.getName();
    	String url= leftScenario.getRequestURL();
    	String pathXML = leftScenario.getPathToXML();
    	String pathJson = leftScenario.getPathToJson();
    	
    	if(name == "(None)"){
    		messageHandler.resetSide("D");
    		return;
    	}
    	
    	messageHandler.updateMovingBarSide("D", getAwtColor(leftDisplayColor.getValue()),getAwtColor(leftReferenceColor.getValue()),
    									referenceValue, stepSize, leftModeChoicebox.getValue(), url,
    									pathXML, pathJson);
    }
    
    public void saveRightData(){
    	int referenceValue= Integer.parseInt(rightRefrenceTextfield.getText());
    	int stepSize= Integer.parseInt(rightStepsizeTextfield.getText());
    	
    	dataHandler.setRightSide(getAwtColor(rightDisplayColor.getValue()), getAwtColor(rightReferenceColor.getValue()),
    							referenceValue , stepSize,rightScenarioChoicebox.getValue(), rightModeChoicebox.getValue());
    	
    	ScenarioModel rightScenario = dataHandler.getScenario(rightScenarioChoicebox.getValue());
    	String name = rightScenario.getName();
    	String url= rightScenario.getRequestURL();
    	String pathXML = rightScenario.getPathToXML();
    	String pathJson = rightScenario.getPathToJson();
    	
    	if(name == "(None)"){
    		messageHandler.resetSide("B");
    		return;
    	}
    	
    	messageHandler.updateMovingBarSide("B", getAwtColor(rightDisplayColor.getValue()),getAwtColor(rightReferenceColor.getValue()),
    									referenceValue, stepSize, rightModeChoicebox.getValue(), url,
    									pathXML, pathJson);
    }
    
    public void saveBackData(){
    	int referenceValue= Integer.parseInt(backRefrenceTextfield.getText());
    	int stepSize= Integer.parseInt(backStepsizeTextfield.getText());
    	
    	dataHandler.setBackSide(getAwtColor(backDisplayColor.getValue()), getAwtColor(backReferenceColor.getValue()),
    							referenceValue , stepSize, backScenarioChoicebox.getValue(), backModeChoicebox.getValue());
    	
    	ScenarioModel backScenario = dataHandler.getScenario(backScenarioChoicebox.getValue());
    	String name = backScenario.getName();
    	String url= backScenario.getRequestURL();
    	String pathXML = backScenario.getPathToXML();
    	String pathJson = backScenario.getPathToJson();
    	
    	if(name == "(None)"){
    		messageHandler.resetSide("C");
    		return;
    	}
    	messageHandler.updateMovingBarSide("C", getAwtColor(backDisplayColor.getValue()),getAwtColor(backReferenceColor.getValue()),
    									referenceValue, stepSize, backModeChoicebox.getValue(), url,
    									pathXML, pathJson);
    }
    
    public void setDataHandler(DataHandler dataHandler){
    	this.dataHandler = dataHandler;
    }
    
    public void setMessageHandler(MessageHandler messageHandler){
    	this.messageHandler = messageHandler;
    }
    
    public ObservableList<String> scenarioListToNames(ArrayList<ScenarioModel> scenarios){
    	ArrayList<String> list = new ArrayList<String>();
    	for(ScenarioModel s: scenarios){
    		list.add(s.getName());
    	}
    	
    	return FXCollections.observableArrayList(list);
    }
    
    private static java.awt.Color getAwtColor(javafx.scene.paint.Color paintColor){
    	java.awt.Color awtColor = new java.awt.Color((float) paintColor.getRed(),
                (float) paintColor.getGreen(),
                (float) paintColor.getBlue(),
                (float) paintColor.getOpacity());
    	return awtColor;
    	
    }
    
    private static Color getPaintColor(java.awt.Color awtColor){
    	int r = awtColor.getRed();
    	int g = awtColor.getGreen();
    	int b = awtColor.getBlue();
    	int a = awtColor.getAlpha();
    	double opacity = a / 255.0 ;
    	return Color.rgb(r, g, b, opacity);
	}
    
    
    
}
