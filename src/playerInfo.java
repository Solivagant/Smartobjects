
/**
 * 
 * Funcao auxiliar para o agente Chuck Norris.
 *
 */
public class playerInfo {
	
	// Variaveis para Guardar Coin e Bet.
	private Integer coin;
	private Integer bet;
	
	/**
	 * Inicializa as variaveis a zero.
	 */
	public playerInfo(){
		this.coin = 0;
		this.bet = 0;
	}
	
	/**
	 * Cria um novo objecto playerInfo.
	 * @param coin - Coin Na mao do Jogador.
	 * @param bet - Aposta do Jogador.
	 */
	public playerInfo (Integer coin, Integer bet) {
		this.coin = coin;
		this.bet = bet;
	}
	
	/**
	 * Cria um novo objecto playerInfo.
	 * @param coin - Coin Na mao do Jogador.
	 * @param bet - Aposta do Jogador.
	 */
	public playerInfo (String coin, String bet){
		this.coin = Integer.parseInt(coin);
		this.bet = Integer.parseInt(bet);
	}
	
	/**
	 * Actualiza o valor do coin com um novo valor.
	 * @param coin - Coin na mao do jogador.
	 */
	public void setCoin(Integer coin){
		this.coin = coin;
	}
	
	/**
	 * Actualiza o valor do bet com um novo valor.
	 * @param bet - Novo valor bet a Actualizar.
	 */
	public void setBet(Integer bet){
		this.bet = bet;
	}
	
	/**
	 * Retorna o bet do jogador.
	 * @return Integer - Bet do Jogador.
	 */
	public Integer getBet(){
		return bet;
	}
	
	/**
	 * Retorna o coin do jogador.
	 * @return Integer - Coin do Jogador.
	 */
	public Integer getCoin(){
		return coin;
	}

}
