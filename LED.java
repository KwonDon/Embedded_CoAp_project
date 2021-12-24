package Project;
import java.util.List;

import org.ws4d.coap.core.enumerations.CoapMediaType;
import org.ws4d.coap.core.rest.BasicCoapResource;
import org.ws4d.coap.core.rest.CoapData;
import org.ws4d.coap.core.tools.Encoder;
import com.pi4j.io.gpio.*;

public class LED extends BasicCoapResource{
	private String state = "off";
	GpioController gpio;
	GpioPinDigitalOutput r_led;
	GpioPinDigitalOutput g_led;
	GpioPinDigitalOutput b_led;

	private LED(String path, String value, CoapMediaType mediaType) {
		super(path, value, mediaType);
	} // value is state default value

	public LED() {
		this("/led", "off", CoapMediaType.text_plain); // path = "/led"
		gpio = GpioFactory.getInstance();
		r_led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08,PinState.LOW);
		g_led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09,PinState.LOW);
		b_led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07,PinState.LOW);

	}

	@Override
	public synchronized CoapData get(List<String> query, List<CoapMediaType> mediaTypesAccepted) {
		return get(mediaTypesAccepted);
	}
	
	@Override
	public synchronized CoapData get(List<CoapMediaType> mediaTypesAccepted) {
		return new CoapData(Encoder.StringToByte(this.state), CoapMediaType.text_plain);
	}

	@Override
	public synchronized boolean setValue(byte[] value) {
		this.state = Encoder.ByteToString(value);
		
		if(this.state.equals("red")) {
			r_led.high();
			g_led.low();
			b_led.low();
		}
		else if(this.state.equals("green")) {
			g_led.high();
			r_led.low();
			b_led.low();
		}
		else if(this.state.equals("off")) {
			g_led.low();
			r_led.low();
			b_led.low();
		}
		
		return true;
	}
	
	@Override
	public synchronized boolean post(byte[] data, CoapMediaType type) {
		return this.setValue(data);
	}

	@Override
	public synchronized boolean put(byte[] data, CoapMediaType type) {
		return this.setValue(data);
	}

	@Override
	public synchronized String getResourceType() {
		return "Raspberry pi 4 LED";
	}

}