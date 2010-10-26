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
	
	private void print(String log){
		System.out.println(getName() + ": " + log);
		
	}
	
	//Identifies the controller that will broker communication
	private AID controller = null;
	
	
	private class WaitForContact extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
				print("Message received at SmartObject, replying");
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent("default reply");
				send(reply);
			}
		}
	}
	
	
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
			System.out.println("BaseAgent is operational.");
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
		System.out.println(controller);

		ACLMessage type = new ACLMessage(ACLMessage.PROPOSE);
		type.addReceiver(controller);
		send(type);

		this.addBehaviour(new WaitForContact());
	}
}
