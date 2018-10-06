#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include <iostream>
#include <queue>

using namespace cv;
using namespace std;

// Step 1: complete gradient and threshold
// Step 2: complete sobel
// Step 3: complete canny (recommended substep: return Max instead of C to check it) 

// Raw gradient. No denoising
void gradient(const Mat&Ic, Mat& G2)
{
	Mat I;
	cvtColor(Ic, I, CV_BGR2GRAY);

	int m = I.rows, n = I.cols;
	G2 = Mat(m, n, CV_32F);

	for (int i = 0; i < m; i++) {
		for (int j = 0; j < n; j++) {
			// Compute squared gradient (except on borders)
			float I_x = 0.0;
			float I_y = 0.0;
			if ((i != 0) && ( i != m-1)){
				I_x = ((float)I.at<uchar>(i+1,j) - (float)I.at<uchar>(i-1,j))/2.0;
			}
			else{ 
				I_x = ((float)I.at<uchar>(i,j));
			}
			if ((j != 0) && ( j != n-1)){
				I_y = ((float)I.at<uchar>(i,j+1) - (float)I.at<uchar>(i,j-1))/2.0;
			}
			else{ 
				I_y = ((float)I.at<uchar>(i,j));
			}
			
			G2.at<float>(i, j) = I_x*I_x + I_y*I_y;
		}
	}
}

// Gradient (and derivatives), Sobel denoising
void sobel(const Mat&Ic, Mat& Ix, Mat& Iy, Mat& G2)
{
	Mat I;
	cvtColor(Ic, I, CV_BGR2GRAY);

	int m = I.rows, n = I.cols;
	Ix = Mat(m, n, CV_32F);
	Iy = Mat(m, n, CV_32F);
	G2 = Mat(m, n, CV_32F);

	for (int i = 0; i < m; i++) {
		for (int j = 0; j < n; j++) {
			if ((i > 0) && (i < m - 1) && (j > 0) && (j < n - 1)) {
			Ix.at<float>(i, j) = (float)I.at<uchar>(i - 1, j + 1) - (float)I.at<uchar>(i - 1, j - 1)
					+ (float)I.at<uchar>(i + 1, j + 1) - (float)I.at<uchar>(i + 1, j - 1)
					+ 2.0*((float)I.at<uchar>(i, j + 1) - (float)I.at<uchar>(i, j - 1));
			Iy.at<float>(i, j) = (float)I.at<uchar>(i + 1, j - 1) - (float)I.at<uchar>(i - 1, j - 1)
					+ (float)I.at<uchar>(i + 1, j + 1) - (float)I.at<uchar>(i - 1, j + 1)
					+ 2.0*((float)I.at<uchar>(i + 1, j) - (float)I.at<uchar>(i - 1, j));
			G2.at<float>(i, j) = Ix.at<float>(i, j)*Ix.at<float>(i, j)/16.0 + Iy.at<float>(i, j)*Iy.at<float>(i, j)/16.0;
			}
		}
	}
}

// Gradient thresholding, default = do not denoise
Mat threshold(const Mat& Ic, float s, bool denoise = false)
{
	Mat Ix, Iy, G2;
	if (denoise)
		sobel(Ic, Ix, Iy, G2);
	else
		gradient(Ic, G2);
	s = s * s;
	int m = Ic.rows, n = Ic.cols;
	Mat C(m, n, CV_8U);
	for (int i = 0; i < m; i++)
		for (int j = 0; j < n; j++) {
			if (G2.at<float>(i, j) > s) { C.at<uchar>(i, j) = 255; }
			else C.at<uchar>(i, j) = 0;
		}
	return C;
}

// Canny edge detector
Mat canny(const Mat& Ic, float s1)
{
	Mat Ix, Iy, G2;
	sobel(Ic, Ix, Iy, G2);
	int m = Ic.rows, n = Ic.cols;
	Mat Max(m, n, CV_8U);	// Max pixels ( G2 > s1 && max in the direction of the gradient )
	queue<Point> Q;			// Enqueue seeds ( Max pixels for which G2 > s2 )
	Max = threshold(Ic, s1, true);
	s1 = s1 * s1;
	float s2 = s1 * 3;
	for (int i = 0; i < m; i++) {
		for (int j = 0; j < n; j++) {
			
			if (Max.at<uchar>(i,j) == 1) {
				float dx = Ix.at<float>(i, j);
				float dy = Iy.at<float>(i, j);
				float pi = 3.1415;
				if (dx == 0) {
					if ((G2.at<float>(i, j) < G2.at<float>(i, j + 1)) || (G2.at<float>(i, j) < G2.at<float>(i, j - 1))) {
						Max.at<uchar>(i, j) = 0;
					}
				}
				else {
					float slope = dy / dx;
					if ((slope <= tan((-3)*pi / 8)) || (slope >= tan((3)*pi / 8))) {
						if ((G2.at<float>(i, j) < G2.at<float>(i, j + 1)) || (G2.at<float>(i, j) < G2.at<float>(i, j - 1))) { Max.at<uchar>(i, j) = 0; }
					}
					else if ((slope < tan(-pi / 8)) && (slope > tan((-3 * pi) / 8))) {
						if ((G2.at<float>(i, j) < G2.at<float>(i + 1, j + 1)) || (G2.at<float>(i, j) < G2.at<float>(i - 1, j - 1))) { Max.at<uchar>(i, j) = 0; }
					}
					else if ((slope < tan(3 * pi / 8)) && (slope > tan(pi / 8))) {
						if ((G2.at<float>(i, j) < G2.at<float>(i + 1, j - 1)) || (G2.at<float>(i, j) < G2.at<float>(i - 1, j + 1))) { Max.at<uchar>(i, j) = 0; }
					}
					else if ((slope <= tan(pi / 8)) && (slope >= tan(-pi / 8))) {
						if ((G2.at<float>(i, j) < G2.at<float>(i + 1, j)) || (G2.at<float>(i, j) < G2.at<float>(i - 1, j))) { Max.at<uchar>(i, j) = 0; }
					}
				}
			}
			if (G2.at<float>(i, j) > s2) {
				Q.push(Point(j, i)); // Beware: Mats use row,col, but points use x,y
				Max.at<uchar>(i, j) = 0;
			}
		}
	}

	// Propagate seeds
	Mat C(m, n, CV_8U);
	C.setTo(0);
	while (!Q.empty()) {
		int i = Q.front().y, j = Q.front().x;
		Q.pop();
		C.at<uchar>(i, j) = 255;
		for (int i1 =-1; i1<2; i1++)
			for (int j1 = -1; j1 > 2; j1++) {
				if ((i1 != 0) && (j1 !=0))
					if (Max.at<uchar>(i + i1, j + j1) == 1) Q.push(Point(i + i1, j + j1));
			}
	}

	return C;
}

int main()
{
	Mat I = imread("../road.jpg");
	float s = 25;
	imshow("Input", I);
	imshow("Threshold", threshold(I, s));
	imshow("Threshold + denoising", threshold(I, s, true));
	imshow("Canny", canny(I, s));

	waitKey();

	return 0;
}
