import java.util.Random;

import visidia.simulation.process.algorithm.SynchronousAlgorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;
import visidia.simulation.process.messages.StringMessage;

public class FastMISV2 extends SynchronousAlgorithm {

	private static final long serialVersionUID = 1L;
	private boolean isFirstRound = true;
	private boolean isNeighbourOfSet = false;
	private boolean isInSet = false;
	private Random randGenerator = new Random();

	@Override
	public Object clone() {
		return new FastMISV2();
	}

	@Override
	public void init() {
		for (int i = 0; i < getNetSize(); i++) {
			if (isFirstRound) {
				putProperty("label", "Q");
				isFirstRound = false;
			}
			if (!isNeighbourOfSet && !isInSet) {
				int randomValue = randGenerator.nextInt(2);
				boolean canJoinMIS = true;

				sendAll(new StringMessage("" + randomValue));
				nextPulse();
				//Oczekuje na wiadomosci od sasiadow i sprawdzam, czy ktos nie ma tez 0
				while (anyMsg()) {
					Door dr = new Door();
					Message messageFromFather = receive(dr);
					int neighbourValue = Integer.parseInt(messageFromFather
							.toString());
					if (randomValue >= neighbourValue) {
						canJoinMIS = false;
						break;
					}
				}
				nextPulse();
				//Dodaje sie i blokuje sasiadow w tej rundzie
				if (canJoinMIS) {
					putProperty("label", "A");
					sendAll(new StringMessage("block"));
					isInSet = true;
				}
				nextPulse();
				//Jestem sasiadem, zostaje zablokowany
				if (anyMsg()) {
					isNeighbourOfSet = true;
					putProperty("label", "B");
				}
			}
		}
	}
}