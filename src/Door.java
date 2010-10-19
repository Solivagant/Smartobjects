import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * SmartObject that defines a door
 * Three states: locked, closed, open
 * Three actions: unlock, open, close, lock
 */
@SuppressWarnings("serial")
public class Door extends SmartObject {
	
	public Door(int state) {
		super(state);

	}
	
	protected void setup(){
		super.setup();
		
		private static final int INERT = 0;
		public static final int LOCKED = 1;
		public static final int CLOSED = 2;
		public static final int OPEN = 3;
	}


}
