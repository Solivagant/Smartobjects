import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class ScenarioController extends Agent {
	private AID userAgent = null;
	private AID door = null;

	protected void setup() {

		// Service Description Starter
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("controller");
		sd.setName("Agent Controller");
		dfd.addServices(sd);

		// Register on the DF yellow pages service
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		System.out.println("Scenario Controller operational.");

		addBehaviour(new Attach());

	}

	private class Attach extends CyclicBehaviour {
		public void action() {
			if (door != null && userAgent != null) {
				System.out.println("Got both agents, connecting them.");
				ACLMessage toSend = new ACLMessage(ACLMessage.AGREE);
				toSend.addReceiver(userAgent);
				toSend.setSender(door);
				send(toSend);

				myAgent.removeBehaviour(this);
			}
			ACLMessage msg = receive();
			if (msg != null) {
				System.out.println("Message received.");
				msg.getSender();
				if (msg.getPerformative() == ACLMessage.REQUEST)
					userAgent = msg.getSender();
				else if (msg.getPerformative() == ACLMessage.PROPOSE)
					door = msg.getSender();
			}

		}
	}
}
