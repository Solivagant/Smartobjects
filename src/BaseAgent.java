import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

/*
 * Smartobjects will extend BaseAgent.
 * It defines basic JADE communication.
 */

public abstract class BaseAgent extends Agent {
	
	protected void log(String log){
		System.out.println(getLocalName() + ": " + log);
	}
	
	//Identifies the controller that will broker communication
	protected AID controller = null;
	
	protected void setup() {
//		this.state = 1;
//		actionsList = new HashMap<Integer, List<String>>();
//		List<String> actionsLocked = Arrays.asList("unlock");
//		List<String> actionsClosed = Arrays.asList("lock", "open");
//		List<String> actionsOpen = Arrays.asList("close");

//		actionsList.put(LOCKED, actionsLocked);
//		actionsList.put(CLOSED, actionsClosed);
//		actionsList.put(OPEN, actionsOpen);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Get Controller
		DFAgentDescription descriptor = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("controller");
		descriptor.addServices(sd);

		try {
			DFAgentDescription[] result = DFService.search(this, descriptor);
			controller = result[0].getName();
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

	}
}
