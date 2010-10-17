import java.util.ArrayList;

import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import jade.proto.SubscriptionResponder;
import jade.proto.SubscriptionResponder.Subscription;


/**
 * 
 * Class que trata em registar ou retirar uma subscription da lista de subscriptions.
 *
 */
public class PlayerManager implements SubscriptionResponder.SubscriptionManager {
	
	// Subscription List
	ArrayList<Subscription> agentlist;
	
	/**
	 * Inicializa o Subscription List.
	 */
	public PlayerManager(){
		agentlist = new ArrayList<Subscription>();	
	}

	/**
	 * Retira uma Subscription da lista de Subscriptions.
	 */
	public boolean deregister(Subscription arg0) throws FailureException {
		if(agentlist.contains(arg0))
			return false;
		else {
			agentlist.remove(arg0);
			return true;
		}
	}
	
	/**
	 * Faz o registo de uma Subscription adicionando � lista de subscriptions.
	 */
	public boolean register(Subscription arg0) throws RefuseException, NotUnderstoodException {
		if(agentlist.contains(arg0))
			return false;
		else {
			agentlist.add(arg0);
			return true;
		}
	}
	
	/**
	 * Retorna a Lista de Subscriptions
	 * @return Lista de Subscriptions - ArrayList<Subscription> 
	 */
	public ArrayList<Subscription> getSubscriptionList(){
		return agentlist;
	}
	
	/**
	 * Retorna uma ACLMessage cujo receivers sao todos os subscribers.
	 * @return Broadcast Message - ACLMessage.
	 */
	public ACLMessage getSubscriptions(){
		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
		for(Subscription sub : agentlist){
			msg.addReceiver(sub.getMessage().getSender());
		}
		return msg;
	}
	
	/**
	 * Retorna o numero de subscribers correntemente na lista.
	 * @return NumSubcribers - int
	 */
	public int getNumSubscribers(){
		return agentlist.size();
	}
}
