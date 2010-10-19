import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * definir melhor os estados e as acções possiveis (pré e pós condiçoes)
 * leap
 * jason
 * madkit
 * 
 * peskisar smartobjects, agents, começar a pensar no relatório preliminar (Entrega a meio de novembro)
 * formalizar smartobjects, usar extensions, generalizar,
 * 
 * pensar em cenários diferentes (torneira encher copo de agua, etc)
 * 
 * ter vários smartobjects num cenário, obrigar o agente a escolher os k necessita
 * darlhe planos de acçoes e eventualmente criar planeador
 * 
 * citeseer
 */

/*
 Object properties: physical properties and a text description
 Interaction information: position of handles, buttons, grips, ...
 Object behavior: different behaviors based on state variables
 Agent behaviors: description of the behavior an agent should 
 follow when using the object
 TODO: NEEDS TO BE AN AGENT TO COMMUNICATE WITH OTHER AGENTS
 */
@SuppressWarnings("serial")
public class SmartObject extends Agent {
	
	List<Action> actionsList;
	List<State> stateList;
	State currentState;
	
	// Get current state
	public int getCurrentState() {
		return currentState;
	}

	public List<String> getActions() {
		System.out.println(state);
		return actionsList.get((Integer) state);
	}

	private class WaitForContact extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
				System.out.println("Message received at SmartObject, replying");
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent(((SmartObject) myAgent).getActions().get(0));
				send(reply);
			}
			// else

		}
	}

	protected void setup() {
		this.state = 1;
		actionsList = new HashMap<Integer, List<String>>();
		List<String> actionsLocked = Arrays.asList("unlock");
		List<String> actionsClosed = Arrays.asList("lock", "open");
		List<String> actionsOpen = Arrays.asList("close");

		actionsList.put(LOCKED, actionsLocked);
		actionsList.put(CLOSED, actionsClosed);
		actionsList.put(OPEN, actionsOpen);

		try {
			Thread.sleep(1000);
			System.out.println("Door is operational.");
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
