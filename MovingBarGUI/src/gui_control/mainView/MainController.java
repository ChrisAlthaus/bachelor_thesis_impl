package gui_control.mainView;

import java.io.IOException;

import gui_control.Main;
import gui_control.ScenarioModel.ScenarioModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainController {
	

    @FXML
    private Button movingBarButton;

    @FXML
    private Button scenarioEditorButton;

    @FXML
    private Button settingButton;
    
    
    
    public void goMovingBarSettings() throws IOException{
    	Main.showMovingBarSettingView();
    }
    
    public void goEditorView() throws IOException{
    	Main.showEditorView();
    }
    
    public void goSettingView() throws IOException{
		Main.showSettingView();
	}
    
}
