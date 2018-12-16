import java.util.ArrayList;

import Jcg.geometry.*;
import Jcg.geometry.Point_3;
import Jcg.polyhedron.*;

/**
 * @author Luca Castelli Aleardi (INF555, 2012)
 *
 */
public class HalfedgeContraction extends MeshSimplification {
	
	public HalfedgeContraction(Polyhedron_3<Point_3> polyhedron3D) {
		super(polyhedron3D);
	}
	
	/**
	 * Basic example of simplification based on edge contractions
	 * Simply select at random edges to be contracted
	 */
	
	public ArrayList<Point_3> getNeighbors(Vertex<Point_3> v){
		ArrayList<Point_3> neighbors = new ArrayList<Point_3>();
		Halfedge<Point_3> halfedge = v.getHalfedge(),
						  iterator = halfedge.getNext();
		neighbors.add(halfedge.getOpposite().getVertex().getPoint());
		while (iterator != halfedge.getOpposite() ){
			neighbors.add(iterator.getVertex().getPoint());
			iterator = iterator.getOpposite().getNext();
		}
		return neighbors;
	}
	
	public void simplify() {
		
		// Select a random halfedge
		int x = (int) (Math.random() * this.polyhedron3D.halfedges.size());
		Halfedge<Point_3> first = this.polyhedron3D.halfedges.get(x);
		Halfedge<Point_3> second = first.getOpposite(); // Get the opposite
		Vertex<Point_3> u = first.getVertex();
		Vertex<Point_3> v = second.getVertex();
		if (this.isLegal(first)){
		
			// Updating the polyhedron
			Halfedge<Point_3> tmp1 = second.getNext(),
							  tmp2 = tmp1.getOpposite();
			
			// Fusing the common neighbors (and eliminating the multiple edges) Part 1/2
			second.getPrev().prev = tmp2.getPrev();
			tmp2.getPrev().next = second.getPrev();
			
			tmp2.getNext().prev = second.getPrev();
			second.getPrev().next = tmp2.getNext();
			
			// Removing the no longer existing edges Part 1/3
			this.polyhedron3D.halfedges.remove(tmp1);
			this.polyhedron3D.halfedges.remove(tmp2);
	
			//Updating other edges linked to vertex v
			tmp1 = tmp2.getNext();
			tmp2 = tmp1.getOpposite();
			
			while(tmp2.getNext() != first){
				tmp2.vertex = first.vertex;
				tmp2 = tmp2.getNext().getOpposite();
			}
			tmp1 = tmp2.getOpposite();
			// Fusing the common neighbors (and eliminating the multiple edges) Part 2/2
			tmp1.getPrev().next = first.getNext();
			first.getNext().prev = tmp1.getPrev();
			
			tmp1.getNext().prev = first.getNext();
			first.getNext().next = tmp1.getNext();
			
			// Removing the no longer existing edges Part 2/3
			this.polyhedron3D.halfedges.remove(tmp1);
			this.polyhedron3D.halfedges.remove(tmp2);
					
			
			// Removing the contracted halfedges Part 3/3
			this.polyhedron3D.halfedges.remove(first);
			this.polyhedron3D.halfedges.remove(second );
			}
		else return;
	}
	
	/**
	 * Check whether a given halfedge can be contracted
	 */
	boolean isLegal(Halfedge<Point_3> h){
		ArrayList<Point_3> u_neighb = this.getNeighbors(h.getVertex());
		ArrayList<Point_3> v_neighb = this.getNeighbors(h.getOpposite().getVertex());
		int n = 0;
		for (Point_3 p : u_neighb){
			if (v_neighb.contains(p)) n++;
		}
		if (n == 2) return true;
		return false;
	}
	
}
