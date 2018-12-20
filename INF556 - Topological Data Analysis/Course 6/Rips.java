import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.TreeSet;


class Point {
	double[] coords;
	
	Point(Scanner sc){
		String line = sc.nextLine();
		String[] tokens = line.replaceFirst("[ |\\t]+", "").split("[ |\\t]+");
		int d = tokens.length;  // nombre de dimensions
		coords = new double[d];
		for (int i=0; i<d; i++)
			coords[i] = Double.parseDouble(tokens[i]);
	}

	public String toString() {
		String res = "(";
		for (int i=0; i<coords.length-1; i++)
			res += coords[i] + ", ";
		return res + coords[coords.length-1] + ")";
	}
	
	static double sqDist (Point a, Point b) {
		double res = 0;
		for (int i=0; i<a.coords.length; i++)
			res += (a.coords[i] - b.coords[i]) * (a.coords[i] - b.coords[i]);
		return res;
	}
	
}


class Simplex {
	double val;
	int dim;
	TreeSet<Integer> vert;

	Simplex(double v, int d, int[] V){
		val = v;
		dim = d;
		vert = new TreeSet<Integer>();
		for (int i : V)
			vert.add(i);
	}

	public String toString(){
		String s = val + " " + dim + " ";
		for (int v : vert)
			s += v + " ";
		return s;
	}
}


public class Rips {
	
	ArrayList<Point> cloud = new ArrayList<Point>();
	
	void readData (String filename) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(filename));
		while (sc.hasNext())
			cloud.add(new Point(sc));
		sc.close();
	}

	void buildRips () {
		for (int i=0; i<cloud.size(); i++) {
			System.out.println(new Simplex(0, 0, new int[]{i}));
			for (int j=i+1; j<cloud.size(); j++) {
				double val2 = Point.sqDist(cloud.get(i), cloud.get(j));
				System.out.println(new Simplex(Math.sqrt(val2), 1, new int[]{i,j}));
				for (int k=j+1; k<cloud.size(); k++) {
					double val3 = Math.max(Math.max(val2, Point.sqDist(cloud.get(i), cloud.get(k))), Point.sqDist(cloud.get(j), cloud.get(k)));
					System.out.println(new Simplex(Math.sqrt(val3), 2, new int[]{i,j,k}));
				}
			}
		}
	}

	public static void main (String[] args) throws FileNotFoundException {
		if (args.length != 1) {
			System.out.println("Syntax: java Rips <cloud_file>");
			return;
		}
		Rips R = new Rips();
		R.readData(args[0]);
		R.buildRips();
	}

}
