import jade.lang.acl.ACLMessage;


public class SimpleAgent extends BaseAgent {
	
	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
		ACLMessage type = new ACLMessage(ACLMessage.REQUEST);
		type.addReceiver(controller);
		send(type);
	}

}
