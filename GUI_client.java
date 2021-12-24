package Project;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.ws4d.coap.core.CoapClient;
import org.ws4d.coap.core.CoapConstants;
import org.ws4d.coap.core.connection.BasicCoapChannelManager;
import org.ws4d.coap.core.connection.api.CoapChannelManager;
import org.ws4d.coap.core.connection.api.CoapClientChannel;
import org.ws4d.coap.core.enumerations.CoapMediaType;
import org.ws4d.coap.core.enumerations.CoapRequestCode;
import org.ws4d.coap.core.messages.api.CoapRequest;
import org.ws4d.coap.core.messages.api.CoapResponse;
import org.ws4d.coap.core.rest.CoapData;
import org.ws4d.coap.core.tools.Encoder;

import javafx.scene.input.DataFormat;

public class GUI_client extends JFrame implements CoapClient{
	private static final boolean exitAfterResponse = false;
	JButton btn_get = new JButton("Current status");
	JButton btn_exit = new JButton("Finishing partrol");
	JButton btn_obsget = new JButton("Starting partrol");
	
	JLabel path_label = new JLabel("Path");
	JTextArea path_text = new JTextArea("/pir", 1,1);//scroll bar none
	JLabel payload_label = new JLabel("Payload");
	JTextArea payload_text = new JTextArea("", 1,1);//scroll bar none
	JTextArea display_text = new JTextArea();
	JScrollPane display_text_jp  = new JScrollPane(display_text);
	JLabel display_label = new JLabel("Display");
	
	CoapClientChannel clientChannel = null;

	
	
	
	
	public GUI_client (String serverAddress, int serverPort) {
		//title
		super("Motion detector GUI");
		//layout
		this.setLayout(null);
		String sAddress = serverAddress;
		int sPort = serverPort;

		CoapChannelManager channelManager = BasicCoapChannelManager.getInstance();

		

		try {
			clientChannel = channelManager.connect(this, InetAddress.getByName(sAddress), sPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(-1);
		}


		if (null == clientChannel) {
			return;
		}

		//button setting 
		btn_obsget.setBounds(20, 670, 150, 50);
		btn_exit.setBounds(180, 670, 150, 50);
		btn_get.setBounds(340, 670, 150, 50);
		
		btn_get.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String path = path_text.getText();
				String payload = payload_text.getText();
				CoapRequest request = clientChannel.createRequest(CoapRequestCode.GET, path, true);
					
				if (path.equals("/pir")){
					request.setToken(Encoder.StringToByte("obToken"));
				}
				
				displayRequest(request);
				clientChannel.sendMessage(request);
			}
		});
		
		
	
		btn_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(-1);
			}
		});
		
		btn_obsget.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String path = path_text.getText();
				String payload = payload_text.getText();				
				CoapRequest request = clientChannel.createRequest(CoapRequestCode.GET, path, true);
				request.setToken(Encoder.StringToByte("obToken"));
				request.setObserveOption(0); // sequnece start number is zero 
				displayRequest(request);
				clientChannel.sendMessage(request);
			}
		});
		
		
		
		payload_label.setBounds(20, 570, 350, 30);
		payload_text.setBounds(20, 600, 440, 30);
		payload_text.setFont(new Font("arian", Font.BOLD, 15));
		
		path_label.setBounds(20, 500, 350, 30);
		path_text.setBounds(20, 530, 440, 30);
		path_text.setFont(new Font("arian", Font.BOLD, 15));
		
		display_label.setBounds(20, 10, 100, 20);
		display_text.setLineWrap(true);
		display_text.setFont(new Font("arian", Font.BOLD, 15));
		display_text_jp.setBounds(20, 40, 740, 430);
		

		
				
		this.add(btn_get);

		this.add(btn_exit);
		this.add(btn_obsget);
		this.add(path_text);
		this.add(path_label);
		this.add(payload_label);
		this.add(payload_text);
		this.add(display_text_jp);
		this.add(display_label);

		//frame size	
		this.setSize(800, 800);

		//showing frame
		this.setVisible(true);

		//terminating GUI by clicking x button 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {
		System.out.println("Connection Failed");
		System.exit(-1);
	}

	@Override
	public void onResponse(CoapClientChannel channel, CoapResponse response) {	
		// LED control (check token value -> check temp  -> send put message for led resource by temp value )
		// Check PIR resposed depending on token value
		if(Encoder.ByteToString(response.getToken()).equals("obToken")){
			String temp = (Encoder.ByteToString(response.getPayload()));
			//transmit put message depending on temp value from led resource 
			this.control_resource(temp);
		}
		
		
		
	}
	
	public void control_resource(String temp) {
		if(temp.equals("true")) {
			//red
			CoapRequest request_led = clientChannel.createRequest(CoapRequestCode.PUT, "/led", true);// 요청 메세지 작성 //con 확인형 == true // non 비확인형 false
			request_led.setPayload(new CoapData("red", CoapMediaType.text_plain)); 
			displayRequest(request_led);
			clientChannel.sendMessage(request_led);
			CoapRequest request_buzzer = clientChannel.createRequest(CoapRequestCode.PUT, "/buzzer", true);// 요청 메세지 작성 //con 확인형 == true // non 비확인형 false
			request_buzzer.setPayload(new CoapData("on", CoapMediaType.text_plain)); 
			displayRequest(request_buzzer);
			clientChannel.sendMessage(request_buzzer);
		}else if(temp.equals("false")) {
			//green
			CoapRequest request_led = clientChannel.createRequest(CoapRequestCode.PUT, "/led", true);// 요청 메세지 작성 //con 확인형 == true // non 비확인형 false
			request_led.setPayload(new CoapData("green", CoapMediaType.text_plain)); 
			displayRequest(request_led);
			clientChannel.sendMessage(request_led);
			CoapRequest request_buzzer = clientChannel.createRequest(CoapRequestCode.PUT, "/buzzer", true);// 요청 메세지 작성 //con 확인형 == true // non 비확인형 false
			request_buzzer.setPayload(new CoapData("off", CoapMediaType.text_plain)); 
			displayRequest(request_buzzer);
			clientChannel.sendMessage(request_buzzer);
		}
	}

	@Override
	public void onMCResponse(CoapClientChannel channel, CoapResponse response, InetAddress srcAddress, int srcPort) {
		// TODO Auto-generated method stub
	}
	
	private void displayRequest(CoapRequest request){
		SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
		Date now = new Date();
		String res = Encoder.ByteToString(request.getPayload()); //Read the byte value payload and save String value 
		if(request.getPayload() != null){
			if(res.equals("red")) {
				display_text.append(date.format(now) + "motion detected");
				display_text.setCaretPosition(display_text.getDocument().getLength());
			}
			else if(res.equals("green")) { 
				display_text.append("motion undetected");
				display_text.setCaretPosition(display_text.getDocument().getLength());
			} 
		} 
		else{
			display_text.setCaretPosition(display_text.getDocument().getLength());  
		}
		display_text.append(System.lineSeparator());
		display_text.append(System.lineSeparator());
	}
	

	public static void main(String[] args){
		//frame 
		GUI_client gui = new GUI_client("Client_IP", CoapConstants.COAP_DEFAULT_PORT);
	}
	
	
}