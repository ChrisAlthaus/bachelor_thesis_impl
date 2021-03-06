package gui_control;
	
import java.io.IOException;
import java.sql.SQLException;

import gui_control.Main;
import gui_control.DataHandler.DataHandler;
import gui_control.Database.Database;
import gui_control.EditorView.EditorController;
import gui_control.MovingBarSettingView.MovingBarSettingController;
import gui_control.ScenarioModel.ScenarioModel;
import gui_control.SettingRequestView.SettingRequestController;
import gui_control.SettingView.SettingController;
import gui_control.WirelessConnection.BluetoothConnection;
import gui_control.WirelessConnection.MessageHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private static Stage primaryStage;
	private static BorderPane mainLayout;
	
	private static EditorController editorController = new EditorController();
	private static MovingBarSettingController movingBarSettingController = new MovingBarSettingController();
	private static SettingController settingController= new SettingController();
	
	private static DataHandler dataHandler = new DataHandler();
	private static Database database = new Database();
	
	private static MessageHandler messageHandler = new MessageHandler();
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("MovingBar Controller");
		this.primaryStage.getIcons().add(new Image("file:icons/guiLogo.png"));
		
		try {
			database.init();
			dataHandler.loadDataFromDatabase(database);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		showMainView();
		showMovingBarSettingView();
	}
	
	private static void showMainView() throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("mainView/MainView.fxml"));
		
		mainLayout = loader.load();
		Scene scene = new Scene(mainLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	public static void showSettingRequestView() throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("SettingRequestView/SettingRequestView.fxml"));
		//loader.setController(requestController);
		
		
		BorderPane requestSetting = loader.load(); 
		mainLayout.setCenter(requestSetting);
		
		
	}
	
	public static void showEditorView() throws IOException{
		editorController.setDataHandler(dataHandler);
		FXMLLoader loader = new FXMLLoader();
		
		loader.setLocation(Main.class.getResource("EditorView/EditorView.fxml"));
		loader.setController(editorController);
		
		
		
		System.out.println("editor view");
		BorderPane editorView = loader.load(); 
		mainLayout.setCenter(editorView);
		
		
	}
	
	
	public static void showMovingBarSettingView() throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("MovingBarSettingView/MovingBarSettingView.fxml"));
		loader.setController(movingBarSettingController);
		
		movingBarSettingController.setDataHandler(dataHandler);
		movingBarSettingController.setMessageHandler(messageHandler);
		
		
		BorderPane movingBarSetting = loader.load(); 
		mainLayout.setCenter(movingBarSetting);
		
		
	}
	
	public static void showSettingView() throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("SettingView/SettingView.fxml"));
		loader.setController(settingController);
		
		//settingController.setWirelessConnectionHandler(wirelessConnection);
		settingController.setMessageHandler(messageHandler);
		settingController.setDataHandler(dataHandler);
		
		BorderPane settingView = loader.load(); 
		mainLayout.setCenter(settingView);
		
		
	}
	
	public static void printScenarioList(){
		System.out.println(dataHandler.getFrontSide());
		System.out.println(dataHandler.getLeftSide());
		System.out.println(dataHandler.getRightSide());
		System.out.println(dataHandler.getBackSide());
	}
	
	@Override
	public void stop() throws Exception {
		messageHandler.closeConnection();
		database.insertModes(dataHandler.getModes());
		database.insertMovingBarSideModel(dataHandler.getFrontSide());
		database.insertMovingBarSideModel(dataHandler.getLeftSide());
		database.insertMovingBarSideModel(dataHandler.getRightSide());
		database.insertMovingBarSideModel(dataHandler.getBackSide());
		database.resetScenarioTable();
		database.insertScenarios(dataHandler.getScenarios());
		database.insertNetworkSocket(dataHandler.getSocket());
		database.insertOverallSettings(dataHandler.getOverallSettings());
	}

	public static void main(String[] args) {
		launch(args);
	}
}
