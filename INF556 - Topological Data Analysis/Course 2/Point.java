import java.util.Scanner;

public class Point {
	
	//Attributes
	double[] coords;
	
	//Constructor
	Point(Scanner sc){
		String line = sc.nextLine();
		String[] tokens = line.replaceFirst("[ |\\t]+", "").split("[ |\\t]+");
		int d = tokens.length;  // number of dimensions
		coords = new double[d];
		for (int i=0; i<d; i++)
			coords[i] = Double.parseDouble(tokens[i]);
	}
	
	//Methods
	static double sqDist(Point p, Point q){
		double dist = 0.0;
		int d = p.coords.length;
		if (d != q.coords.length) {throw new Error("Dimensiosn are not equal");}
		else {
			for (int i=0; i<d; i++ ){
				dist = dist + Math.pow(p.coords[i] - q.coords[i], 2);
			}
			return(dist);
		}
	}
	
	public String toString(){
		String s = "(";
		int d = this.coords.length;
		for (int i=0; i<d; i++) s = s + this.coords[i] + ",";
		return( s + ")");
	}
}
