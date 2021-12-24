package Project;

import java.util.List;

import org.ws4d.coap.core.enumerations.CoapMediaType;
import org.ws4d.coap.core.rest.BasicCoapResource;
import org.ws4d.coap.core.rest.CoapData;
import org.ws4d.coap.core.tools.Encoder;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;

import com.pi4j.io.gpio.RaspiPin;


public class PIR extends BasicCoapResource {
	
	private String state = "false";
	GpioController gpio;
	GpioPinDigitalInput pir;

	
	private PIR(String path, String value, CoapMediaType mediaType) {
		super(path, value, mediaType);
		// TODO Auto-generated constructor stub
	}
	public PIR() {
		this("/pir","false",CoapMediaType.text_plain);
		GpioController gpio = GpioFactory.getInstance();
		pir = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01);
	}
	
	public synchronized CoapData get(List<String> query, List<CoapMediaType> mediaTypesAccepted) {
		return get(mediaTypesAccepted);
	}
	
	@Override
	public synchronized CoapData get(List<CoapMediaType> mediaTypesAccepted) {
		boolean now = pir.isHigh();
		if(now == true) 
			this.state = "true";
		else
			this.state = "false";
		return new CoapData(Encoder.StringToByte(this.state), CoapMediaType.text_plain);
	}
		
	public synchronized boolean setValue(byte[] value) {
		
		this.state = Encoder.ByteToString(value);
		return true;
	}
	public synchronized void optional_changed() {
		String temp; // Get Current status 
		boolean now = pir.isHigh();
		if(now == true) { 
			temp = "true";
		}
		else {
			temp = "false";
		}		
		
		if(temp.equals(this.state)) { 
			if(temp.equals("true"))
				System.out.println("motion detected");
			
		}
		else  { 
			if(temp.equals("false")) {
				System.out.println("motion undetected");
				this.changed(temp);
				this.state = temp;
			}
			else {
				System.out.println("motion detected");
				this.changed(temp);
				this.state = temp;
			}
			
		}
		// if state has changed, send response 
	}


	public synchronized boolean post(byte[] data, CoapMediaType type) {
		return this.setValue(data);
	}

	@Override
	public synchronized boolean put(byte[] data, CoapMediaType type) {
		return this.setValue(data);
	}

	@Override
	public synchronized String getResourceType() {
		return "Raspberry pi 4 Pir Sensor";
	}
	
}
