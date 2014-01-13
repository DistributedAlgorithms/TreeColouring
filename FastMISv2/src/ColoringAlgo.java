public class ColoringAlgo {
   private static ColoringAlgo instance = null;
   private int initialNumberOfNodes; //ile jest wezlow ogolnie
   private int numberOfNodes;  //ile jest wezlow w aktaulnej rundzie
   private int numberOfMisNodes;  //ile wezlow jest w ktoryms z MISow
   private int roundNumber;
   protected ColoringAlgo(int n) {
      // Exists only to defeat instantiation.
      initialNumberOfNodes = n;
      numberOfNodes = n;
      numberOfMisNodes = 0;
      roundNumber = 0;
   }
   public static ColoringAlgo getInstance(int n) {
      if(instance == null) {
         instance = new ColoringAlgo(n);
      }
      return instance;
   }
   public synchronized void joinedMis()
   {
      numberOfMisNodes++;
      finishedRound();
   }
   public synchronized void finishedRound()
   {
      if (numberOfNodes > 0)
         numberOfNodes--;
   }
   public synchronized void startRound()
   {
      if (numberOfNodes != initialNumberOfNodes - numberOfMisNodes)
         roundNumber++;
      numberOfNodes = initialNumberOfNodes - numberOfMisNodes;
   }
   public boolean didFinishRound()
   {
      System.out.println("number of active nodes " + numberOfNodes);
      System.out.println("number of mis nodes " + numberOfMisNodes);
      
      return (numberOfNodes == 0) || didFinishColoring();
   }
   public synchronized  int getRoundNumber()
   {
      return roundNumber;
   }
   public synchronized boolean didFinishColoring()
   {
      return (numberOfMisNodes == initialNumberOfNodes);
   }
}