import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import Jcg.geometry.Point_3;

/**
 * A class providing methods for the computatino of outliers in a 3D point cloud
 * 
 * @author Luca Castelli Aleardi (INF574, 2018)
 *
 */
public class OutliersComputation {

	/**
	 * Compute the outliers in the point cloud
	 * 
	 * @param points  input point cloud
	 * @param sqRad  distance parameter (sqRad=d*d)
	 * @param epsilon  threshold parameter
	 */
	public static boolean[] computeOutliers(PointSet points, double sqRad, double epsilon) {
		throw new Error("To be completed");
	}

	/**
	 * Compute the outliers in the point cloud
	 * 
	 * @param points  input point cloud
	 * @param k  number of closest neighbor
	 */
	public static boolean[] computeOutliers(PointSet points, int k) {
		throw new Error("To be completed");
	}

}
