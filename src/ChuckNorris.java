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
 * 
 * Jogador de Jogo da Moeda que e baseado em fazendo as medias do historial do jogo.
 *
 */
public class ChuckNorris extends Agent {
	
	private static final long serialVersionUID = 1L;

	// Definicao da Template
	MessageTemplate template = MessageTemplate.and( 
			MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE), 
			MessageTemplate.MatchPerformative(ACLMessage.REQUEST ));
	
	// Controller AID
	private AID subscribe;
	
	// Historical Lists
	private ArrayList<playerInfo> betList = new ArrayList<playerInfo>();
	private ArrayList<Integer> currentBetList = new ArrayList<Integer>();
	
	// Controller Integers Total Players and the Coins in the Players Hand
	private int totalplayers = 0;
	private int coinsinhand = 0;
	
	/**
	 * Inicializacao do agente.
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
			
			/**
			 * Recepcao da confirmacao de registo no controller.
			 */
			protected void handleAgree(ACLMessage agree) {
				System.out.println("Agent " + getLocalName()
						+ ": Agreement received from "
						+ agree.getSender().getName() + ". Action is "
						+ agree.getContent());
			}
		});
		
		// Funcao que ira correr durante o desenlace do jogo.
		addBehaviour(new ProposeResponder(this,MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)) {
			
			private static final long serialVersionUID = 1L;
			
			/**
			 * Funcao que espera por respostas do controller.
			 */
			protected ACLMessage prepareResponse(ACLMessage agree) {
				
				if (agree.getContent() == null){
					betList.clear();
					ACLMessage msg = agree.createReply();
					msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					
					
					return msg;
				}
				
				// Show Coin Function
				else if (agree.getContent().equals("Start")) {
					
					currentBetList.clear();
					ACLMessage msg = agree.createReply();
					msg.setPerformative(ACLMessage.CONFIRM);
					msg.setContent(Integer.toString(coinChooser(Integer.parseInt(agree.getConversationId()))));
					
					System.out.println(" Agent:" + myAgent.getLocalName() + " Nº Coins:" + msg.getContent());
					
					return msg;
				}
				
				// Get Result Function - OpCode Code == Result and Conversation ID != Null
				else if(agree.getConversationId() != null && agree.getConversationId().equals("Result")){

					processResults(agree.getContent());		
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
						msg.setContent(Integer.toString(firstBet()));
					} else {
						processBets(agree.getContent());
						msg.setContent(Integer.toString(firstBet()));
					}
					
					
					System.out.println(" Agent:" + myAgent.getLocalName() + " Bet:" + msg.getContent());
					
					return msg;
				}
			}
		});
	}
	
	/**
	 * Funcao para escolher as moedas que o jogador tem na mao.
	 * @param ptotal - numero total de jogadores em jogo.
	 * @return Integer - Moedas na Mao de um Jogador.
	 */
	public Integer coinChooser(int ptotal){
		totalplayers = ptotal;
		coinsinhand = new Random().nextInt(4);
		return coinsinhand;
	}
	
	/**
	 * Funcao que verifica qual aposta ira efectuar. Para o caso deste agente ira fazer a media 
	 * do historial de jogo tanto a moedas jogadas como as apostas efectuadas. Caso nao haja historial
	 * faz aleatoriamente a jogada.
	 * @return Integer - Numero da aposta.
	 */
	public Integer firstBet() {
		
		if ( betList.isEmpty() && currentBetList.isEmpty() ){
			return getValue(coinsinhand, totalplayers);
		}
		
		else if (betList.isEmpty()){
			int iteration = 0;
			int value = 0;
			for(Integer bets: currentBetList){
				iteration += bets;
				value++;
			}
			
			int avg = Math.round(iteration/value);
			
			int cyclebreaker = 0;
			
			while(currentBetList.contains(avg)){
				avg = getValue(avg, totalplayers);
				if (cyclebreaker >= 3){
					avg = getValue(0, totalplayers);
				}
				cyclebreaker++;
			}
			return avg;
		}
		
		else if (currentBetList.isEmpty()){
			
			int coins = 0;
			int bets = 0;
			int value = 0;
			
			for (playerInfo info: betList){
				coins += info.getCoin();
				bets += info.getBet();
				value++;
			}
			
			int avg =  Math.round( ( ( ( (coins+coinsinhand) / (value+1) ) + (bets / value) ) / 2 ) );
			int cyclebreaker = 0;
			
			while(currentBetList.contains(avg)){
				avg = getValue(avg, totalplayers);
				if (cyclebreaker >= 3){
					avg = getValue(0, totalplayers);
				}
				cyclebreaker++;
			}
			return avg;
			
		}
		
		else {
			
			int coins = 0;
			int bets = 0;
			int value = 0;
			
			for (playerInfo info: betList){
				coins += info.getCoin();
				bets += info.getBet();
				value++;
			}
			
			int valuebet = value;
			
			for(Integer bet:currentBetList){
				bets += bet;
				valuebet++;
			}
			
			int avgbet = Math.round( bets / valuebet );
			int avgcoin = Math.round( ( coins + coinsinhand ) / ( value + 1 ) );
			
			int avg =  Math.round( (avgbet + avgcoin) / 2 );
			
			int cyclebreaker = 0;
			
			while(currentBetList.contains(avg)){
				avg = getValue(avg, totalplayers);
				if (cyclebreaker >= 3){
					avg = getValue(0, totalplayers);
				}
				cyclebreaker++;
			}
			return avg;
		}
	}
	
	/**
	 * Funcao que retorna um numero aleatoria entre um minimo e um maximo.
	 * @param min - valor minimo aleatorio.
	 * @param max - valor maximo aleatorio.
	 * @return int - valor aleatorio entre o maximo e o minimo proposto.
	 */
    public int getValue(int min, int max) {
        Random r = new Random();
        max = ( max * 3 ) + 1;
        int range = max - min;
        
        return Math.abs(r.nextInt(range) + min);
    }
    
    /**
     * Processa a string tipo retornada pelo controller mas so armazena 
     * o numero das apostas que ira ser guardada para que o jogador nao repita uma aposta ja efectuada.
     * @param bets - String Apostas Recebida pelo Controller durante o processo de aposta.
     */
    public void processBets(String bets) {
		String[] playerBets = bets.split("-");
		int i;
		for(i = 2; i < playerBets.length; i+=2){
			currentBetList.add(Integer.parseInt(playerBets[i]));
		}
	}
    
    /**
     * Processa a string tipo retornada pelo controller mas neste caso so armazena as moedas na mao
     * de cada jogador e a aposta feita por cada jogador que ira depois ser guardada no historial do
     * Chuck Norris.
     * @param results - String Resultados Recebida pelo Controller durante o processo de recepcao de resultados.
     */
    public void processResults(String results){
    	String[] playerResults = results.split("-");
    	for(int i = 2; i < playerResults.length; i+=4) {   		
    		// System.out.println("Coins - " + playerResults[i] + " Bets - " + playerResults[i+1]);    		
    		betList.add(new playerInfo(playerResults[i], playerResults[i+1]));
    	}
    }
}

