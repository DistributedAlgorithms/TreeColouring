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
	private int previousColor;
	private boolean isRoot;
	private boolean six2TreePending = false;

	@Override
	public Object clone() {
		return new VertexColoring();
	}

	public static int calculateColour(int myColour, int parentColour) {
		int z = (myColour ^ parentColour);
		int l = (int) Math.log(Math.max(myColour, parentColour)) + 1;
		int i = 0;
		for (; i < l; i++) {
			if ((z & (1 << i)) > 0) {
				break;
			}
		}
		int result = i * 2;
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

		// 6-color procedure
		while (sixColorRoundNumber < 6) {

			if (!isRoot) {
				// // The root assigns itself label 0 and makes no other
				// operations
				if (label == 0 && sixColorRoundNumber == 0) {
					isRoot = true;
					sendTo(0, new StringMessage("" + label + " jestem rootem"));
				}
				// We start from 1 because 0 is our parent
				for (int i = 1; i < getArity(); i++) {
					sendTo(i, new StringMessage("" + label
							+ " jestem nie rootem"));
				}

				// We use it as breakpoint, visidia has sync problems without
				// it.
				nextPulse();

				if (anyMsg()) {
					Door dr = new Door();
					Message messageFromFather = receive(dr);
					int fathers_color = Integer.parseInt(messageFromFather
							.toString().substring(0, 1));
					label = calculateColour(label, fathers_color);
					putProperty("label", "" + changeIDIntoColor(label));
				}
			}
			sixColorRoundNumber++;
			System.out.println("sixColorRound nr " + sixColorRoundNumber);
		}
//		nextPulse();
//		if (sixColorRoundNumber == 6) {
//			
//			six2TreePending = true;
//		}
//		nextPulse();
//		// six2tree procedure
//		while (six2TreePending && six2TreeRoundNumber > 2) {
//			System.out.println("six2treeStart"+six2TreePending+" "+getId()+" "+six2TreeRoundNumber);
//			shiftDown();
//			nextPulse();
//			paintToFathersColor();
//			firstFree();
//			six2TreeRoundNumber--;
//			nextPulse();
//		}
	}

	private void firstFree() {
		if (six2TreeRoundNumber == label) {
			sendTo(0, new StringMessage("dej kamienia"));
			nextPulse();
			while (anyMsg()) {
				Door dr = new Door();
				Message messageFromChild = receive(dr);
				sendTo(dr.getNum(), new StringMessage("" + label));
			}
			nextPulse();
			if (anyMsg()) {
				Door dr = new Door();
				Message messageFromFather = receive(dr);
				label = chooseFreeColor(previousColor,
						Integer.parseInt(messageFromFather.toString()));

			}
		}
	}

	private int chooseFreeColor(int firstColor, int secondColor) {
		if (firstColor != 0 && secondColor != 0) {
			return 0;
		} else if (firstColor != 1 && secondColor != 1) {
			return 1;
		} else {
			return 2;
		}
	}

	private void shiftDown() {
		// Choose a new random color for root and send to children
		if (getId() == 0) {
			sendAll(new StringMessage("" + label));

			switch (label) {
			case 0:
				label = 1;
				break;
			case 1:
				label = 2;
				break;
			}
			putProperty("label", changeIDIntoColor(label));

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
			previousColor = label;
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
