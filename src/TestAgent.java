import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class TestAgent extends Agent {
	private AID controller;
	private AID door;

	/**
	 * Agent initialization
	 */
	protected void setup() {
		try {
			Thread.sleep(1000);
			System.out.println("Test Agent is operational.");
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

		ACLMessage type = new ACLMessage(ACLMessage.REQUEST);
		type.addReceiver(controller);
		send(type);

		this.addBehaviour(new WaitForContact());
	}

	private class WaitForContact extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.AGREE) {
				door = msg.getSender();
				myAgent.addBehaviour(new Act());
			}
			if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
				System.out.println(msg.getContent());

			}
		}
	}

	private class Act extends CyclicBehaviour {
		public void action() {
			if (((TestAgent) myAgent).door != null) {
				ACLMessage type = new ACLMessage(ACLMessage.REQUEST);
				type.addReceiver(door);
				send(type);
				
				myAgent.removeBehaviour(this);
			}
		}
	}

}
