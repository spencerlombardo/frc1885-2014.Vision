#include <cv.h>
#include<highgui.h>

using namespace cv;
using namespace std;

int main(int argc, char ** argv)
{
	char * imageName = argv[1];
	cout<<"Image Name: "<<imageName<<endl<<" args: "<<argc<<endl;;

	Mat image,yuvImage,houghImage;
	Mat origImage;
	image = imread(imageName,1);
	origImage = imread(imageName,1);

	if(!image.data)
	{
		cout<<"Error loading image: "<<imageName<<endl;
		return 1;
	}

	//Convert to YUV color space. The goal of this 
	//is to zero out the light channel (Y) 
	cvtColor(image,yuvImage,CV_BGR2YCrCb);
	CvSize s = cvSize(yuvImage.rows, yuvImage.cols) ;
	int d = yuvImage.depth();

	//Split he image into the Y, U and V channels
	Mat planes[3]; 
	split(yuvImage,planes);


	//Zero out the Y channel
	planes[0] = Mat::zeros(planes[0].rows,planes[0].cols,planes[0].depth());
	//Combine the channels back to the image
	merge(planes,3,yuvImage);

	//Convert back to RGB
	cvtColor(yuvImage,image,CV_YCrCb2BGR);

	//Split the images to 0 out the blue and green channels. Will need to update
	//to point the blue channel when looking for blue goal
	split(image,planes);

	planes[0] = Mat::zeros(planes[0].rows,planes[0].cols,planes[0].depth());
	planes[1] = Mat::zeros(planes[0].rows,planes[0].cols,planes[0].depth());
	merge(planes,3,image);

	Mat src, dst, color_dst;
	src = image;

	//Canny edge detection. Will need to experiment with different threshold values:
	//http://docs.opencv.org/doc/tutorials/imgproc/imgtrans/canny_detector/canny_detector.html

	int canny_low_thresh = 50;
	int canny_high_thresh = 200;
	int canny_kernal_size = 3;
	
	Canny(src, dst, canny_low_thresh, canny_high_thresh,canny_kernal_size);

	//Convert the image with canny edges back to BGR rep. 
	cvtColor(dst, color_dst, CV_GRAY2BGR);
	vector<Vec4i>lines;


	//Hough Lines
	//http://docs.opencv.org/doc/tutorials/imgproc/imgtrans/hough_lines/hough_lines.html
	double hough_rho = 1;
	double hough_theta = CV_PI/180;
	int hough_thresh = 80;
	double hough_minLineLength = 30;
	double hough_maxLineGap = 10;
	HoughLinesP(dst, lines, hough_rho, hough_theta, hough_thresh, hough_minLineLength, hough_maxLineGap);


	//Draw lines found by Hough transform
	for(size_t i = 0; i<lines.size();i++)
	{
		line(color_dst, Point(lines[i][0], lines[i][1]),
				Point(lines[i][2], lines[i][3]), Scalar(0,0,255),3,8);
	}
	//Show the original/unaltered image
	namedWindow("ORIG_RGB", CV_WINDOW_AUTOSIZE);
	imshow("ORIG_RGB", origImage);

        namedWindow("Final Image",CV_WINDOW_AUTOSIZE);
	imshow("Final Image", color_dst);	

	waitKey(0);

	return 0;
}
