package gui_control.SettingView;

import java.io.IOException;

import javax.swing.BorderFactory;

import gui_control.DataHandler.DataHandler;
import gui_control.MovingBarOverallModel.MovingBarOverallModel;
import gui_control.ScenarioModel.ScenarioModel;
import gui_control.SocketModel.SocketModel;
import gui_control.WirelessConnection.BluetoothConnection;
import gui_control.WirelessConnection.MessageHandler;
import gui_control.WirelessConnection.TcpConnection;
import gui_control.WirelessModel.WirelessConnectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;

public class SettingController {
	    
	@FXML
	private TextField ipAddressTextfield;

	@FXML
	private TextField portTextfield;

	@FXML
	private Button selectSocketButton;
	
	@FXML
	private TextArea statusTextArea;

	@FXML
	private Button statusButton;
	
    @FXML
    private Button shutdownButton;
	
	@FXML
    private TextField brightnessTextfield;
	
	@FXML
	private Label tcpStatus;
	
	@FXML
    private TextField sampleTimeTextfield;
	
    @FXML
    private Button setOverallButton;
    

	    
	MessageHandler messageHandler;
	DataHandler dataHandler;
	 
	 
	public void initialize() throws InterruptedException{
			SocketModel savedSocket = dataHandler.getSocket();
			if(savedSocket!=null){
				ipAddressTextfield.setText(savedSocket.getIp());
				portTextfield.setText(Integer.toString(savedSocket.getPortNumber()));
			}
			
			MovingBarOverallModel savedOverallSettings = dataHandler.getOverallSettings();
			if(savedOverallSettings!=null){
				brightnessTextfield.setText(Integer.toString(savedOverallSettings.getBrightness()));
				sampleTimeTextfield.setText(Integer.toString(savedOverallSettings.getSampleTime()));
			}
	}
	 
	 
	public void setSocket() throws IOException{
		if(!portTextfield.getText().isEmpty()){
			String ip= ipAddressTextfield.getText();
			int portNumber = Integer.parseInt(portTextfield.getText());
			
			if(ip.length() > 0 && portNumber != 0){
				dataHandler.setSocket(new SocketModel(ip,portNumber));
				messageHandler.setTcpConnection(ip,portNumber);	
				messageHandler.establishConnection(tcpStatus);
				
			}
		
		}
		
		
		
		
	}
	 
	public void setMessageHandler(MessageHandler messageHandler){
	    	this.messageHandler = messageHandler;
	    }
	

	 public void setDataHandler(DataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}
	 
	public void getStatus(){
		statusTextArea.clear();
		System.out.println("getting status");
		messageHandler.requestStatus(statusTextArea);
	}
	
	public void shutdownRaspberryPi(){
		messageHandler.shutdownRaspberryPi();
	}


	public void setOverallSettings(){
		if(!brightnessTextfield.getText().isEmpty() && !sampleTimeTextfield.getText().isEmpty()){
			 int brightness = Integer.parseInt(brightnessTextfield.getText());
			 int sampleTime = Integer.parseInt(sampleTimeTextfield.getText());
			 if(brightness>=0 && brightness<=255){
				 MovingBarOverallModel m = new MovingBarOverallModel();
				 m.setBrightness(brightness);
				 m.setSampleTime(sampleTime);
				 
				 dataHandler.setOverallSettings(m);
				 messageHandler.updateOverallPreferences(sampleTime,brightness);
			 }
			
		}
		
	 }
	 
}
