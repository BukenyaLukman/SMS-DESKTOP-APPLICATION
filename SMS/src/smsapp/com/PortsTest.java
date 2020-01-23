 package smsapp.com;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Formatter;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;



public class PortsTest {
	SMS smsAppObj;
	
	private static final String _NO_DEVICE_FOUND = " no device found";
	private static final Formatter _formatter = new Formatter(System.out);
	
	static CommPortIdentifier portId;
	
	static Enumeration<CommPortIdentifier> portList;
	
	static int bauds[] = { 9600 };
	
	private Enumeration<CommPortIdentifier> getCleanPortIdentifiers()
	{
		return CommPortIdentifier.getPortIdentifiers();
	}
	
	public void getAndTestCOMPorts()
	{
		smsAppObj.smsApp.appendToJTextAreaLog("\nSearching for devices...");
		portList = getCleanPortIdentifiers();
		while(portList.hasMoreElements())
		{
			portId = portList.nextElement();
			if(portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				smsAppObj.smsApp.appendToJTextAreaLog("\n\nFound Port " + portId.getName());
				for(int i = 0; i < bauds.length;i++) {
					SerialPort serialPort = null;
					smsAppObj.smsApp.appendToJTextAreaLog("\nTrying at "+ bauds[i]);
					
					try {
						InputStream inStream;
						OutputStream outStream;
						int c;
						String response;
						serialPort = (SerialPort) portId.open("\nTrying at ", 1971);
						serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
						serialPort.setSerialPortParams(bauds[i], SerialPort.DATABITS_8, SerialPort.DATABITS_7, SerialPort.DATABITS_6);
						inStream = serialPort.getInputStream();
						outStream = serialPort.getOutputStream();
						serialPort.enableReceiveTimeout(1000);
						
						c = inStream.read();
						while(c != -1)
						{
							c = inStream.read();
						outStream.write('A');
						outStream.write('T');
						outStream.write('\r');
						Thread.sleep(1000);
						response = "";
						StringBuilder sb = new StringBuilder();
						c = inStream.read();
						while(c != -1)
						{
							sb.append((char) c);
							c = inStream.read();
							
						}
						response = sb.toString();
						
						if(response.indexOf("OK") >= 0) {
							try {
								smsAppObj.smsApp.appendToJTextAreaLog(" Getting ");
								outStream.write('A');
								outStream.write('T');
								outStream.write('+');
								outStream.write('C');
								outStream.write('G');
								outStream.write('M');
								outStream.write('I');
								outStream.write('\r');
								outStream.write('A');
								
								
								outStream.write('A');
								outStream.write('T');
								outStream.write('+');
								outStream.write('\r');
								
								response = "";
								c = inStream.read();
								while(c != -1) {
									response += (char)c;
									c = inStream.read();
									
								}
								smsAppObj.smsApp.appendToJTextAreaLog(" Found ");
								
								
							}catch(Exception e) {
								smsAppObj.smsApp.appendToJTextAreaLog(_NO_DEVICE_FOUND);
							}
						}else {
							smsAppObj.smsApp.appendToJTextAreaLog(_NO_DEVICE_FOUND);
						}
						
						
						}
						
					}
					catch(Exception e) {
						System.out.print(_NO_DEVICE_FOUND);
						Throwable cause = e;
						while(cause.getCause() != null) {
							cause = cause.getCause();	
						}
						smsAppObj.smsApp.appendToJTextAreaLog(" (" + cause.getMessage() );
						
						
					}finally {
						if(serialPort != null)
						{
							serialPort.close();
						}
					}
				}
			}
		}
		smsAppObj.smsApp.appendToJTextAreaLog("\nTest complete.");
		
		
	}
	
	

}
