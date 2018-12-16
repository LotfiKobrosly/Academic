import Jcg.geometry.*;
import Jcg.polyhedron.*;

import java.util.*;

/**
 * Perform the Loop subdivision scheme
 * Only for triangular meshes (without boundary)
 * 
 * @author Luca Castelli Aleardi (INF555, 2012)
 *
 */
public class LoopSubdivision extends MeshSubdivision {
	
	HashSet<Vertex<Point_3>> originalVertices; // store the references of the original vertices
	
	public LoopSubdivision(Polyhedron_3<Point_3> polyhedron) {
		super(polyhedron);
	}
	
	/**
	 * The main method performing the subdivision process
	 * To be implemented
	 */
	public void subdivide() {
		this.originalVertices = new HashSet<Vertex<Point_3>>();
		for( Vertex<Point_3> v : this.polyhedron3D.vertices){
			this.originalVertices.add(v);
		}
		this.splitEdges(this.computeEdgePoints());
		ArrayList<Face<Point_3>> faces = this.polyhedron3D.facets; 
		int n = faces.size();
		for(int i=0; i < n; i++ ){
			this.subdivideFace(faces.get(i));
		}
		this.computeNewVertexLocations();
	}

	/**
	 * Splits all edges by inserting a new vertex
	 */
	public void splitEdges(HashMap<Halfedge<Point_3>, Point_3> edgePoints) {
		System.out.print("Splitting edges...");
		for (Halfedge<Point_3> h : edgePoints.keySet()) {
			this.polyhedron3D.splitEdge(h, edgePoints.get(h));
		}
		System.out.println("done");
	}
	
	/**
	 * Perform the subdivision of a face into 4 triangular sub-faces
	 * Edges must already be split: the face has degree 3+3
	 */
	public void subdivideFace(Face<Point_3> f) {
		Halfedge<Point_3> h1 = f.getEdge();
		if (this.originalVertices.contains(h1.getVertex())) h1 = h1.getNext();
		Halfedge<Point_3> h2 = h1.getNext().getNext();
		Halfedge<Point_3> h3 = h2.getNext().getNext();
		this.polyhedron3D.splitFacet(h1, h2);
		this.polyhedron3D.splitFacet(h1.next,h3);
		this.polyhedron3D.splitFacet(h1.next.next, h1);
	}

	/**
	 * Compute a new edge point (given the half-edge h)
	 */
	public Point_3 computeEdgePoint(Halfedge<Point_3> h) {
		Halfedge<Point_3> h_op = h.getOpposite();
		Point_3 v1 = h.getVertex().getPoint(),
				v2 = h_op.getVertex().getPoint(),
				v3 = h.getNext().getVertex().getPoint(),
				v4 = h_op.getNext().getVertex().getPoint();
		double x = 3*(v1.x + v2.x)/8 + (v3.x + v4.x)/8,
			   y = 3*(v1.y + v2.y)/8 + (v3.y + v4.y)/8,
			   z = 3*(v1.z + v2.z)/8 + (v3.z + v4.z)/8;
		Point_3 p = new Point_3();
		p.x = x; p.y = y; p.z = z ;
		return p;
	}

	/**
	 * Compute all new edge points and store the result in an HashMap
	 */
	public HashMap<Halfedge<Point_3>, Point_3> computeEdgePoints() {
		HashMap<Halfedge<Point_3>, Point_3> map = new HashMap<Halfedge<Point_3>, Point_3>();
		ArrayList<Halfedge<Point_3>> list =  this.polyhedron3D.halfedges;
		int n = list.size();
		for (int i=0; i<n; i++){
			Halfedge<Point_3> h = list.get(i);
			Point_3 p = this.computeEdgePoint(h);
			if (!map.containsKey(h.getOpposite())) map.putIfAbsent(h, p);
		}
		return map;
	}
	
	/**
	 * Compute the new coordinates for a vertex (already existing in the initial mesh)
	 */
	public Point_3 computeNewVertexLocation(Vertex<Point_3> v) {
		int n=1;
		ArrayList<Vertex<Point_3>> neighbors = new ArrayList<Vertex<Point_3>>();
		Halfedge<Point_3> h = v.getHalfedge().getNext().getOpposite();
		Halfedge<Point_3> v_h = v.getHalfedge();
		neighbors.add(h.getOpposite().getVertex());
		while (!h.equals(v_h)){
			n++;
			neighbors.add(h.getOpposite().getVertex());
			h = h.getNext().getOpposite();
		}
		double x=0., y=0., z=0.;
		if (n > 3){
			x= 5./8. * v.getPoint().x;
			y= 5./8. * v.getPoint().y; 
			z= 5./8. * v.getPoint().z;
			double x0=0., y0=0., z0=0.;
			for (Vertex<Point_3> vert : neighbors){
				x0 = x0 + vert.getPoint().x;
				y0 = y0 + vert.getPoint().y;
				z0 = z0 + vert.getPoint().z;
			}
			x = x + 3.*x0/(8.*n);
			y = y + 3.*y0/(8.*n); 
			z = z + 3.*z0/(8.*n);
		}
		else{
			x= 7./16. * v.getPoint().x; 
			y= 7./16. * v.getPoint().y; 
			z= 7./16. * v.getPoint().z;
			double x0=0., y0=0., z0=0.;
			for (Vertex<Point_3> vert : neighbors){
				x0 = x0 + vert.getPoint().x;
				y0 = y0 + vert.getPoint().y;
				z0 = z0 + vert.getPoint().z;
			}
			x = x + 3.*x0/16.;
			y = y + 3.*y0/16.;
			z = z + 3.*z0/16.;
		}
		Point_3 p = new Point_3();
		p.x = x; p.y = y; p.z = z;
		return p;
	}

	/**
	 * Compute the new coordinates for all vertices of the initial mesh
	 */
	public Point_3[] computeNewVertexLocations() {
		int n = this.originalVertices.size(), i = 0;
		Point_3[] new_pts = new Point_3[n];
		for (Vertex<Point_3> v : this.originalVertices){
			new_pts[i] = this.computeNewVertexLocation(v);
			v.setPoint(new_pts[i]);
			i++;
		}
		return new_pts;
	}

}
