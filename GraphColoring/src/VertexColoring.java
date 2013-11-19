import java.util.Random;

import visidia.simulation.process.algorithm.SynchronousAlgorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;
import visidia.simulation.process.messages.StringMessage;

public class VertexColoring extends SynchronousAlgorithm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isFirstRound = true;
	private int label;
	private int sixColorRoundNumber = 0;
	private int six2TreeRoundNumber = 5;
	private boolean isRoot;
	private boolean six2TreePending;

	@Override
	public Object clone() {
		return new VertexColoring();
	}

	public static int calculateColour(int myColour, int parentColour)
	{
		int z = (myColour ^ parentColour);
		int l =	(int)Math.log(Math.max(myColour, parentColour))+1;
		int i = 0;
		for (; i < l; i++)
		{
			if ((z & (1 << i)) > 0)
			{
				System.out.println("index " + i);
				break;
			}
		}
		int result = i*2;
		if ((myColour & (1 << i)) > 0)
			result++;
		return result;
	}

	@Override
	public void init() {

		// Here we color all edges for the first time
		// After this we have 7 colors
		if (isFirstRound) {
			label = getId();
			putProperty("label", "" + changeIDIntoColor(label));
			isFirstRound = false;
		}

		//  6-color procedure
		while (sixColorRoundNumber < 6) {

			if (!isRoot) {
				// // The root assigns itself label 0 and makes no other
				// operations
				if (label == 0) {
					isRoot = true;
					sendTo(0, new StringMessage("" + label));
				}
				// We start from 1 because 0 is our parent
				for (int i = 1; i < getArity(); i++) {
					sendTo(i, new StringMessage("" + label));
				}

				// We use it as breakpoint, visidia has sync problems without
				// it.
				nextPulse();

				if (anyMsg()) {
					Door dr = new Door();
					Message messageFromFather = receive(dr);
					int fathers_color = Integer.parseInt(messageFromFather
							.toString());
					// // We need to make here 7-9 lines of algorithm 6 color, some
					// // bitwise operations
					// if (fathers_color == 0) {
					// 	fathers_color = 1;
					// }
					label = calculateColour(label, fathers_color);
					putProperty("label", "" + changeIDIntoColor(label));
				}
			}
			if (sixColorRoundNumber == 5) {
				six2TreePending = true;
			}
			sixColorRoundNumber++;
		}
		// six2tree procedure
		// while (six2TreePending && six2TreeRoundNumber > 2) {
		// 	shiftDown();
		// 	nextPulse();
		// 	paintToFathersColor();
		// 	six2TreeRoundNumber--;
		// 	nextPulse();
		// }
	}

	private void shiftDown() {
		// Choose a new random color for root and send to children
		if (isRoot) {
			Random rand = new Random();
			int newColor = rand.nextInt(3);
			while (newColor == label) {
				newColor = rand.nextInt(3);
			}
			label = newColor;
			putProperty("label", changeIDIntoColor(newColor));
			sendAll(new StringMessage("" + label));
		} else {
			// If you're not the root, send only to children
			for (int i = 1; i < getArity(); i++) {
				sendTo(i, new StringMessage("" + label));
			}
		}
	}

	private void paintToFathersColor() {
		if (anyMsg()) {
			Door dr = new Door();
			Message mes = receive(dr);
			int fathers_color = Integer.parseInt(mes.toString());
			label = fathers_color;
			putProperty("label", changeIDIntoColor(label));
		}
	}

	/**
	 * Utility method for changing Id of edge into color
	 * 
	 * @param color
	 * @return
	 */
	private String changeIDIntoColor(int color) {
		switch (color) {
		case 0:
			return "A";
		case 1:
			return "E";
		case 2:
			return "F";
		case 3:
			return "I";
		case 4:
			return "J";
		case 5:
			return "N";
		default:
			return "P";
		}
	}
}
