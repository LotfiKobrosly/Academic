import java.util.LinkedList;
import java.util.Random;

import Jama.Matrix;

public class HermiteSplineInterpolation extends Interpolation{
	
	LinkedList<Vector_2> slopeList;

	public HermiteSplineInterpolation(Draw frame) {
		super(frame);
		this.slopeList = new LinkedList<Vector_2>();
		slopeList.add(new Vector_2(100,100));
		slopeList.add(new Vector_2(-100,0));
		slopeList.add(new Vector_2(0,100));
		slopeList.add(new Vector_2(-100,100));
		slopeList.add(new Vector_2(300,100));
	}
	
	public Vector_2[] linkedListArray() {
		Vector_2[] slopeListArray=new Vector_2[slopeList.size()];
		int i=0;
		for(Vector_2 v: slopeList) {
			slopeListArray[i]=v;
			i++;
		}		
		return slopeListArray;
	}

	@Override
	public void interpolate() {
		int pointsSize = this.points.length;
		int slopesSize = this.slopeList.size();
		
		for(int i=0;i<pointsSize;i++)
	    	drawPoint(this.points[i]);
		
		for(int i=0; i<(pointsSize-slopesSize); i++){
			Random randomGenerator = new Random();
			slopeList.add(new Vector_2(100*randomGenerator.nextInt(10)-500,100*randomGenerator.nextInt(10)-500));
		}
		
		
		plotHermite(points, this.linkedListArray());
		
	}
	
	
	public void plotHermite(Point_2[] p, Vector_2[] sL){
		
		double[][] m = new double[][] { {1.,0.,0.,0.},
										{1.,1.,1.,1.},
										{0.,1.,0.,0.},
										{0.,1.,2.,3.}};
		Matrix M = new Matrix(m);
		int n = p.length;
		for (int i=0; i<n-1; i++){
			Vector_2 vector1 = sL[i]; //slope at p0 (for t=0)
			Vector_2 vector2 = sL[i+1]; //slope at p1 (for t=1)
			
			double[][] y1 = new double[][] { {p[i].getX()},
											 {p[i+1].getX()},
											 {vector1.getX()},
											 {vector2.getX()} };
			double[][] y2 = new double[][] { {p[i].getY()},
											 {p[i+1].getY()},
											 {vector1.getY()},
											 {vector2.getY()} };
			
			Matrix Y1 = new Matrix(y1);
			Matrix Y2 = new Matrix(y2);
			
			double[] coeffX = M.solve(Y1).getRowPackedCopy();
			double[] coeffY = M.solve(Y2).getRowPackedCopy();
			
			//color choices
			int a=0,b=0,c=0;
			if (i%3 == 0 ) a = 255;
			if (i%3 == 1 ) b = 255;
			if (i%3 == 2 ) c = 255;
			frame.stroke(a, b, c);
			
			//Drawing the polynomial function in the interval [x_i,x_i+1]
			plotPolynomialCurve(coeffX,coeffY,100);
			
			//Drawing the tangents
			Point_2 q = new Point_2( p[i].getX() + vector1.getX(), p[i].getY() + vector1.getY() );
			drawSegment(p[i],q);
			if ( i == n-2){
				Point_2 q2 = new Point_2( p[i+1].getX() + vector2.getX(), p[i+1].getY() + vector2.getY());
				drawSegment(p[i+1],q2);
			}
			
		}
	}
	
	public Vector_2 getSlope(int pointIndex){
		return linkedListArray()[pointIndex];
	}
	
	void plotPolynomialCurve(double[] coeffX, double[] coeffY, int n) {
		Point_2[] plotPoints = new Point_2[n+1];
		double space = (double)1./n;
		
		for(int i=0;i<=n;i++){
			double t = (double)i*space;
			double xValue = 0;
			for(int l=0;l<coeffX.length;l++)
				xValue+=coeffX[l]*Math.pow(t,l);

			double yValue = 0;
			for(int l=0;l<coeffY.length;l++)
				yValue+=coeffY[l]*Math.pow(t,l);
			
			plotPoints[i]=new Point_2(xValue,yValue);
		}
			
		for(int i=0;i<n;i++)
			drawSegment(plotPoints[i], plotPoints[i+1]);
	}
	
	
	
}
