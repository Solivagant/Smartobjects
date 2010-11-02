import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class ScenarioController extends BaseAgent {
	private ArrayList<AID> agents = new ArrayList<AID>();
	private ArrayList<AID> smartObjects = new ArrayList<AID>();

	@Override
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
		addBehaviour(new PollSmartObjects());
	}

	private class Attach extends CyclicBehaviour {
		public void action() {
			// if (door != null && userAgent != null) {
			// System.out.println("Got both agents, connecting them.");
			// ACLMessage toSend = new ACLMessage(ACLMessage.AGREE);
			// toSend.addReceiver(userAgent);
			// toSend.setSender(door);
			// send(toSend);
			//
			// myAgent.removeBehaviour(this);
			// }
			ACLMessage msg = blockingReceive();
			if (msg != null) {
				// Agents identificam-se com REQUESTs, smartobjects usam PROPOSe
				if (msg.getPerformative() == ACLMessage.REQUEST) {
					agents.add(msg.getSender());
				}
				if (msg.getPerformative() == ACLMessage.PROPOSE) {
					smartObjects.add(msg.getSender());

				}
				
				log("Number of smartobjects: " + smartObjects.size());
				log("Number of agents: " + agents.size());
			}

		}
	}
	
	private class PollSmartObjects extends CyclicBehaviour {
		public void action() {
			if (smartObjects.size() > 0) {
				for(AID so : smartObjects){
					//Abstrair as ACLMessages e os performatives utilizando métodos próprios
					//género (requestActions()), que lá dentro é que têm os performatives
					ACLMessage toSend = new ACLMessage(ACLMessage.INFORM);
					toSend.addReceiver(so);
					send(toSend);
					log("Sending message to so");
				}

				ACLMessage msg = receive();
				if (msg != null) {
					log("got message from so");
					// Agents identificam-se com REQUESTs, smartobjects usam PROPOSe
					if (msg.getPerformative() == ACLMessage.INFORM) {
						try {
							log("Acções de " + msg.getSender() + ":" + msg.getContentObject());
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}
			

		}
	}	
}
