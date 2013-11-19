import java.util.*;

public class Bits {

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
	public static void main(String[] args) {
		System.out.println("Witaj!");
		System.out.println("Dzisiaj jest: "+(new Date()));
		int x = 100;
		int y = 212;
		int z = (x ^ y);
		int l =	(int)Math.log(Math.max(x, y))+1;
		String xstring = Integer.toBinaryString(x);
		String ystring = Integer.toBinaryString(y);
		System.out.println("x " + xstring + " y " + ystring + " z " + Integer.toBinaryString(~(x ^ y)));
		
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
		if ((x & (1 << i)) > 0)
			result++;
		
		System.out.println("result " + Integer.toBinaryString(result));
				

	}
}