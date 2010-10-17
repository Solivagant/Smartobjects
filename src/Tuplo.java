import jade.core.AID;

/**
 * Descreve um jogador em jogo, atraves do seu AID, das moedas na mao, da sua aposta e quantas vitorias teve.
 */
public class Tuplo {
	private AID player;
	private Integer moeda;
	private Integer aposta;
	private Integer victory;
	
	/**
	 * Construtor, recebe o AID (identificador de um agente) de um jogador.
	 * @param player o AID de um jogador
	 */
	public Tuplo(AID player){
		this.player = player;
		this.moeda = 0;
		this.aposta = 0;
		this.victory = 0;
	}
	
	/**
	 * Atribui o valor do numero de moedas deste jogador.
	 * @param moeda o valor a atribuir
	 */
	public void setMoeda(Integer moeda){
		this.moeda = moeda;
	}
	
	/**
	 * Atribui o valor da aposta deste jogador.
	 * @param aposta
	 */
	public void setAposta(Integer aposta){
		this.aposta = aposta;
	}
	
	/**
	 * Devolve o numero de moedas deste jogador.
	 * @return Integer o numero de moedas deste jogador
	 */
	public Integer getMoeda(){
		return moeda;
	}
	
	/**
	 * Devolve a aposta deste jogador.
	 * @return Integer a aposta
	 */
	public Integer getAposta(){
		return aposta;
	}
	
	/**
	 * Devolve o AID deste jogador.
	 * @return AID a identificacao deste jogador
	 */
	public AID getPlayer(){
		return player;
	}
	
	/**
	 * Coloca a vitoria a 0.
	 */
	public void resetVictory(){
		this.victory = 0;
	}
	
	/**
	 * Incrementa o contador de vitorias.
	 */
	public void incVictory(){
		this.victory++;
	}
	
	/**
	 * Devolve quantas vitorias este jogador ja teve.
	 * @return Integer o numero de vitorias
	 */
	public Integer getVictory(){
		return victory;
	}
	
	/**
	 * Devolve uma representacao em String deste jogador.
	 */
	public String toString(){
		return player.getLocalName()+"-"+Integer.toString(this.getMoeda())+"-"+Integer.toString(this.getAposta())+"-"+Integer.toString(this.getVictory());
	}

}
