import java.util.Random;

import visidia.simulation.process.algorithm.SynchronousAlgorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;
import visidia.simulation.process.messages.StringMessage;

public class FastMIS extends SynchronousAlgorithm {

	private static final long serialVersionUID = 1L;
	private Random  randGenerator = new Random();
	private boolean hasFinished;
	private boolean isInMis;
	private ColoringAlgo coloringAlgo;
	@Override
	public Object clone() {
		return new FastMIS();
	}

	@Override
	public void init() {
		coloringAlgo = ColoringAlgo.getInstance(getNetSize());
		while (!isInMis)
		{
			//liczymy MISA
			singleRound();
			//informujemy singleton
			if (isInMis)
				coloringAlgo.joinedMis();
			else
				coloringAlgo.finishedRound();
			//czekamy az inne skoncza
			//liczba next pulsow w 1 rundzie
			nextPulse();
			if (!isInMis)
			{
				System.out.println("siema tu " + getId() + " czekam na koniec rundy");
				while(!coloringAlgo.didFinishRound()){
					singlePhasePulse();
				}
				nextPulse();
				System.out.println("siema tu " + getId() + " startuje do kolejnej rundy " + getColor());
			}
		}
	}		
	
	//czyscimy zmienne i wywolujemy MISa
	private void singleRound()
	{
		hasFinished = false;
		isInMis = false;
		coloringAlgo.startRound();
		putProperty("label", "Q");
			
		while (!hasFinished) {
			singlePhase();
		} 
	}
	//wywolujemy tyle next pulsow co w singleRound
	private void singlePhasePulse()
	{
		nextPulse();
		nextPulse();
		nextPulse();
		nextPulse();
		nextPulse();
	}

	private void singlePhase() {
		System.out.println("single phase " + getColor());

		int myDegree = getDegree();
		int randomValue = randGenerator.nextInt(2 * myDegree + 1);
		boolean canJoinMIS = randomValue == 0;

		//Wylosowalem mozliwosc dodania do MIS, rozsylam sygnal
		if (canJoinMIS) {
			sendAll(new StringMessage(myDegree + " " + getId()));
		}
		nextPulse();
		//Sprawdzam czy moi sasiedzi nie chca sie dodac do MIS
		while (anyMsg() && canJoinMIS) {
			Door dr = new Door();
			Message challengeMessage = receive(dr);
			String[] challengeData = challengeMessage.toString().split(" ");
			int neighborDegree = Integer.parseInt(challengeData[0].toString());
			int neighborID = Integer.parseInt(challengeData[1].toString());
			if (isMyNeighborHigher(myDegree, neighborDegree, neighborID)) {
				canJoinMIS = false;
			}
		}
		nextPulse();
		//Dodaje sie i blokuje sasiadow w tej rundzie
		if (canJoinMIS) {
			sendAll(new StringMessage("block"));
			putProperty("label", getColor());
			hasFinished = true;
			isInMis = true;
		}
		nextPulse();
		//Jestem sasiadem, zostaje zablokowany
		if (anyMsg()) {
			hasFinished = true;
			isInMis = false;
			putProperty("label", "B"); 
		}
	}

	private String getColor()
	{
		int misColor = coloringAlgo.getRoundNumber() + 67;
		return Character.toString((char)misColor);
	}

	/**
	 * W razie konfliktu (dwa sasiadujace wierzcholki chca sie dodac do MIS)
	 * metoda porownuje liczbe sasiadow i ID jesli liczba sasiadow jest rowna
	 * @param myDegree
	 * @param neighborDegree
	 * @param neighborID
	 * @return
	 */
	private boolean isMyNeighborHigher(int myDegree, int neighborDegree,
			int neighborID) {
		return neighborDegree > myDegree
				|| (neighborDegree == myDegree && neighborID > getId());
	}

	/**
	 * Wysylamy wiadomosc od siebie i oczekujemy na wiadomosci od niepoblokowanych sasiadow
	 * Zliczamy odpowiedzi i mamy swoj stopien
	 * (Potrzebne do pozniejszych rund, bo getArity() zawsze zwroci wszystkich sasiadow)
	 * @return
	 */
	private int getDegree() {
		int neighbors = 0;
		sendAll(new StringMessage("deg"));
		nextPulse();
		while (anyMsg()) {
			receive(new Door());
			neighbors++;
		}
		nextPulse();
		return neighbors;
	}
}