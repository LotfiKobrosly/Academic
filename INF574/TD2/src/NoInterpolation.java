/**
 * Basic example illustrating interpolation schemes
 * 
 * @author Luca Castelli Aleardi (INF555, 2012)
 *
 */
public class NoInterpolation extends Interpolation {

	public NoInterpolation(Draw frame) {
		super(frame);
	}
	
	/**
	 * Draw only input points
	 */
	public void interpolate() {
		for(int i=0;i<this.points.length;i++)
	    	drawPoint(this.points[i]);
	}

}
