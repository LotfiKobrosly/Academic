import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.Vector;
import java.time.LocalTime;



class Simplex {
	float val;
	int dim;
	TreeSet<Integer> vert;

	Simplex(Scanner sc){
		val = sc.nextFloat();
		dim = sc.nextInt();
		vert = new TreeSet<Integer>();
		for (int i=0; i<=dim; i++)
			vert.add(sc.nextInt());
	}

	public String toString(){
		return "{val="+val+"; dim="+dim+"; "+vert+"}\n";
	}
	

}

public class ReadFiltration {
	
	static void sort(Vector<Simplex> F){ // Insertion sort of the vector simplices, according 
										 // to their val attribute
		int l = F.size();
		for (int i=1; i<l;i++){
			Simplex s = F.get(i);
			int j = 0;
			while (F.get(j).val < s.val) j++;
			F.remove(i);
			F.insertElementAt(s, j);
		}
	}
	
	static ArrayList<Integer>[] buildBoundaryMatrix(Vector<Simplex> F){ //Sparse representation here
		int n = F.size();
		ArrayList<Integer>[] boundMat = new ArrayList[n];
		int i;
		for (i=0; i<n; i++) boundMat[i] = new ArrayList<Integer>();
		for (i=0; i<n; i++){
			Simplex s = F.get(i);// For easier access to the simplex, reduces
								 // time of call to function get
			
			// Only adding the indexes of the elements that equal 1
			for (int j=i+1; j<n; j++){
				TreeSet<Integer> t = F.get(j).vert;
				if (s.vert.size() + 1 == t.size()){
					Iterator<Integer> iter = s.vert.iterator(); //iterator on vert elements
					boolean b = true;
					while (iter.hasNext()){
						Integer u = iter.next();
						b = b && t.contains(u);
					}
					if (b){ // condition that verifies that every vertex of s.vert is in t, and since we necessarily 
						   //  have s.vert != t with the size condition, it means t.vert is a facet of t
						boundMat[j].add(i);}}}
			}
		return boundMat;
	}

	static Vector<Simplex> readFiltration (String filename) throws FileNotFoundException {
		Vector<Simplex> F = new Vector<Simplex>();
		Scanner sc = new Scanner(new File(filename));
		sc.useLocale(Locale.US);
		while (sc.hasNext())
			F.add(new Simplex(sc));
		sc.close();
		sort(F);
		return F;
	}
	
	static int low(ArrayList<Integer>[] M, int i){ // Returns the greatest l such that M[i][j] != -1
									  
		int l = -1;
		if (M[i].size() > 0){
			for (Integer k : M[i])
				if (k.intValue() > l) l = k.intValue();
		}
		return l;
	}
	
	static void reduce(ArrayList<Integer>[] M){ // Reducing the boundary matrix using the original algorithm (task 2)
		for (int i=1; i<M.length; i++){ // Doing this step for all columns
			
			if (low(M,i) != -1){ // Checks that the column is not already all zeros
				int j = 0;
				while(j<i){
					int l = low(M,i);
					if ((low(M,j) == l) && (l != -1)){ // Rechecking, we may have zeros 
						for (Integer k : M[j]){
							Integer K = Integer.valueOf(k);
							if (!M[i].remove(K)) M[i].add(K);}
						j = 0;
						}
					else j++;}}}
	}
	
	static void reduceOptimized(ArrayList<Integer>[] M, Vector<Simplex> F){ // Improving the complexity to m³ (task 3)
		
		for (int i=1; i<M.length; i++){
			ArrayList<Integer> vectors = new ArrayList<Integer>();
			for (int j=0; j<i; i++)
				if ((M[j].size()>0) && (F.get(j).dim == F.get(i).dim)) vectors.add(Integer.valueOf(j)); // This way, we have the columns of which column i is potentially just a linear sum, and they are all linearly independent
			for (int j=0; j<vectors.size(); j++){
				
			}
			
		}
		
	}
	
	static ArrayList<String> barcode(ArrayList<Integer>[] M, Vector<Simplex> F){
		int m = M.length;
		ArrayList<Integer> list = new ArrayList<Integer>(); // Will contain beginnings of the intervals of the sommands
		ArrayList<String> res = new ArrayList<String>(); // Will contain the lines to be output in a text file
		for (int i=0; i<m; i++){
			if (M[i].size() == 0){
				list.add(i);
			}
			else{
				int j = low(M,i);
				if (list.remove(Integer.valueOf(j))){
					res.add(F.get(j).dim + " " + F.get(j).val + " " + F.get(i).val + "\n");
				}
			}
		}
		for (Integer k : list){
			res.add(F.get(k).dim + " " + F.get(k).val + " inf \n");
		}
		return res;
	}
	
	static void writeFile(ArrayList<String> text, String filename) throws IOException{
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		for (String s : text){
			writer.write(s);
		}
		writer.close();
	}

	public static void main(String[] args) throws IOException {
		String filename = "filtration_A.txt";
		/*
		if (args.length != 1) {
			System.out.println("Syntax: java ReadFiltration <filename>");
			System.exit(0);
		}
			*/
		LocalTime x = LocalTime.now();
		double s = x.getSecond() * Math.pow(10, 9) + x.getNano();
		Vector<Simplex> F = readFiltration(filename);
		System.out.println("Dataset size is "+ F.size());
		ArrayList<Integer>[] M = buildBoundaryMatrix(F);
		reduce(M);
		ArrayList<String> res = barcode(M,F);
		String output = "output_" + filename;
		writeFile(res,output);
		x = LocalTime.now();
		s = s - (x.getSecond() * Math.pow(10, 9) + x.getNano());
		s = s * Math.pow(10, 6);
		System.out.println("For file " + filename + " time="+ s + " ms");
		
	}
}
