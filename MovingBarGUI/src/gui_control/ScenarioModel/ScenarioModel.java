package gui_control.ScenarioModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScenarioModel {
	String requestURL;
	String pathToXML;
	String pathToJson;
	String name;
	Date creationDate;
	double updateDuration;
	
	
	public ScenarioModel(String name, String requestURL, String pathToXML, String pathToJson,  String creationDate,
			double updateDuration) {
		super();
		this.requestURL = requestURL;
		this.pathToXML = pathToXML;
		this.pathToJson = pathToJson;
		this.name = name;
		this.updateDuration = updateDuration;
		
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date dateObject = null;
		try {
			dateObject = df.parse(creationDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.creationDate = dateObject;
	}



	public ScenarioModel() {
		this.creationDate = new Date();
	}
	
	

	public double getUpdateDuration() {
		return updateDuration;
	}

	public void setUpdateDuration(double updateDuration) {
		this.updateDuration = updateDuration;
	}

	public String getCreationDateString() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String date = df.format(creationDate);
		return date;
	}

	

	public String getCreationDate() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String date = df.format(creationDate);
		return date;
	}



	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDateText(){
		if(creationDate == null ){
			return null;
		}else{
			return creationDate.toString();
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getPathToXML() {
		return pathToXML;
	}

	public void setPathToXML(String pathToXML) {
		this.pathToXML = pathToXML;
	}

	public String getPathToJson() {
		return pathToJson;
	}

	public void setPathToJson(String pathToJson) {
		this.pathToJson = pathToJson;
	}
	
	public String toString(){
		return ""+name+","+requestURL+","+pathToXML+","+pathToJson+","+creationDate+","+updateDuration;
		
	}
	
	
}
