import java.util.List;

/*
 * SmartObject base
 * Definido por:
 * 	Lista de ac��es poss�veis
 * 	Lista de estados poss�veis
 * 	Estado corrente
 * � um agente para poder comunicar com os agentes que pretendam utilizar o SO.
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

}
