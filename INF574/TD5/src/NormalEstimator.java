import java.util.List;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;

/**
 * A class providing methods for the estimation of vertex normal in a 3D point cloud
 * 
 * @author Luca Castelli Aleardi (INF574, 2018)
 *
 */
public class NormalEstimator {

	/**
	 * Compute the outliers in the point cloud
	 * 
	 * @param points  input point cloud
	 * @param k  number of closest neighbor
	 */
	public static double[][] computeNormals(PointSet points, int k) {
		int n = points.size();
		Point_3[] pts = points.toArray();
		double[][] normals = new double[n][3];
		for (int i=0; i<n; i++){
			Point_3[] neighbors = points.getKNearestNeighbors(pts[i], k);
			Point_3 bary = new Point_3();
			bary.barycenter(neighbors);
			Matrix P = new Matrix(new double[][] {{bary.x},{bary.y},{bary.z}});
			Matrix C = new Matrix(new double[][]{{0.,0.,0.},
				 								 {0.,0.,0.},
				 								 {0.,0.,0.}});
			for (int j=0; j<k; j++){
				Matrix pt = new Matrix( new double[][] {{neighbors[j].x},{neighbors[j].y},{neighbors[j].z}});
				pt = pt.minus(P); 
				C = C.plus(pt.times(pt.transpose()));
			}
			double[][] v = C.eig().getV().getArray();
			normals[i][0] = v[0][2];
			normals[i][1] = v[1][2];
			normals[i][2] = v[2][2];
		}
		return normals;
		
	}
	
	/**
	 * Compute the normals for all points in the point cloud
	 * 
	 * @param points  input point cloud
	 * @param sqRad  distance parameter (sqRad=d*d)
	 */
	public static double[][] computeNormals(PointSet points, double sqRad) {
		throw new Error("To be completed");
	}
	
	/**
	 * Given a point p and a distance d, <p>
	 * compute the matrix $C=\sum_{i}^{k} [(p_i-P)(p_i-P)^t]$<p>
	 * <p>
	 * where $k$ is the number of points ${p_i}$ at distance at most $d$ from point $p$
	 * 
	 * @param points  input point cloud
	 * @param p  the query point (for which we want to compute the normal)
	 * @param sqRad  squared distance (sqRad=d*d)
	 */
	public static Matrix getCovarianceMatrix(PointSet points, Point_3 p, double sqRad) {
		throw new Error("To be completed");
	}

	/**
	 * Return the distance parameter (a rough approximation of the average distance between neighboring points)
	 * 
	 * @param points  input point cloud
	 */
	public static double estimateAverageDistance(PointSet points) {
		int n=(int)Math.sqrt(points.size());
		double maxDistance=points.getMaxDistanceFromOrigin();
		return maxDistance*4/n;
	}

}
