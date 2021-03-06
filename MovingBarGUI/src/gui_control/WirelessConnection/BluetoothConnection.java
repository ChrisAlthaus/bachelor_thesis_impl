package gui_control.WirelessConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

import gui_control.WirelessModel.WirelessConnectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BluetoothConnection {
	ArrayList<RemoteDevice> remoteDevices;		//holds all detected devices
						
	ObservableList<WirelessConnectionModel> wirelessConnections;
	RemoteDevice selectedDevice;	//selected device
	
	DiscoveryListener listener = getDiscoveryListener();
	Thread searcherThread;		//threads, because wait time would stop main application
	Thread senderThread;
	
	boolean queryRunning=false;
	static String message;			//message to be send to device
	final Object lock = new Object();
	
	LocalDevice localDevice;
    DiscoveryAgent agent;
	
	
	public BluetoothConnection(){
		try {
			localDevice = LocalDevice.getLocalDevice();
			agent = localDevice.getDiscoveryAgent();
			
		} catch (BluetoothStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setObservableList(ObservableList<WirelessConnectionModel> wirelessConnections){
		this.wirelessConnections = wirelessConnections;
	}
	    
	public void searchForDevices() throws InterruptedException, IOException{
		remoteDevices = new ArrayList<RemoteDevice>();
		
		if(!queryRunning){
			searcherThread =  getSearcherThread();
			queryRunning=true;
			searcherThread.start();
			queryRunning=false;
		}else{
			System.out.println("One Query is already running.");
		}
		
	
    }
	
	public void sendMessage(String message){
		this.message = message;
		
		if(!queryRunning){
			senderThread =  getSenderThread();
			queryRunning=true;
			senderThread.start();
			queryRunning=false;
		}else{
			System.out.println("One Query is already running.");
		}
	}
	

	//gets information (name, address) of available devices
	public Thread getSearcherThread(){
		Thread searchForDevices = new Thread(new Runnable() {
            @Override
            public void run() {
            	synchronized(lock) {
			        boolean started;
					try {
						started = agent.startInquiry(DiscoveryAgent.GIAC, listener);
						 if (started) {
				            System.out.println("wait for device inquiry to complete...");
				            lock.wait();
				            if(remoteDevices!=null){
				            	System.out.println(remoteDevices.size() +  " device(s) found");
				            }
				        	wirelessConnections.setAll(getWirelessConnectionModels());
				            
				        }
					
					} catch (BluetoothStateException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			       
			    }
            }
        });
		
		return searchForDevices; 
	}
	
	//used to send a message to the selected remote device
	//gets the supported transfer services of the remote device and 
	//sends a message, if this service is supported
	public Thread getSenderThread(){
		Thread searchForDevices = new Thread(new Runnable() {
            @Override
            public void run() {
                
                
            	UUID[] uuidSet = new UUID[1];
                 uuidSet[0]=new UUID(0x1105); //OBEX Object Push service
                 
                 int[] attrIDs =  new int[] {
                         0x0100 // Service name
                 };
               
                 
                 if(selectedDevice!=null){
                	try { 
                		//DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
						agent.searchServices(attrIDs,uuidSet,selectedDevice,listener);
						
					} catch (BluetoothStateException e) {
						
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                 }else{
                	return;
                 }
                 
                     
                 try {
                	 synchronized(lock){
                		 lock.wait();
                	 }
                  }
                 
                 catch (InterruptedException e) {
                     e.printStackTrace();
                     return;
                 }
                     
                 System.out.println("Service search finished.");
            }
                 
           
        });
		
		return searchForDevices; 
	}


	public DiscoveryListener getDiscoveryListener(){
		DiscoveryListener discoveryListener = new DiscoveryListener(){
			
	        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
	            System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
	            remoteDevices.add(btDevice);
	            String deviceAdress = btDevice.getBluetoothAddress();
	            String deviceName = "Hidden name";
	          
	            try {
	                System.out.println("     name " + btDevice.getFriendlyName(false));
	                deviceName = btDevice.getFriendlyName(false);
	            } catch (IOException cantGetDeviceName) {
	            	
	            }
	            //wirelessConnections.add(new WirelessConnectionModel(deviceName,deviceAdress));
	        }
	
	        public void inquiryCompleted(int discType) {
	            System.out.println("Device Inquiry completed!");
	            synchronized(lock){
	            	lock.notifyAll();
	            }
	        }

			@Override
			public void serviceSearchCompleted(int arg0, int arg1) {
				 synchronized (lock) {
			            lock.notify();
			        }
			}

			
			//goes through found supported services and sends message, if this service is supported
			@Override
		    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		        for (int i = 0; i < servRecord.length; i++) {
		            String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		            if (url == null) {
		                continue;
		            }
		            DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
		            if (serviceName != null) {
		                System.out.println("service " + serviceName.getValue() + " found " + url);
		                System.out.println(serviceName.getValue() +",");
		                 
		                if(((String) serviceName.getValue()).trim().equals("OBEX Object Push")){
		                    System.out.println("send message");
		                    sendMessageToDevice(url);                
		                }
		            } else {
		                System.out.println("service found " + url);
		            }
		            
		          
		        }
		    }
	
	    };
	    return discoveryListener;
	    
	}
	//send message to selected device/ server url
	private static void sendMessageToDevice(String serverURL){
        try{
            System.out.println("Connecting to " + serverURL);
    
            ClientSession clientSession = (ClientSession) Connector.open(serverURL);
            HeaderSet hsConnectReply = clientSession.connect(null);
            if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
                System.out.println("Failed to connect");
                return;
            }
    
            HeaderSet hsOperation = clientSession.createHeaderSet();
            hsOperation.setHeader(HeaderSet.NAME, "Hello.txt");
            hsOperation.setHeader(HeaderSet.TYPE, "text");
    
            //Create PUT Operation
            Operation putOperation = clientSession.put(hsOperation);
    
            // Send some text to server
            if(message!=null){
	            
            	System.out.println("write message :" + message );
            	byte data[] = message.getBytes("iso-8859-1");
	            OutputStream os = putOperation.openOutputStream();
	            os.write(data);
	            os.close();	
            }else{
            	System.out.println("Message to be send is null.");
            }
            
    
            putOperation.close();
    
            clientSession.disconnect(null);
    
            clientSession.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	

	/*public void searchForDevices() throws IOException{
		localDevice = LocalDevice.getLocalDevice();
		remoteDevices = localDevice.getDiscoveryAgent().retrieveDevices(DiscoveryAgent.PREKNOWN);
		
		/*for(RemoteDevice r: remoteDevices){
			System.out.println(r.getFriendlyName(false));
			System.out.println(r.getBluetoothAddress());
		}*/
	



	public ArrayList<RemoteDevice> getRemoteDevices() {
		return remoteDevices;
	}



	public void setRemoteDevices( ArrayList<RemoteDevice> remoteDevices) {
		this.remoteDevices = remoteDevices;
	}
	
	//returns true, if connection model is found in device list, else false
	public boolean setSelectedDevice(WirelessConnectionModel m){
		for(RemoteDevice r: remoteDevices){
			if(r.getBluetoothAddress() == m.getDeviceAddress()){
				selectedDevice=r;
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<WirelessConnectionModel> getWirelessConnectionModels(){
		ArrayList<WirelessConnectionModel> result = new ArrayList<WirelessConnectionModel>();
		if(remoteDevices!=null){
			for(RemoteDevice r: remoteDevices){
				WirelessConnectionModel m;
				try {
					m = new WirelessConnectionModel(r.getFriendlyName(false),r.getBluetoothAddress());
				} catch (IOException e) {
					//no identifier name
					m = new WirelessConnectionModel(null,r.getBluetoothAddress());
				}
				
				result.add(m);
			}	
		}
		
		return result;
	}
	
	public boolean isDeviceSet(){
		return (localDevice != null);
	}


	public LocalDevice getLocalDevice() {
		return localDevice;
	}

	public void setLocalDevice(LocalDevice localDevice) {
		this.localDevice = localDevice;
	}
	
	
}
