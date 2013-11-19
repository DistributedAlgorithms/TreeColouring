/* javac -classpath $VISIDIA_API_PATH/visidia_api.jar YourClass.java */
import visidia.simulation.process.algorithm.Algorithm; 
import visidia.simulation.process.edgestate.MarkedState; 
import visidia.simulation.process.messages.IntegerMessage; 
import visidia.simulation.process.messages.StringMessage;

public class MyRendezVous extends Algorithm 
{
	@Override
	public Object clone()
	{
        return new MyRendezVous();
	}
	
	@Override
	public void init()
	{
		java.util.Random r = new java.util.Random();
		int nbNeighbors = getArity();

		while (true)
		{
			// Randomly select a neighbor
			int neighborDoor = r.nextInt() % nbNeighbors;
			// Send synchronization messages (0 and 1)
			 for (int i = 0; i < nbNeighbors; ++i)
				 sendTo(i, new IntegerMessage(i == neighborDoor ? 1 : 0));
			// Receive a message
			boolean rendezVousAccepted = false;
			for (int i = 0; i < nbNeighbors; ++i)
			{
				IntegerMessage msg = (IntegerMessage) receiveFrom(i); 
				if ((i == neighborDoor) && (msg.value() == 1))
					rendezVousAccepted = true;
			}
			// Mark an edge and send a "Hello" message
		 	if (rendezVousAccepted == true) {
				 setDoorState(new MarkedState(true), neighborDoor); 	
	 			 sendTo(neighborDoor, new StringMessage("Hello")); 
				 receiveFrom(neighborDoor);
				 setDoorState(new MarkedState(false), neighborDoor);
		 	}
	 	}
	}
}