package gui_control.WirelessModel;

public class WirelessConnectionModel {
	
	String deviceName;
	String deviceAddress;
	
	public WirelessConnectionModel() {
		
	}
	
	

	public WirelessConnectionModel(String deviceName, String deviceAddress) {
		this.deviceName = deviceName;
		this.deviceAddress = deviceAddress;
	}



	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}
	
	
	
	
	
}
