/*
 * Defines a Smartobject state
 * parallel behaviours para controlar posiçao (agente controlador)
 * 
 */
public class State {
	private String name;
	private int code;
	
	public State(String name, int code) {
		super();
		this.name = name;
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	
}
