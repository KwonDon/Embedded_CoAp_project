package Project;

import java.util.List;

import org.ws4d.coap.core.enumerations.CoapMediaType;
import org.ws4d.coap.core.rest.BasicCoapResource;
import org.ws4d.coap.core.rest.CoapData;
import org.ws4d.coap.core.tools.Encoder;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Buzzer extends BasicCoapResource{
	private String state = "off";
	
	GpioController gpio;
	GpioPinDigitalOutput buzzer;
	
	private Buzzer(String path, String value, CoapMediaType mediaType) {
		super(path, value, mediaType);
		// TODO Auto-generated constructor stub
	}
	public Buzzer() {
		this("/buzzer","off",CoapMediaType.text_plain);
		GpioController gpio = GpioFactory.getInstance();
		buzzer = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_15,PinState.LOW);
	}
	@Override
	public synchronized CoapData get(List<String> query, List<CoapMediaType> mediaTypesAccepted) {
		return get(mediaTypesAccepted);
	}
	
	@Override
	public synchronized CoapData get(List<CoapMediaType> mediaTypesAccepted) {
		return new CoapData(Encoder.StringToByte(this.state), CoapMediaType.text_plain);
	}
	public synchronized boolean setValue(byte[] value) {
		this.state = Encoder.ByteToString(value);
		
		if(this.state.equals("on")) {
			buzzer.high();
		}
		else if(this.state.equals("off")){
			buzzer.low();
		}else {
			buzzer.low();
		}
		
		return true;
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
		return "Raspberry pi 4 Buzzer";
	}

}
