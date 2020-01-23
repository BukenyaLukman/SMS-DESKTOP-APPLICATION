package smsapp.com;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import gnu.io.CommPortIdentifier;
import smsapp.com.org.ajwcc.pduUtils.test.integration.AbstractTester.OutboundNotification;
import smsapp.com.org.smslib.AGateway;
import smsapp.com.org.smslib.IOutboundMessageNotification;
import smsapp.com.org.smslib.Service;
import smsapp.com.org.smslib.modem.SerialModemGateway;

public class SMS extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldLog;
	private JTextField textFieldCellPhoneNumber;
	private JTextField textFieldMessage;
	public static SMS smsApp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					smsApp = new SMS();
					smsApp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SMS() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnDetect = new JButton("Detect Comm Ports And Devices");
		btnDetect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new PortsTest().getAndTestCOMPorts();
			}
		});
		btnDetect.setBounds(12, 12, 258, 25);
		contentPane.add(btnDetect);
		
		textFieldLog = new JTextField();
		textFieldLog.setBounds(22, 56, 258, 203);
		contentPane.add(textFieldLog);
		textFieldLog.setColumns(10);
		
		textFieldCellPhoneNumber = new JTextField();
		textFieldCellPhoneNumber.setText("+256702588162");
		textFieldCellPhoneNumber.setBounds(22, 285, 258, 63);
		contentPane.add(textFieldCellPhoneNumber);
		textFieldCellPhoneNumber.setColumns(10);
		
		textFieldMessage = new JTextField();
		textFieldMessage.setText("This message is sent from the desktop of the linux machine");
		textFieldMessage.setBounds(22, 360, 258, 70);
		contentPane.add(textFieldMessage);
		textFieldMessage.setColumns(10);
		
		JComboBox comboBoxPorts = new JComboBox();
		comboBoxPorts.setBounds(292, 362, 32, 24);
		contentPane.add(comboBoxPorts);
		
		JButton btnNewButtonListPorts = new JButton("List Ports");
		btnNewButtonListPorts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				comboBoxPorts.removeAllItems();
				java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
				while(portEnum.hasMoreElements()) {
					CommPortIdentifier portIdentifier = portEnum.nextElement();
					System.out.println(portIdentifier.getName() + " - " + getPortTypeName(portIdentifier.getPortType()) + "\n");
					comboBoxPorts.addItem(portIdentifier.getName());
					textFieldLog.append("\n" + portIdentifier.getName() + " - " + getPortTypeName(portIdentifier.getPortType() + "\n"));
					
				}
			}
			
			

			private String getPortTypeName(int portType) {
				// TODO Auto-generated method stub
				switch( portType ) {
				 	case CommPortIdentifier.PORT_I2C:
				 		return "12C";
				 	case CommPortIdentifier.PORT_PARALLEL:
				 		return "Parellel";
				 	case CommPortIdentifier.PORT_RAW:
				 		return "Raw";
				 	case CommPortIdentifier.PORT_RS485:
				 		return "RS485";
				 	case CommPortIdentifier.PORT_SERIAL:
				 		return "Serial";
				 	default:
				 		return "unknown type";
				}
				
			}
		});
		
		
		
		
		btnNewButtonListPorts.setBounds(292, 398, 117, 25);
		contentPane.add(btnNewButtonListPorts);
		
		JButton btnNewButtonSendSMS = new JButton("SendSMS");
		btnNewButtonSendSMS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					sendSMS();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}

			private void sendSMS() throws Exception{
				// TODO Auto-generated method stub
				OutboundNotification outboundNotification = new OutboundNotification();
				SerialModemGateway gateway = new SerialModemGateway("", comboBoxPorts.getSelectedItem().toString(),9600,"","");
				gateway.setInbound(true);
				gateway.setOutbound(true);
				//gateway.setSimPin(1000);
				Service.getInstance().setOutboundMessageNotification(outboundNotification);
				Service.getInstance().addGateway(gateway);
				String status = Service.getInstance().getServiceStatus().toString();
				if(status == "STARTED") {
					Service.getInstance().startService();
				}
				
				OutboundMessage msg = new OutboundMessage(textFieldCellPhoneNumber.getText(),textFieldMessage.getText());
				Service.getInstance().sendMessage(msg);
				
				textFieldLog.append(msg.toString());
				
				Service.getInstance().stopService();
				Service.getInstance().removeGateway(gateway);
				
				
				
			}
			
			class OutboundMessage implements IOutboundMessageNotification
			{
				public void process(AGateway gateway, OutboundMessage msg) {
					System.out.println("Outbound handler called from gateway: "+ gateway.getGatewayId());
					System.out.println(msg);
				}
			}
		});
		btnNewButtonSendSMS.setBounds(292, 463, 117, 25);
		contentPane.add(btnNewButtonSendSMS);
		
		JButton btnNewButtonModemInfo = new JButton("Modem Information");
		btnNewButtonModemInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					getModemInfo();
				}catch(Exception e) {
					
				}
			}

			private void getModemInfo() throws Exception{
				// TODO Auto-generated method stub
				SerialModemGateway gateway = new SerialModemGateway("",comboBoxPorts.getSelectedItem().toString(),9600,"","");
				
				Service.getInstance().addGateway(gateway);
				Service.getInstance().startService();
				
				textFieldLog.append("Modem Information");
				textFieldLog.append("\n Manufacturer: " + gateway.getManufacturer());
				textFieldLog.append("\n Model: "+ gateway.getModel());
				textFieldLog.append("\n Serial No: " + gateway.getImsi());
				textFieldLog.append("\n Battery Level: "+ gateway.getSignalLevel() + "dBm");
				textFieldLog.append("\n SIM IMSI: " + gateway.getSignalLevel() + gateway.getBatteryLevel() + "%");
				textFieldLog.append("\n Battery Level: " + gateway.getBatteryLevel() + "%");
				
				
				Service.getInstance().stopService();
				Service.getInstance().removeGateway(gateway);
				
				
			}
		});
		btnNewButtonModemInfo.setBounds(292, 145, 171, 25);
		contentPane.add(btnNewButtonModemInfo);
	}
	
	public void appendToJTextAreaLog(String txt) {
		try {
			((Appendable) textFieldLog).append(txt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
