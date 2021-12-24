package Project;

import org.ws4d.coap.core.rest.CoapResourceServer;

public class CoAP_Server {
	private static CoAP_Server coapServer;
	private CoapResourceServer resourceServer;
	public static void main(String[] args) {
		coapServer = new CoAP_Server();
		coapServer.start();
	}

	public void start() {
		System.out.println("===Run Test Server ===");

		// create server
		if (this.resourceServer != null) {
			this.resourceServer.stop();
		}
		else
		this.resourceServer = new CoapResourceServer();


		// initialize resource
		LED led = new LED();
		PIR pir = new PIR();
		Buzzer buzzer = new Buzzer();
		pir.registerServerListener(resourceServer);
		
		// add resource to server
		this.resourceServer.createResource(pir);
		this.resourceServer.createResource(led);
		this.resourceServer.createResource(buzzer);
		
		
		// run the server
		try {
			this.resourceServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		while(true) {
			try {
				Thread.sleep(1000);
				pir.optional_changed();
			}catch(Exception e){
				
			}
		}

	}
}

