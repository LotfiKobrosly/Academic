
#include <iostream>
#include <opencv2/opencv.hpp>
using namespace std;
using namespace cv;

bool isMainLine() {


    return false;
}

int main(int, char** argv)
{
    // Load the image
    Mat src = imread("../sheet2.png");
    // Check if image is loaded fine
    if(!src.data)
        cerr << "Problem loading image!!!" << endl;
    // Show source image
    imshow("src", src);
    // Transform source image to gray if it is not
    Mat gray;
    if (src.channels() == 3)
    {
        cvtColor(src, gray, CV_BGR2GRAY);
    }
    else
    {
        gray = src;
    }
    // Show gray image
    //imshow("gray", gray);
    // Apply adaptiveThreshold at the bitwise_not of gray, notice the ~ symbol
    Mat bw;
    // 4. blur smooth img
    // 5. smooth.copyTo(src, edges)
    adaptiveThreshold(~gray, bw, 255, CV_ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15, -2);
    // Show binary image
    //imshow("binary", bw);
    // Create the images that will use to extract the horizontal and vertical lines
    Mat horizontal = bw.clone();
    Mat vertical = bw.clone();

    // Specify size on horizontal axis
    int horizontalsize = horizontal.cols / 3;
    // Create structure element for extracting horizontal lines through morphology operations
    Mat horizontalStructure = getStructuringElement(MORPH_RECT, Size(horizontalsize,1));
    // Apply morphology operations
    erode(horizontal, horizontal, horizontalStructure, Point(-1, -1));
    dilate(horizontal, horizontal, horizontalStructure, Point(-1, -1));
    // Show extracted horizontal lines
    imshow("horizontal", horizontal);
    vector<Vec2f> lines;
    //Canny(horizontal, horizontal, 50, 200, 3);
    HoughLines(horizontal, lines, 1, CV_PI/180, 100, 0, 0 );


    Mat showlines = bw.clone();
    showlines.setTo(0);


    vector<float> thetas ;
    for( size_t i = 0; i < lines.size(); i++ )
        thetas.push_back(lines[i][1]);
    size_t n = lines.size() / 2;
    nth_element(thetas.begin(), thetas.begin()+n, thetas.end());
    float medianTheta = thetas[n];

    for( size_t i = 0; i < lines.size(); i++ )
        if (abs(lines[i][1] - medianTheta) <0.01)
        {
          float rho = lines[i][0], theta = lines[i][1];
          Point pt1, pt2;
          double a = cos(theta), b = sin(theta);
          double x0 = a*rho, y0 = b*rho;
//          pt1.x = cvRound(x0 + 1000*(-b));
//          pt1.y = cvRound(y0 + 1000*(a));
//          pt2.x = cvRound(x0 - 1000*(-b));
//          pt2.y = cvRound(y0 - 1000*(a));
          pt1.x = 0;
          pt1.y = cvRound(y0 + x0 * a / b);
          pt2.x = 2*bw.rows;
          pt2.y = cvRound(y0 + (x0 + pt2.x) * a / b);
          line(showlines, pt1, pt2, Scalar(255,255,255), 1, CV_AA);
        }
    imshow("hough lines", showlines);

    // Specify size on vertical axis
    int verticalsize = vertical.rows / 30;
    // Create structure element for extracting vertical lines through morphology operations
    Mat verticalStructure = getStructuringElement(MORPH_RECT, Size( 1,verticalsize));
    // Apply morphology operations
    erode(vertical, vertical, verticalStructure, Point(-1, -1));
    dilate(vertical, vertical, verticalStructure, Point(-1, -1));
    // Show extracted vertical lines
    //imshow("vertical", vertical);
    // Inverse vertical image
    bitwise_not(vertical, vertical);
    //imshow("vertical_bit", vertical);
    // Extract edges and smooth image according to the logic
    // 1. extract edges
    // 2. dilate(edges)
    // 3. src.copyTo(smooth)
    // 4. blur smooth img
    // 5. smooth.copyTo(src, edges)
    // Step 1
    Mat edges;
    adaptiveThreshold(vertical, edges, 255, CV_ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 3, -2);
    imshow("edges", edges);
    // Step 2
    Mat kernel = Mat::ones(2, 2, CV_8UC1);
    dilate(edges, edges, kernel);
    //imshow("dilate", edges);
    // Step 3
    Mat smooth;
    vertical.copyTo(smooth);
    // Step 4
    blur(smooth, smooth, Size(2, 2));
    // Step 5
    smooth.copyTo(vertical, edges);
    // Show final result
    imshow("smooth", vertical);
    waitKey(0);
    return 0;
}
