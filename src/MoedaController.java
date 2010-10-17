
import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeInitiator;
import jade.proto.SubscriptionResponder;
import jade.proto.SubscriptionResponder.Subscription;



/**
 * 
 * Esta class tem como objectivo em gerir o jogo da moeda e fazer a gestao dos jogadores.
 *
 */
public class MoedaController extends Agent {

	private static final long serialVersionUID = 1L;
	
	// Numero de Rounds de Um Jogo
	private static final int numRound = 10;
	
	// Flag verifica se um jogo est� a correr ou n�o
	private boolean gameInProgress = false;
	
	// Cria um playerManager que � do tipo Subscription Responder
	final PlayerManager regist = new PlayerManager();
	
	// Lista que ir� guardar todos os jogadores que est�o correntemente a jogar.
	private ArrayList<Tuplo> inGamePlayers = new ArrayList<Tuplo>();
	
	// Tipo MessageTemplate a ser recebido durante o processo do decorrer do jogo.
	private MessageTemplate gameTemplate = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM );

	
	/**
	 * Funcao startup do Agent Controller.
	 */
	protected void setup(){
		
		// Tipo de MessageTemplate a ser recebido durante o processo de subscriptions.
		MessageTemplate template = MessageTemplate.and( 
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE), 
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST ));
		
		
		// Service Description Starter
		DFAgentDescription dfd = new DFAgentDescription(); 
		dfd.setName(getAID()); 
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType("controller");
		sd.setName("Agent Controller");
		dfd.addServices(sd);
		
		// Faz o registo do agente ao DF Service
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		
		System.out.println(" The Game Controller is Now Online and Ready to receive players");
		
		//Get new Players, handle quitters
		ParallelBehaviour pb = new ParallelBehaviour();
		
		
		pb.addSubBehaviour(new SubscriptionResponder(this, template, regist) {
			
			private static final long serialVersionUID = 1L;
			
			/**
			 * Funcao que trata de uma mensagem do tipo subscription.
			 */
			protected ACLMessage handleSubscription(ACLMessage subscription){
				
				System.out.println("Recebi um Subscription!");
				
				Subscription temp = this.createSubscription(subscription);
				
				ACLMessage reply = subscription.createReply();
				
				try {
					boolean success = regist.register(temp);
					
					if(success){
						reply.setPerformative(ACLMessage.AGREE);
					}
					else{
						reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					}	
				} catch (RefuseException e) {
					e.printStackTrace();
				} catch (NotUnderstoodException e) {
					e.printStackTrace();
				}
				
				return reply;
			}
			
			/**
			 * Trata de uma mensagem do tipo Subscription Cancel.
			 */
			protected ACLMessage handleCancel(ACLMessage cancel){
				
				Subscription temp = this.createSubscription(cancel);
				ACLMessage reply = cancel.createReply();
				
				try {
					boolean success = regist.deregister(temp);
					
					if(success)
						reply.setPerformative(ACLMessage.AGREE);
					else
						reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					
				} catch (FailureException e) {
					e.printStackTrace();
				}
				
				return reply;
			}
			
		});

		pb.addSubBehaviour(new startGame(this,10000));
		this.addBehaviour(pb);
	}

	
	/**
	 * 
	 * Behaviour que comeca um jogo novo.
	 *
	 */
	private class startGame extends TickerBehaviour {

		public startGame(MoedaController a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			
			System.out.println(gameInProgress);
			
			if(!gameInProgress && regist.getNumSubscribers() > 1){
				System.out.println("A New Game Has Started!! Players Get Ready to Play!");
				
				gameInProgress = true;
				ACLMessage msgType = regist.getSubscriptions();
				int totalplayers = regist.getNumSubscribers();
				
				ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
				msg.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
				myAgent.addBehaviour(new RoundManager( myAgent, msgType, totalplayers));
			}		
			
		}
	}
	
	/**
	 * 
	 * Class Round Manager que trata das rondas de um jogo. 
	 * Preparado para receber mensagens do tipo Propose. 
	 *
	 */
	private class RoundManager extends ProposeInitiator {
		int totalplayers;
		
		public RoundManager(Agent a, ACLMessage msg, int totalplayers) {
			super(a, msg);
			this.totalplayers = totalplayers;
		}
		
		/**
		 * Recebe Mensagens do tipo accept proposal. 
		 * Neste caso recepcao wakeup de todos os jogadores irao jogar.
		 */
		protected void handleAcceptProposal(ACLMessage accept_proposal){
			inGamePlayers.add(new Tuplo(accept_proposal.getSender()));
			System.out.println("Proposal agreed by " +  accept_proposal.getSender());
			if(inGamePlayers.size() == totalplayers){
				System.out.println("All Players Have Responded to the Call - Total Number of Players " + totalplayers );
				myAgent.addBehaviour(new Round(myAgent, totalplayers));
			}
		}
	}	
	
	/**
	 * 
	 * Class que trata de um round do jogo, ou seja Coin Set, Bet Set e Result Return.
	 *
	 */
	private class Round extends Behaviour {
		
		private static final long serialVersionUID = 1L;
		private int totalplayers;
		
		/**
		 * 
		 * @param a - Agent Controller
 		 * @param totalplayers - Total of Players in Game.
		 */
		public Round(Agent a, int totalplayers){
			this.totalplayers = totalplayers;
		}
		
		/**
		 * Desenrolar da ronda.
		 */
		public void action() {
			
			// Inicializacao das mensagens
			// ACLMessage for Player Responses
			ACLMessage newMsg;
			
			// ACLMessage for Coins
			ACLMessage showCoin = new ACLMessage(ACLMessage.PROPOSE);
			showCoin.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
			showCoin.setContent("Start");
			showCoin.setConversationId(Integer.toString(totalplayers));
			
			// ACLMessage for Bets
			ACLMessage getBet = new ACLMessage(ACLMessage.PROPOSE);
			getBet.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
			
			// ACLMessage for Results 
			ACLMessage showResult = new ACLMessage(ACLMessage.PROPOSE);
			showResult.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
			showResult.setConversationId("Result");
			showResult.setContent("");
			
			// Get Coin Cycle
			while (roundPlayed <= numRound){
				System.out.println("Starting Round Number " + roundPlayed);
				

				for (Tuplo player: inGamePlayers){
					
					showCoin.addReceiver(player.getPlayer());
					myAgent.send(showCoin);
					newMsg = myAgent.blockingReceive(gameTemplate);
					
					try {
						
						System.out.println("Received Coins in hand from Player - " + newMsg.getSender().getLocalName());
						player.setMoeda(Integer.parseInt(newMsg.getContent()));
						showCoin.removeReceiver(player.getPlayer());
						
					} catch (NullPointerException e){
						showCoin.reset();
						
						showCoin.setPerformative(ACLMessage.PROPOSE);
						showCoin.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
						showCoin.setContent("Start");
						showCoin.setConversationId(Integer.toString(totalplayers));
						showCoin.addReceiver(player.getPlayer());
						
						System.out.println("Received Error Message from Player when received Coins");			
						
						myAgent.send(showCoin);
						newMsg = myAgent.blockingReceive(gameTemplate);
						
						if(newMsg == null) {
							System.out.println(player.getPlayer().getLocalName() + " has been disqualified from the Round ! Coins not Received ");
							player.setMoeda(0);
							showCoin.removeReceiver(player.getPlayer());
						}
						else {
							player.setMoeda(Integer.parseInt(newMsg.getContent()));
							showCoin.removeReceiver(player.getPlayer());
						}
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// Fim do Ciclo Get Coin
				

				// Inicio do Ciclo betCoins 

				getBet.setContent("");
				for (Tuplo player: inGamePlayers){
					getBet.addReceiver(player.getPlayer());
					myAgent.send(getBet);
					newMsg = myAgent.blockingReceive(gameTemplate);
					
					try {
						System.out.println("Received a Bet From Player - " + newMsg.getSender().getLocalName());
						
						// New Code Added
						player.setAposta(Integer.parseInt(newMsg.getContent()));
						String newBet = getBet.getContent() + "-" + newMsg.getSender().getLocalName() + "-" + newMsg.getContent();
						getBet.setContent(newBet);
						getBet.removeReceiver(player.getPlayer());
					} catch(NullPointerException e){
						String temp = getBet.getContent();
						getBet.reset();
						
						getBet.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
						getBet.setPerformative(ACLMessage.PROPOSE);
						getBet.setContent(temp);
						
						System.out.println("Message Error Received Reasking Player for Message");
						myAgent.send(getBet);
						
						newMsg = myAgent.blockingReceive(gameTemplate);
						
						if(newMsg == null){
							System.out.println(player.getPlayer().getLocalName() + " has been disqualified from the Round ! Bet not Received ");
							player.setAposta(99);
							getBet.removeReceiver(player.getPlayer());
						}
						else {
							player.setAposta(Integer.parseInt(newMsg.getContent()));
							String newBet = getBet.getContent() + "-" + newMsg.getSender().getLocalName() + "-" + newMsg.getContent();
							getBet.setContent(newBet);
							getBet.removeReceiver(player.getPlayer());
							System.out.println("Received Bet without errors!");
						}
					}
						
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// Fim do Ciclo betCoins
				
				// Inicio do Ciclo Get Results.
				AID winner = checkResults();
				if(winner != null){
					System.out.println("The Winner of This Round is " + winner.getLocalName());
				}
				else{
					System.out.println("No One Wins This Time...Better Luck Next Time !!");
				}
				
				// Send results to players
				// Message Template is as Follow :
				// -LocalName-CoinPlayed-BetPlayed-VictoriesHeld-LocalName2-CoinsPlayed-BetPlayed-VictoriesHeld-....
				showResult.setContent(compileMessage());
				for(Tuplo player:inGamePlayers){
					showResult.addReceiver(player.getPlayer());
					myAgent.send(showResult);
					
					newMsg = myAgent.blockingReceive(gameTemplate);
					
					System.out.println("Received confirmation from " + player.getPlayer().getLocalName());
					showResult.removeReceiver(player.getPlayer());
				}
				
				
				
				showStandings();
				
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				roundPlayed ++;
			}
		}

		int roundPlayed = 1;

		/**
		 * Fim do Ciclo do Jogo, e "Wrap Up" Final.
		 */
		public boolean done() {
			showWinner();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Game Has Finished Returning to Idle ");
			gameInProgress = false;
			inGamePlayers.clear();
			myAgent.removeBehaviour(this);
			return false;
		}
		
		/**
		 * Verifica Se Existe um Vencedor da Ronda.
		 * @return Caso exista um vencedor ou nao retorn - Player AID or null
		 */
		public AID checkResults(){
			Integer total = 0;
			for (Tuplo player:inGamePlayers){
				total += player.getMoeda();
			}
			
			System.out.println("Total Of Coins Played - " + total);
			
			for (Tuplo player:inGamePlayers){
				if(player.getAposta() == total){
					player.incVictory();
					return player.getPlayer();
				}
			}		
			return null;
		}
		
		/**
		 * Mostra os resultados totais do jogo (ou seja o numero de vitorias que cada jogador tem)
		 */
		public void showStandings(){
			System.out.println(" ------------------------------------------------------ ");
			System.out.println("   ****     Current Standings     ****     ");
			for(Tuplo player: inGamePlayers){
				System.out.println(player.getPlayer().getLocalName() + " - has " + player.getVictory() + " Victories!");
			}
			System.out.println(" ------------------------------------------------------ ");
		}
		
		/**
		 * Mostra o vencedor final de um jogo (ou caso exista empate aqueles que empataram).
		 */
		public void showWinner(){
			
			ArrayList<Tuplo> winners = new ArrayList<Tuplo>();
			
			for(Tuplo player: inGamePlayers){
				if (winners.isEmpty()){
					winners.add(player);
				}
				if(player.getVictory() > winners.get(0).getVictory() && !winners.contains(player)){
					winners.clear();
					winners.add(player);
				}
				if(player.getVictory() == winners.get(0).getVictory() && !winners.contains(player)){
					winners.add(player);
				}
			}
			
			if(winners.size() > 1){
				System.out.println(" ********** Draw Game *********** ");
				for(Tuplo player: winners){
					System.out.println("Player - " + player.getPlayer().getLocalName());
				}
			}else{
				System.out.println(" ********* The Winner of the Game  **********");
				System.out.println(winners.get(0).getPlayer().getLocalName() + " with " + winners.get(0).getVictory() + " victories !!");
			}
		}
		
		
		/**
		 * Cria a Mensagem com os resultados para enviar ao jogador.
		 * @return String : -LocalName-CoinPlayed-BetPlayed-VictoriesHeld-LocalName2-CoinsPlayed-BetPlayed-VictoriesHeld-....
		 */
		public String compileMessage(){
			String msg = new String();
			for (Tuplo player:inGamePlayers){
				msg += "-"+player.toString();
			}
			return msg;
		}
	}
} 
