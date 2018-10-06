//Packages
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class HillClimbing {
	
	//Attributes
	static ArrayList<Point> cloud;
	static ArrayList<Integer[]> neighbors;
	static ArrayList<Double> density;
	static ArrayList<Integer> parent;
	static ArrayList<Integer> label;
	
	//Comparator class
	public class ArrayListComparator implements Comparator<Integer>{ //tool used to argsort an Arraylist
		
		public ArrayList<Double> list;
		
		public ArrayListComparator(ArrayList<Double> list){
			this.list = list;
		}
		
		public ArrayList<Integer> indexes(){
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			for (int i=0; i<list.size();i++) indexes.add(Integer.valueOf(i));
			return indexes;
		}
		
		@Override
		public int compare(Integer i1, Integer i2){
			return(list.get(i1).compareTo(list.get(i2)));
		}
	}
	
	//No constructors
	
	//Methods
	void readData (String filename) throws FileNotFoundException{ //reads the data from a file, fills cloud attribute
		
		Scanner sc = new Scanner(new File(filename));
		//sc.
		while (sc.hasNext()){
			Point p  = new Point(sc);
			cloud.add(p);
		}
	}
	
	void computeNeighbors (int k){ //computes kNN graph
		
		int n = cloud.size();
		for (int j=0; j<n; j++){
			//Initializing
			ArrayList<Double> distances = new ArrayList<Double>();
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			int i = 0;
			Point p = cloud.get(j);
			for (Point q : cloud){
				distances.add(Point.sqDist(p, q));
				indexes.add(i);
				i = i + 1 ;
			}
			Collections.sort(indexes, new ArrayListComparator(distances));
			Integer[] neighbors_p = new Integer[k];
			for (int l=1;l<=k;l++){
				neighbors_p[l-1] = indexes.get(l);
			}
			neighbors.add(neighbors_p);
		}
	}
	
	void computeDensity (int k){ //computes density function
		
		int n = cloud.size();
		for (int i=0; i<n;i++){
			double sum = 0.0;
			Integer[] neighbors_p = neighbors.get(i);
			for (int j=0; j<k; j++){
				sum = sum + Point.sqDist(cloud.get(i), cloud.get(neighbors_p[j]));
			}
			density.add(1/Math.sqrt(sum/k));
		}
	}
	
	void computeForest (int k){ //computes each point's parent
		
		int n = cloud.size();
		for (int i=0; i<n; i++){
			int peak = i;
			Integer[] neighbors_i = neighbors.get(i);
			for (int j=0; j<k; j++){
				if (density.get(neighbors_i[j]) > density.get(peak)) {
					peak = neighbors_i[j];
				}
			}
			parent.add(peak);
		}
	}
	
	int getLabel(int i){ //recursive function that returns the label of a given point
		
		if (parent.get(i).intValue() == i) return i;
		else{
			return getLabel(parent.get(i).intValue());
		}
	}
	
	void computeLabels(){ //extracts the labels from parents array
		
		for (int i=0; i<cloud.size(); i++){
			label.add(getLabel(i));
		}
	}
	
	void computePersistence (int k){
		
	}
	
	@SuppressWarnings("resource")
	public static void main( String[] args) throws FileNotFoundException{
		
		int kDensity, kGraph ;
		double tau;
		HillClimbing c = new HillClimbing();
		cloud = new ArrayList<Point>();
		neighbors = new ArrayList<Integer[]>();
		density = new ArrayList<Double>();
		parent = new ArrayList<Integer>();
		label = new ArrayList<Integer>();
		int input = 0;
		Scanner in = new Scanner(System.in);
		String filename;
		while ((input<1) || (input>3)){
			input = in.nextInt();
		}
		if (input == 1) {
			filename = "test.xy";
			// parameters for test.xy
			kDensity = 10;
			kGraph = 5;
			tau = 0.35;
		}
		else if (input == 2) {
			filename = "crater.xy";
			// parameters for crater.xy
			kDensity = 50;
			kGraph = 15;
			tau = 2.;
		}
		else {
			filename = "spirals.xy";
			// parameters for spirals.xy
			kDensity = 100;
			kGraph = 30;
			tau = 0.03;
		}
		c.readData(filename);
		c.computeNeighbors(kDensity);
		c.computeDensity(kDensity);
		c.computeForest(kGraph);
		c.computeLabels();
		
		new ClusteringWindow(cloud, label, neighbors, kGraph);
	}
	
}
