import java.util.ArrayList;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeResponder;
import jade.proto.SubscriptionInitiator;

/**
 * Agente Jogador que toma decisoes aleatoriamente.
 */
public class Randomizer extends Agent {
	

	// Definicao da Template
	MessageTemplate template = MessageTemplate.and( 
			MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE), 
			MessageTemplate.MatchPerformative(ACLMessage.REQUEST ));
	
	AID subscribe;
	int numPlayers;
	ArrayList<Integer> betList = new ArrayList<Integer>();
	
	/**
	 * Configuracao inicial do agente.
	 */
	protected void setup() {
		
		try {
			Thread.sleep(10000);
			System.out.println("Moeda Player Inicio !");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	    DFAgentDescription descriptor = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("controller");
		descriptor.addServices(sd);
		
		try {
			DFAgentDescription[] result = DFService.search(this,descriptor);
			subscribe = result[0].getName();
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println(subscribe);
		
		ACLMessage type = new ACLMessage(ACLMessage.REQUEST);
		type.setProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE);
		type.addReceiver(subscribe);
		
		addBehaviour(new SubscriptionInitiator(this, type) {
			
			private static final long serialVersionUID = 1L;

			protected void handleAgree(ACLMessage agree) {
				System.out.println("Agent " + getLocalName()
						+ ": Agreement received from "
						+ agree.getSender().getName() + ". Action is "
						+ agree.getContent());
			}
		});
		
		addBehaviour(new ProposeResponder(this,MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)) {
			
			private static final long serialVersionUID = 1L;

			protected ACLMessage prepareResponse(ACLMessage agree) {
				
				// Verificar primeiro se o getContent nao e NULL para evitar o null pointer exception.
				if (agree.getContent() == null){
					ACLMessage msg = agree.createReply();
					msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					return msg;
				}
				//receber aqui algures o nº de players!
				
				// Show Coin Function
				else if (agree.getContent().equals("Start")) {
					ACLMessage msg = agree.createReply();
					msg.setPerformative(ACLMessage.CONFIRM);
					msg.setContent(Integer.toString(randomCoin()));
					numPlayers = Integer.parseInt(agree.getConversationId());					
					System.out.println(" Agent:" + myAgent.getLocalName() + " Nº Coins:" + msg.getContent());
					
					return msg;
				}
				
				else if(agree.getConversationId() != null && agree.getConversationId().equals("Result")){
					System.out.println(myAgent.getLocalName() + " Received Results and they are : ");
					System.out.println(agree.getContent());
					
					ACLMessage msg = agree.createReply();
					msg.setPerformative(ACLMessage.CONFIRM);
					
					return msg;
				}
				
				// Show Bet Function
				else {
					System.out.println("Current Array of Bets are ---> " + agree.getContent());
					
					System.out.println("Player - " + myAgent.getLocalName() + " Bets !");
					ACLMessage msg = agree.createReply();
					msg.setPerformative(ACLMessage.CONFIRM);
					
					if(agree.getContent().equals("")){
						msg.setContent(randomBet());
					}
					else {
						msg.setContent(Integer.toString(processBets(agree.getContent())));
					}
					
					betList.clear();
					System.out.println("Sending Content " + msg.getContent());
					
					return msg;
				}
			}
		});
	}
	
	/**
	 * Calcula uma aposta aleatoriamente.
	 * @return String aposta
	 */
	public String randomBet(){
		int value = numPlayers*3;
		Random random = new Random();
		String bet = Integer.toString(random.nextInt((numPlayers*3) +1));
		return bet;
	}
	
	/**
	 * Calcula o numero de moedas a esconder na mao aleatoriamente.
	 * @return Integer o numero de moedas
	 */
	public Integer randomCoin(){
		Random random = new Random();
		return random.nextInt(4);
	}

	
	/**
	 * Processa as apostas recebidas e gera uma aposta aleatoria que nao tenha sido ja apostada por outro jogador.
	 * @param bets
	 * @return Integer - Retorna a Aposta escolhida.
	 */
	protected Integer processBets(String bets) {
		String[] playerBets = bets.split("-");
		int i;
		Integer bet = Integer.valueOf(randomBet());
		for(i = 2; i < playerBets.length; i+=2){
			betList.add(Integer.parseInt(playerBets[i]));
		}
		while (betList.contains(bet)){
			 bet = Integer.valueOf(randomBet());
		}
		return bet;
	}
}
