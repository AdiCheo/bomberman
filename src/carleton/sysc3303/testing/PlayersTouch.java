package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import carleton.sysc3303.client.RunBot;
import carleton.sysc3303.server.Run;

public class PlayersTouch {

	@Before
	public void setUp() throws Exception {
		Run server = new Run();
		RunBot bot = new RunBot();
		
		//somehow run server and bots here
	}

	@After
	public void tearDown() throws Exception {
		//terminate server
	}

	@Test
	public void test() {
		//wait for collision...
		
		//when collision happens
			//check if collision happened as expected
	}

}
