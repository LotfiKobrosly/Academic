import java.util.List;

import Jcg.geometry.Point_2;
import Jcg.geometry.Point_3;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;

import cern.colt.matrix.Norm;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.solver.DefaultDoubleIterationMonitor;
import cern.colt.matrix.tdouble.algo.solver.DoubleCG;
import cern.colt.matrix.tdouble.algo.solver.IterativeSolverDoubleNotConvergedException;
import cern.colt.matrix.tdouble.algo.solver.preconditioner.DoubleDiagonal;
import cern.colt.matrix.tdouble.algo.solver.preconditioner.DoublePreconditioner;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;

/**
 * Implementation of the planar (2D) Tutte barycentric method, based on the resolution of 2 linear systems
 * 
 * @author Luca Castelli Aleardi, Ecole Polytechnique
 * @version 2017
 */
public class MeshParameterization_PColt extends TutteLayout2D {

	/** vertex numbering in the sub-graph consisting of inner vertices (array of size 'n') */
	public int[] vertexOrder;
	
	/**
	 * Initialize the parameterization
	 */	
	public MeshParameterization_PColt(Polyhedron_3<Point_3> mesh, int face) {
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
		
		solution=this.solveLinearSystem(rightTermX, rightTermY, tolerance); // an array containing the two vectors of size 'n' (for the x and y coordinates)
				
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
	public double[][] solveLinearSystem(double[] x, double y[], double tolerance) {
		System.out.print("Creating Laplacian (dense) Tutte laplacian matrix of size"+this.nInnerVertices+" (using Jama library)...");
		DoubleMatrix2D TutteMatrix=this.computePColt();
    	System.out.println("done");

    	System.out.print("Solving linear system with Jama...");
    	long startTime=System.nanoTime(), endTime; // for evaluating time performances
   	
    	double[] solutionX=this.solve(TutteMatrix, x, tolerance); // solve the linear system for x-ccordinates
    	double[] solutionY=this.solve(TutteMatrix, y, tolerance); // solve the linear system for y-coordinates
		
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
	private double[] solve(DoubleMatrix2D A, double[] b, double precision) {
		int size=A.columns(); // matrix size
		
		// set the linear solver
		System.out.print("Setting solver and preconditioner (diagonal)...");
		double[] start=new double[size]; // initial guess for the CG iterator
		DoubleCG itSolver; // iterative solver implemented in Parallel Colt (Conjugate Gradient)
		DefaultDoubleIterationMonitor m;
		
		DoublePreconditioner preconditioner; // preconditioner
		preconditioner=new DoubleDiagonal(size); // use the diagonal matrix as preconditioner
		preconditioner.setMatrix(A);
		
		itSolver=new DoubleCG(new DenseDoubleMatrix1D(size));
		
		itSolver.setPreconditioner(preconditioner);
		
		m=(DefaultDoubleIterationMonitor)itSolver.getIterationMonitor();
		m.setMaxIterations(size);
		m.setNormType(Norm.Two); // choose euclidean norm
		m.setRelativeTolerance(precision);
		System.out.println("done");

		// running the iterative linear solver
		double[] x=new double[size]; // solution (and initial guess)
		DoubleMatrix1D X = new DenseDoubleMatrix1D(size); // solution
		DoubleMatrix1D B = new DenseDoubleMatrix1D(size);
		
		for (int i=0; i<size; i++) {
			B.set(i, b[i]);
			X.set(i, start[i]); // set initial guess
		}
		try {
			itSolver.solve(A, B, X); // compute solution of the linear system
		} catch (IterativeSolverDoubleNotConvergedException e) {
			e.printStackTrace();
		}
		for (int i=0; i<size; i++) {
			x[i] = X.get(i);
		}
		
		int verbosity=1;
		if(verbosity>0) {
			int iter=m.iterations();
			double residual=m.residual();
			System.err.println("  Conjugate Gradient solved in "+iter+" turns, dist = "+residual);
		}

		return x;
	}

	/**
	 * Create the Tutte matrix of a mesh (PColt sparse matrix) <p>
	 */	
	public DoubleMatrix2D computePColt() {
		DoubleMatrix2D L; // (sparse) matrix implementation based on Parallel Colt library
		int nIn=this.nInnerVertices; // matrix size
		System.out.print("Creating Laplacian matrix from a graph of size "+n+" (using Parallel Colt library)...");
		
    	long startTime=System.nanoTime(), endTime; // for evaluating time performances

		L=new SparseDoubleMatrix2D(nIn, nIn); // create a sparse matrix of size nxn
		
		//throw new Error("To be completed");

		for(Vertex u: this.mesh.vertices) {
			if(this.isInside[u.index]==true) { // consider only inner vertices
				int indexU=this.vertexOrder[u.index]; // index of vertex 'u' in the sub-graph: an integer in [0..n-k)
				int degree=this.mesh.vertexDegree(u);
				L.setQuick(indexU, indexU, degree); // set diagonal entries
				
				List<Halfedge> neighbors=u.getOutgoingHalfedges();
				for(Halfedge e: neighbors) { // visit all neighbors of vertex 'u'
					Vertex v=e.getVertex();
					if(this.isInside[v.index]==true) { // check whether 'v' is an inner vertex
						int indexV=this.vertexOrder[v.index]; // index of vertex 'v' in the sub-graph: an integer in [0..n-k)
						L.setQuick(indexU, indexV, -1.); // store a '-1', when there is an edge between 'u' and 'v'
					}
				}
			}
		}
		
    	endTime=System.nanoTime();
    	double duration=(double)(endTime-startTime)/1000000000.;
    	System.out.println("done ("+duration+" seconds)");
    	
		return L;
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
