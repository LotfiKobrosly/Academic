import java.util.Arrays;

import Jama.*;

/**
 * Cubic spline interpolation
 * 
 * @author Luca Castelli Aleardi (INF555, 2014)
 *
 */
public class CubicSplineInterpolation extends Interpolation {
	
	double[] coeffs=null;	

	public CubicSplineInterpolation(Draw frame) {
		super(frame);
	}
	
	public void computeCoefficients(Point_2[] P) {
		Matrix W; //Matrix of coeffs
		Matrix Y; //vector of s0,s1 and y
		
		int n = P.length;
		
		//Filling Y
		double[][] y = new double[4*(n-1)][1];
		
		y[0][0] = 1; //s0 = f'(x0)
		y[n][0] = 1; // s1 = f'(xN)
		for (int i=0; i<n-1;i++){
			y[4*i+1][0] = P[i].getY();
			y[4*i+2][0] = P[i+1].getY();
		}
		Y = new Matrix(y);
		
		//Filling W
		double[][] w = new double[4*(n-1)][4*(n-1)];
		//Arrays.fill(w, 0.);
				
		//First line exception
		w[0][0] = 0;
		w[0][1] = 1;
		w[0][2] = 2*P[0].getX();
		w[0][3] = 3*Math.pow(P[0].getX(), 2);
		
		
		for (int i=0; i<n-1; i++){
			for (int j=0; j<4;j++){
				w[4*i+1][4*i+j] = Math.pow(P[i].getX(), j);
			}
			for (int j=0; j<4;j++){
				w[4*i+2][4*i+j] = Math.pow(P[i+1].getX(), j);
			}
			w[4*i+3][4*i+1] = 1;
			w[4*i+3][4*i+2] = P[i+1].getX()*2;
			w[4*i+3][4*i+3] = 3*Math.pow(P[i+1].getX(), 2);
			
			if (i<n-2){
				w[4*i+3][4*i+5] = -1;
				w[4*i+3][4*i+6] = P[i+1].getX()*(-2);
				w[4*i+3][4*i+7] = (-3)*Math.pow(P[i+1].getX(), 2);
				
				w[4*i+4][4*i+2] = 2;
				w[4*i+4][4*i+3] = 6*P[i].getX();
				w[4*i+4][4*i+6] = -2;
				w[4*i+4][4*i+7] = (-6)*P[i].getX();
			}
			
		}
		
		W = new Matrix(w);
		
		this.coeffs = W.solve(Y).getRowPackedCopy();
	}
	
	/**
	 * Evaluate polynomial a[0]+a[1]x+a[2]x^2+...+a[n]x^n, at point x
	 */
	public static double evaluate(double[] a, double x) {
		if(a==null || a.length==0) throw new Error("polynomial not defined");
		double result=a[0];
		double p=1.;
		for(int i=1;i<a.length;i++) {
			p=p*x;
			//System.out.println(""+a[i]+"*"+p+"="+(a[i]*p));
			result=result+(a[i]*p);
		}
		return result;
	}

	public void interpolate() {
		int n = this.points.length;
		Arrays.sort(this.points);
		if(n>2)  {
			this.computeCoefficients(this.points);
			for (int i=0; i<n-1;i++){
				double[] coeff = new double[4];
				double[] points = new double[]{this.points[i].getX(),this.points[i+1].getX()};
				for (int j = 0; j<4; j++){ 
					coeff[j] = this.coeffs[4*i+j];
				}
				int a=0,b=0,c=0;
				if (i%3 == 0 ) a = 255;
				if (i%3 == 1 ) b = 255;
				if (i%3 == 2 ) c = 255;
				frame.stroke(a, b, c);
				plotPolynomial(coeff, points[0], points[1], 300);
				
				//Drawing the tangents
				double[] coeff_deriv = new double[]{coeff[1],coeff[2]*2.0,coeff[3]*3.0};
				double deriv = evaluate(coeff_deriv, points[1]);
				double im = evaluate(coeff, points[1]);
				Point_2 target = new Point_2(points[1]+20, im + deriv*20 );
				this.drawSegment(this.points[i+1], target);
			}
			
			//System.out.println("Interpolating polynomial: "+polynomialToString(this.coeff));
		}
		for(Point_2 q:this.points) {
	    	drawPoint(q); // draw input points
	    }
		
		
	}
	
	public void plotPolynomial(double[] a, double min, double max, int n) {
		double dx=(max-min)/n;
		
		double x=min;
		for(int i=0;i<n;i++) {
			Point_2 p=new Point_2(x, evaluate(a, x));
			Point_2 q=new Point_2(x+dx, evaluate(a, x+dx));
			this.drawSegment(p, q);
			x=x+dx;
		}
		
	}

}
