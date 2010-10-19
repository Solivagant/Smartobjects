import java.util.List;

/*
 * Defines a Smartobject action
 */
public class Action {
	private String name;
	private int code;
	//The idea is to fill pre and post conditions on each smartobject type
	private List<State> preConditions;
	private List<State> postConditions;
	
	public Action(String name, int code) {
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
