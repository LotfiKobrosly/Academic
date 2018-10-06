/**
 * Simple scheme implementing linear interpolation
 * 
 * @author Luca Castelli Aleardi (INF555, 2012)
 *
 */
public class LinearInterpolation extends Interpolation {
	
	public LinearInterpolation(Draw frame) {
		super(frame);
	}
	
	public void interpolate() {	
		int n = this.points.length;
		if (n>1){
			for (int i=0;i<this.points.length-1;i++){
				this.drawSegment(this.points[i], this.points[i+1]);
				}
		}
	}
	
}
