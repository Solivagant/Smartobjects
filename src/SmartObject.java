import java.util.List;

/*
 * SmartObject base
 * Definido por:
 * 	Lista de acções possíveis
 * 	Lista de estados possíveis
 * 	Estado corrente
 * É um agente para poder comunicar com os agentes que pretendam utilizar o SO.
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

	// Obter acções correntes
	public List<Action> getActions() {
		return actionsList;
	}

}
