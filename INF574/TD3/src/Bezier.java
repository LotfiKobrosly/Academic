import java.util.LinkedList;

public class Bezier extends Curve {

	Transformation_2 transformation;

	public Bezier(DrawCurve frame, LinkedList<Point_2> p) {
		super(frame, p);
		transformation=null;
	}

	public Bezier(DrawCurve frame, LinkedList<Point_2> p, Transformation_2 transformation) {
		super(frame, p);
		this.transformation=transformation;
	}
	
	public Bezier(DrawCurve frame, Point_2[] points, Transformation_2 transformation) {
		super(frame, points);
		this.transformation=transformation;
	}

	/**
	 * Draw the control polygon
	 */
	public void drawControlPolygon() {
		this.frame.stroke(0, 0, 255);
	    for(int i=1;i<this.points.length;i++) {
	    	drawSegment(this.points[i], this.points[i-1]);
	    	//drawSegment(transformation.transform(points[i]), transformation.transform(points[i-1]));
	    }
		this.frame.stroke(0, 0, 0);
	}

	/**
	 * Evaluate the curve for parameter t
	 * Return point (x(t), y(t))
	 */
	public Point_2 evaluate(double t) {
		//return recursiveDeCasteljau(this.points.length-1, 0, t);
		//return iterativeDeCasteljau(t);
		return BernsteinBezier(t);
	}

	/**
	 * Perform the subdivision (once) of the Bezier curve (with parameter t)
	 * Return two Bezier curves (with n control points each)
	 */
	public Bezier[] subdivide(double t) {
		Point_2[] controlPolygon=this.points;
		int n=this.points.length-1; // degree and number of edges of the control polygon

		Point_2[] b0=new Point_2[n+1]; // first control polygon
		Point_2[] b1=new Point_2[n+1]; // second control polygon
		Bezier[] result=new Bezier[2]; // the pair of Bezier curves to return as result

		throw new Error("To be completed:  INF574");
	}

	/**
	 * Plot the curve (in the frame), for t=0..1, with step dt
	 */
	public void plotCurve(double dt) {
		this.drawControlPolygon();
		this.drawControlPoints();
		int len = (int)(1./dt);
		Point_2[] bpoints = new Point_2[len];
		for (int i=0;i<len; i++ ){
			bpoints[i] = evaluate((double)i*dt);
			if (i>1){
				drawSegment(bpoints[i-1],bpoints[i]);
			}
		}
	}

	/**
	 * Perform the rendering of the curve using subdivision approach
	 * Perform the subdivision n times
	 */
	public void subdivisionRendering(int n) {
		this.drawControlPolygon(); // draw original control polygon
		this.drawControlPoints(); // draw original control points
		LinkedList<Bezier> subCurves=new LinkedList<Bezier>();
		
		// to be completed TD INF574:
		if(this.points.length<3) return;

	}
	
	private static long binomial(int n, int k)
    {
        if (k>n-k)
            k=n-k;

        long b=1;
        for (int i=1, m=n; i<=k; i++, m--)
            b=b*m/i;
        return b;
    }

	public Point_2 recursiveDeCasteljau(int r, int i, double t) {
		//int n = this.points.length;
		//if (i>n-r) {return new Point_2(0.,0.);}
		if (r == 0) { return this.points[i];}
		double x = (1-t)*recursiveDeCasteljau(r-1,i,t).getX() + t*recursiveDeCasteljau(r-1,i+1,t).getX();
		double y = (1-t)*recursiveDeCasteljau(r-1,i,t).getY() + t*recursiveDeCasteljau(r-1,i+1,t).getY();
		
		return(new Point_2(x,y));
	}

	/**
	 * Perform the (iterative) De Casteljau algorithm to evaluate b(t)
	 */
	public Point_2 iterativeDeCasteljau(double t) {
		int n = this.points.length-1;
		
		Point_2[] iterpts = this.points.clone();
		for (int i=0; i<=n; i++){
			for (int j=0; j<n-i; j++){
				double x = 0, y = 0;
				x = iterpts[j].getX()*(1-t) + iterpts[j+1].getX()*t;
				y = iterpts[j].getY()*(1-t) + iterpts[j+1].getY()*t;
				iterpts[j] = new Point_2(x,y);
			}
		}
		return iterpts[0];
	}

	public Point_2 BernsteinBezier(double t){
		int n = this.points.length-1;
		double x = 0, y = 0;
		for (int i=0;i<=n;i++){
			x += this.points[i].getX() * Math.pow(t, i) * Math.pow(1-t, n-i) * binomial(n,i);
			y += this.points[i].getY() * Math.pow(t, i) * Math.pow(1-t, n-i) * binomial(n,i);
		}
		return new Point_2(x,y);
	}
}
