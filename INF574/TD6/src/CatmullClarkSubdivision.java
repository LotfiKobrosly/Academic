import Jcg.geometry.*;
import Jcg.polyhedron.*;

import java.util.*;

public class CatmullClarkSubdivision extends MeshSubdivision{

	HashSet<Vertex<Point_3>> originalVertices; // store the references of the original vertices
	
	public CatmullClarkSubdivision(Polyhedron_3<Point_3> polyhedron) {
		super(polyhedron);
	}
	
	public void subdivide(){
		this.originalVertices = new HashSet<Vertex<Point_3>>();
		for (Vertex<Point_3> v: this.polyhedron3D.vertices) this.originalVertices.add(v);
		ArrayList<Face<Point_3>> faces = this.polyhedron3D.facets;
		int n = faces.size();
		for (int i=0; i<n; i++){
			this.polyhedron3D.createCenterVertex(faces.get(i));
		}
		this.computeNewVertexLocations();
		this.splitEdges(this.computeNewEdgePoints());
		n = faces.size();
		for (int i=0; i<n; i++){
			this.subdivideFace(faces.get(i));
		}
	}
	
	public void subdivideFace(Face<Point_3> f){
		Halfedge<Point_3> h = f.getEdge();
		while(this.originalVertices.contains(h.getVertex())) h = h.getNext();
		Halfedge<Point_3> h1 = h.getNext().getNext();
		this.polyhedron3D.splitFacet(h, h1);
	}
	
	public Point_3 computeNewEdgePoint(Halfedge<Point_3> h){
		Halfedge<Point_3> h_op = h.getOpposite();
		Point_3 v1 = h.getVertex().getPoint(),
				v2 = h_op.getVertex().getPoint(),
				v3 = h.getNext().getVertex().getPoint(),
				v4 = h_op.getNext().getVertex().getPoint();
		Point_3 p = new Point_3();
		p.barycenter(new Point_3[]{v1,v2,v3,v4});
		return p;
	}
	
	public HashMap<Halfedge<Point_3>, Point_3> computeNewEdgePoints() {
		HashMap<Halfedge<Point_3>, Point_3> map = new HashMap<Halfedge<Point_3>, Point_3>();
		ArrayList<Halfedge<Point_3>> list =  this.polyhedron3D.halfedges;
		int n = list.size();
		for (int i=0; i<n; i++){
			Halfedge<Point_3> h = list.get(i);
			Point_3 p = this.computeNewEdgePoint(h);
			if (!map.containsKey(h.getOpposite())) map.putIfAbsent(h, p);
		}
		return map;
	}
	
	public void splitEdges(HashMap<Halfedge<Point_3>, Point_3> edgePoints) {
		for (Halfedge<Point_3> h : edgePoints.keySet()) {
			this.polyhedron3D.splitEdge(h, edgePoints.get(h));
		}
	}
	
	public Point_3 computeNewVertexLocation(Vertex<Point_3> v){
		int n = 1;
		ArrayList<Vertex<Point_3>> neighbors = new ArrayList<Vertex<Point_3>>();
		Halfedge<Point_3> h = v.getHalfedge().getNext().getOpposite();
		Halfedge<Point_3> v_h = v.getHalfedge();
		neighbors.add(h.getOpposite().getVertex());
		while (!h.equals(v_h)){
			n++;
			neighbors.add(h.getOpposite().getVertex());
			h = h.getNext().getOpposite();
		}
		n = n*2/3;
		double x0 = 0., y0 = 0., z0 = 0.;
		for (Vertex<Point_3> vert : neighbors){
			x0 = x0 + vert.getPoint().x - v.getPoint().x;
			y0 = y0 + vert.getPoint().y - v.getPoint().y;
			z0 = z0 + vert.getPoint().z - v.getPoint().z;
		}
		double x = v.getPoint().x + x0/((double)(n*n)),
			   y = v.getPoint().y + y0/((double)(n*n)),
			   z = v.getPoint().z + z0/((double)(n*n));
		Point_3 p = new Point_3();
		p.x = x; p.y = y; p.z = z;
		return p;
	}
	
	public void computeNewVertexLocations(){
		for (Vertex<Point_3> v : this.originalVertices){
			v.setPoint(this.computeNewVertexLocation(v));
		}
	}
	
}
