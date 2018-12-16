import java.util.List;

import Jama.Matrix;

import Jcg.geometry.Point_2;
import Jcg.geometry.Point_3;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;

/**
 * Implementation of the planar (2D) Tutte barycentric method, based on the resolution of 2 linear systems
 * 
 * @author Luca Castelli Aleardi, Ecole Polytechnique
 * @version 2017
 */
public class MeshParameterization_Jama extends TutteLayout2D {

	/** vertex numbering in the sub-graph consisting of inner vertices (array of size 'n') */
	public int[] vertexOrder;
	
	/**
	 * Initialize the parameterization
	 */	
	public MeshParameterization_Jama(Polyhedron_3<Point_3> mesh, int face) {
		super(mesh, face); // class the constructor of the ancestor class
		
		vertexOrder=new int[n];
		int counter=0; // counter for inner vertices
		for(int i=0;i<n;i++) {
			if(this.isInside[i]==true) {
				vertexOrder[i]=counter; // store the permutation between vertex indices: from G to G1 (the sub-graph consisting of inner vertices)
				counter++;
			}
		}
	}

	/**
	 * Compute the Tutte drawing of a planar graph, by iteratively
	 * computing barycenters of neighboring vertices <p>
	 * <p>
	 * The algorithm stops when a given convergence condition is reached
	 */	
	public void computeLayout(double tolerance) {
		if(this.nInnerVertices==0) // nothing to compute: there are no inner vertices (all vertices belong to the boundary cycle)
			return;
		
		double[] rightTermX=this.getRightTerm()[0];
		double[] rightTermY=this.getRightTerm()[1];
		
		double[][] solution=null;
		
		solution=this.solveLinearSystem(rightTermX, rightTermY); // an array containing the two vectors of size 'n' (for the x and y coordinates)
				
		for(Vertex u: this.mesh.vertices) {
			if(this.isInside[u.index]==true) { // set coordinated only for inner vertices
				int index=this.vertexOrder[u.index]; // index of vertex 'u' in the sub-graph: an integer in [0..n-k)
				
				this.points[u.index]=new Point_2(solution[0][index], solution[1][index]);
			}
		}
		
	}

	/**
	 * Solve the linear system (A+D)x=b using Jama library <p>
	 * <p>
	 * Remark: this method is not suitable for a large linear system, since Jama matrices are dense and the solver is not fast
	 * 
	 * @param A  the adjacency matrix (for the sub-graph consisting of inner vertices)
	 * @param D  the diagonal matrix (for the original graph)
	 * @return the solution of the linear system (A+D)x=b
	 */	
	public double[][] solveLinearSystem(double[] x, double y[]) {
		System.out.print("Creating Laplacian (dense) Tutte laplacian matrix of size"+this.nInnerVertices+" (using Jama library)...");
    	Matrix TutteMatrix=this.computeJama();
    	System.out.println("done");

    	System.out.print("Solving linear system with Jama...");
    	long startTime=System.nanoTime(), endTime; // for evaluating time performances
   	
    	double[] solutionX=this.solve(TutteMatrix, x); // solve the linear system for x-ccordinates
    	double[] solutionY=this.solve(TutteMatrix, y); // solve the linear system for y-coordinates
		
    	// evaluate time performances
    	endTime=System.nanoTime();
    	double duration=(double)(endTime-startTime)/1000000000.;
    	System.out.println("done ("+duration+" seconds)");
    	
		return new double[][] {solutionX, solutionY};
	}
	
	/**
	 * Solve linear system Ax=b
	 * 
	 * @param b
	 *            right hand side vector
	 * 
	 * @return the vector solution x[]
	 */
	public double[] solve(Matrix A, double[] b) {
		double[][] B=new double[1][]; // row vector
		B[0]=b; // the transpose gives the column vector
		Matrix X=A.solve(new Jama.Matrix(B).transpose()); // compute column vector Ax
		return X.transpose().getArray()[0]; // return the transpose of Ax
	}

	
	/**
	 * Computes and returns the Tutte laplacian matrix of the graph (Jama matrix)
	 */	
	public Matrix computeJama() {
		double[][] A=this.getDenseAdjacencyMatrix();
		double[][] D=this.getDenseDiagonalMatrix();
		Matrix adjacency=new Matrix(A);
		Matrix diagonal=new Matrix(D);
		
		Matrix Q=diagonal.minus(adjacency);
		
		return Q.transpose();
	}

	/**
	 * Compute and return the diagonal matrix corresponding to the (n-k) inner vertices
	 * 
	 * @return the (n-k)x(n-k) diagonal matrix storing the degrees of the inner vertices in the original (entire) graph G
	 */	
	public double[][] getDenseDiagonalMatrix() {
		//throw new Error("To be completed");
		double[][] m=new double[this.nInnerVertices][this.nInnerVertices];
		
		for(Vertex u: this.mesh.vertices) {
			if(this.isInside[u.index]==true) { // consider only inner vertices
				int subIndex=this.vertexOrder[u.index]; // index of vertex 'u' in the sub-graph: an integer in [0..n-k)
				m[subIndex][subIndex]=mesh.vertexDegree(u);
			}
		}
		
	   	return m;
	}

	/**
	 * Compute and return the adjacency matrix corresponding to the (n-k) inner vertices
	 * 
	 * @return the (n-k)x(n-k) adjacency matrix corresponding to the inner vertices
	 */	
	public double[][] getDenseAdjacencyMatrix() {
		//throw new Error("To be completed");
		double[][] m=new double[this.nInnerVertices][this.nInnerVertices];
		
		for(Vertex u: this.mesh.vertices) {
			if(this.isInside[u.index]==true) { // consider only inner vertices
				int indexU=this.vertexOrder[u.index]; // index of vertex 'u' in the sub-graph: an integer in [0..n-k)
			
				List<Halfedge> neighbors=u.getOutgoingHalfedges();
				for(Halfedge e: neighbors) { // visit all neighbors of vertex 'u'
					Vertex v=e.getVertex();
					if(this.isInside[v.index]==true) { // check whether 'v' is an inner vertex
						int indexV=this.vertexOrder[v.index]; // index of vertex 'v' in the sub-graph: an integer in [0..n-k)
						m[indexU][indexV]=1.; // store a '1', when there is an edge between 'u' and 'v'
					}
				}
			}
		}
		
	   	return m;
	}

	/**
	 * Compute and return the vector right term given by the locations of the boundary vertices (on the outer cycle)
	 * 
	 * @return the (n-k)x(n-k) adjacency matrix corresponding to the inner vertices
	 */	
	public double[][] getRightTerm() {
		//throw new Error("To be completed");
		
		double[][] result=new double[2][this.nInnerVertices];
		for(Vertex u: this.mesh.vertices) {
			if(this.isInside[u.index]==true) { // consider only inner vertices
				int indexU=this.vertexOrder[u.index]; // index of vertex 'u' in the sub-graph: an integer in [0..n-k)
			
				double x=0., y=0.;
				List<Halfedge> neighbors=u.getOutgoingHalfedges();
				for(Halfedge e: neighbors) { // visit all neighbors of vertex 'u'
					Vertex v=e.getVertex();
					if(this.isOnBoundary[v.index]==true) { // check whether 'v' is an inner vertex
						int indexV=this.vertexOrder[v.index]; // index of vertex 'v' in the sub-graph: an integer in [0..n-k)
						x=x+this.points[v.index].getX().doubleValue();
						y=y+this.points[v.index].getY().doubleValue();
					}
				}
				
				result[0][indexU]=x;
				result[1][indexU]=y;
			}
		}
		System.out.println();
		
		return result;
	}

}
