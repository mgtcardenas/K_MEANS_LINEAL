
// #region imports
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
// #endregion imports

public class Lineal extends Application
{
	
	// K-Means
	private static final int				Y_COORDINATE			= 250;
	private static final int				CIRCLE_SIZE				= 5;
	private static final int				NUMBER_OF_CLUSTERS		= 4;
	private static final int				NUMBER_OF_CIRCLES		= 20; // Do not change this
	private static boolean					thereWasANewMean		= false;
	private static Random					dice					= new Random();
	private static Line						line					= new Line(100, Y_COORDINATE, 700, Y_COORDINATE);
	private static Enumeration				clusters;
	private static Hashtable<Double, Paint>	meansTable				= new Hashtable<>();
	private static Hashtable<Double, Paint>	bestMeansTable			= new Hashtable<>();
	private static double					totalVariation;
	private static double					smallestTotalVariation	= Double.MAX_VALUE;
	
	// GUI
	private static Group	canvas	= new Group(line);
	private static Scene	scene	= new Scene(canvas, 800, 500);
	
	public static void main(String[] args)
	{
		launch(args);
	}// end main
	
	public static void setCircles()
	{
		
		HashSet<Integer>	circleSet	= new HashSet<Integer>();
		Circle				circle;
		
		while (circleSet.size() != NUMBER_OF_CIRCLES)
		{
			circleSet.add(dice.nextInt(601) + 100);
		}// end while
		
		for (int i : circleSet)
		{
			circle = new Circle(i, Y_COORDINATE, CIRCLE_SIZE);
			canvas.getChildren().add(circle);
		}// end for
		
	}// end setCircles
	
	public static void assignRandomClusters()
	{
		meansTable.clear();
		Circle circle;
		// Select 3 random circles and assign their coordinates with a random color to a
		// table
		while (meansTable.keySet().size() != NUMBER_OF_CLUSTERS)
		{
			
			circle = (Circle) canvas.getChildren().get(dice.nextInt(NUMBER_OF_CIRCLES) + 1);
			
			meansTable.put(circle.getCenterX(),
					Color.color(Math.random(), Math.random(), Math.random()));
		} // end while
	}// end assignRandomClusterPoints
	
	public static void paintClusters(Hashtable<Double, Paint> table)
	{
		for (int i = 1; i <= NUMBER_OF_CIRCLES; i++)
		{
			
			Circle	circle				= (Circle) canvas.getChildren().get(i);
			double	smallestDistance	= Double.MAX_VALUE;
			
			for (Double clusterMean : meansTable.keySet())
			{
				
				double distance = Math.abs(circle.getCenterX() - clusterMean);
				
				if (distance < smallestDistance)
				{
					smallestDistance = distance;
					circle.setFill(table.get(clusterMean));
				} // end if
				
			}// end for
			
		} // end for
		
	}// end paintClusters
	
	public static void calculateMeans(Hashtable<Double, Paint> table)
	{
		
		clusters = table.keys();
		while (clusters.hasMoreElements())
		{
			
			int		circlesInCluster	= 0;
			double	newMean				= 0.0;
			
			double	currentMean		= (Double) clusters.nextElement();
			Paint	clusterColor	= table.get(currentMean);
			
			for (int i = 1; i <= NUMBER_OF_CIRCLES; i++)
			{
				Circle c = (Circle) canvas.getChildren().get(i);
				if (c.getFill() == clusterColor)
				{
					circlesInCluster++;
					newMean += c.getCenterX();
				} // end if
			} /// end for
			
			newMean = newMean / (double) circlesInCluster;
			
			if (!table.containsKey(newMean))
			{
				table.remove(currentMean);
				table.put(newMean, clusterColor);
				thereWasANewMean = true;
			} // end if
			
			System.out.println("Center XMean: " + newMean);
			
		} // end while
		
		System.out.println("-------------------");
		
	}// end calculateClustersMeans
	
	public static void cluster()
	{
		assignRandomClusters();
		do
		{
			thereWasANewMean = false;
			paintClusters(meansTable);
			calculateMeans(meansTable);
		}
		while (thereWasANewMean);
	}// end cluster
	
	public static double getVariation()
	{
		
		totalVariation = 0.0;
		
		clusters = meansTable.keys();
		while (clusters.hasMoreElements())
		{
			
			double	clusterVariation	= 0.0;
			double	smallestDistance	= Double.MAX_VALUE;
			double	largestDistance		= Double.MIN_VALUE;
			
			double	mean			= (Double) clusters.nextElement();
			Paint	clusterColor	= meansTable.get(mean);
			
			for (int i = 1; i <= NUMBER_OF_CIRCLES; i++)
			{
				Circle c = (Circle) canvas.getChildren().get(i);
				
				if (c.getCenterX() < smallestDistance && c.getFill() == clusterColor)
					smallestDistance = c.getCenterX();
				
				if (c.getCenterX() > largestDistance && c.getFill() == clusterColor)
					largestDistance = c.getCenterX();
			} // end for
			
			clusterVariation	= Math.abs(largestDistance - smallestDistance);
			totalVariation		+= clusterVariation;
		} // end while
		
		System.out.println("The Variation is : " + totalVariation);
		return totalVariation;
		
	}// end getVariation
	
	public static void kMeans(int attempts)
	{
		for (int i = 0; i < attempts; i++)
		{
			System.out.println("---");
			System.out.println(i);
			System.out.println("---");
			cluster();
			double variation = getVariation();
			if (variation < smallestTotalVariation)
			{
				bestMeansTable			= meansTable;
				smallestTotalVariation	= variation;
			} // end if
		} // end for
	}// end kMeans
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		
		primaryStage.setTitle("Static Lineal K-Means");
		
		primaryStage.setScene(scene);
		
		setCircles();
		
		// 10 is the number of times the clustering will be done
		kMeans(10);
		System.out.println("Smallest Total Variation was: " + smallestTotalVariation);
		paintClusters(bestMeansTable);
		
		primaryStage.show();
	}// end start
	
}// end Lineal
