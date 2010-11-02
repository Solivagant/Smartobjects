import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/*
 * SmartObject base
 * Definido por:
 * 	Lista de ac��es poss�veis
 * 	Lista de estados poss�veis
 * 	Estado corrente
 * � um agente para poder comunicar com os agentes que pretendam utilizar o SO.
 * criar a partir de ficheiros vrml (agentes definidos em ficheiros vrml para depois serem lidos pelo webots)
 * VRML parser
 * Tornar os Estados comuns a v�rios tipos de smartobjects,e o useja, o estado Fechado de uma porta � o mesmo 
 * de um arm�rio, etc.
 * Criar SmartObjects abstractos tipificados, por ex um Arm�rio Abstracto k dps � instanciado com propriedades espec�ficas
 * Viper Parser
 * 
 * condi�oes com informa�ao de posi�ao
 */

@SuppressWarnings("serial")
public class SmartObject extends BaseAgent {

	List<Action> actionsList;
	List<State> stateList;
	State currentState;

	// Obter o estado corrente
	public State getCurrentState() {
		return currentState;
	}

	// Obter ac��es correntes
	public List<Action> getActions() {
		return actionsList;
	}

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
		addBehaviour(new actionInform());
		ACLMessage type = new ACLMessage(ACLMessage.PROPOSE);
		type.addReceiver(this.controller);
		send(type);
	}

	private class actionInform extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {
				// Agents identificam-se com REQUESTs, smartobjects usam PROPOSe
				if (msg.getPerformative() == ACLMessage.INFORM) {
					ACLMessage type = new ACLMessage(ACLMessage.INFORM);
					type.addReceiver(msg.getSender());
					try {
						type.setContentObject((Serializable) getActions());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					send(type);
				}

			}
		}
	}

}
