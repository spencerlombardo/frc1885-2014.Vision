#include <cv.h>
#include<highgui.h>

using namespace cv;
using namespace std;

Mat loadImage(char * pImageName)
{
	return imread(imageName, 1);
}

int main(int argc, char ** argv)
{
	char * imageName = argv[1];
	cout<<"Image Name: "<<imageName<<endl<<" args: "<<argc<<endl;;

	Mat image;
	image = loadImage(imageName);

	if(!image.data)
	{
		cout<<"Error loading image: "<<imageName<<endl;
		return 1;
	}
	namedWindow("ORIG_RGB", CV_WINDOW_AUTOSIZE);
	imshow("ORG_RGB", image);

	if(argc != 2 || !image.data)
	{
		printf("No image data \n");
		return -1;
	}
	cvtColor(image,image,CV_BGR2YCrCb);
	CvSize s = cvSize(image.rows, image.cols) ;
	int d = image.depth();


	Mat planes[3]; 
	//namedWindow("Orig_Image", CV_WINDOW_AUTOSIZE);
	//imshow("Orig_Image", image);
	split(image,planes);

	planes[0] = Mat::zeros(planes[0].rows,planes[0].cols,planes[0].depth());
	merge(planes,3,image);

	//namedWindow("PostImage", CV_WINDOW_AUTOSIZE);
	//imshow("PostImage", image);


	//Convert back to RGB
	cvtColor(image,image,CV_YCrCb2BGR);
	//namedWindow("FinalImage", CV_WINDOW_AUTOSIZE);
	//imshow("FinalImage", image);
	imwrite("SimplyRed.jpg", image);

	split(image,planes);

	planes[0] = Mat::zeros(planes[0].rows,planes[0].cols,planes[0].depth());
	planes[1] = Mat::zeros(planes[0].rows,planes[0].cols,planes[0].depth());
	merge(planes,3,image);

	Mat src, dst, color_dst;
	src = image;
	Canny(src, dst, 50, 200,3);
	cvtColor(dst, color_dst, CV_GRAY2BGR);
	vector<Vec4i>lines;
	HoughLinesP(dst, lines, 1, CV_PI/180, 80, 30, 10);
	for(size_t i = 0; i<lines.size();i++)
	{
		line(color_dst, Point(lines[i][0], lines[i][1]),
				Point(lines[i][2], lines[i][3]), Scalar(0,0,255),3,8);
	}
	vector<Vec3f>circles;
	Mat gray;
	cvtColor(src,gray,CV_BGR2GRAY);
	HoughCircles(gray,circles, CV_HOUGH_GRADIENT,
			gray.rows/32, //accumulator resolution
			500,
			100,
			200,
			50,
			100);

	cout<<"I found: " <<circles.size()<<" circles!"<<endl;

	for(size_t i=0;i<circles.size();i++)
	{
		Point center(cvRound(circles[i][0]),cvRound(circles[i][1]));
		int radius = cvRound(circles[i][2]);
		circle(src,center,3,Scalar(0,255,0),-1,8,0);
		circle(src,center,radius,Scalar(0,0,255),-3,8,0);
	}
	namedWindow("HOUGH_CIRCLE",CV_WINDOW_AUTOSIZE);
	imshow("HOUGH_CIRCLE", src);
	//namedWindow("Source", 1);
	//	imshow("Source", src);

	//namedWindow("Detected Lines",1);
	//imshow("DetectedLines", color_dst);

#if 0
	//This is the simple, sample
	Mat src, dst, color_dst;
	src = imread(imageName, 0);
	Canny(src, dst, 50, 200,3);
	cvtColor(dst, color_dst, CV_GRAY2BGR);
	vector<Vec4i>lines;
	HoughLinesP(dst, lines, 1, CV_PI/180, 80, 30, 10);
	for(size_t i = 0; i<lines.size();i++)
	{
		line(color_dst, Point(lines[i][0], lines[i][1]),
				Point(lines[i][2], lines[i][3]), Scalar(0,0,255),3,8);
	}
	namedWindow("Source", 1);
	imshow("Source", src);

	namedWindow("Detected Lines",1);
	imshow("DetectedLines", color_dst);

	vector<Vec3f>circles;
	HoughCircles(dst,circles, CV_HOUGH_GRADIENT,1,dst.rows/8,200,100,0,0);

	for(size_t i=0;i<circles.size();i++)
	{
		Point center(cvRound(circles[i][0]),cvRound(circles[i][1]));
		int radius = cvRound(circles[i][2]);
		circle(dst,center,3,Scalar(0,255,0),-1,8,0);
		circle(dst,center,radius,Scalar(0,0,255),-3,8,0);
	}
	namedWindow("HOUGH_CIRCLE",CV_WINDOW_AUTOSIZE);
	imshow("HOUGH_CIRCLE");
#endif	


#if 0
	//HoughLines(contours, lines, 1, CV_PI/180, 100, 0,0);

	Mat cdst;
	cvtColor(contours, cdst, CV_GRAY2BGR);

	for(size_t i = 0; i<lines.size();i++)
	{
		float rho = lines[i][0], theta = lines[i][1];
		Point pt1, pt2;
		double a = cos(theta), b = sin(theta);
		double x0 = a*rho, y0=b*rho;
		pt1.x = cvRound(x0 + 20*(-b));
		pt1.y = cvRound(y0 + 20*(a));
		pt2.x = cvRound(x0 - 20*(-b));
		pt2.y = cvRound(y0 - 20*(a));


		line( cdst, pt1, pt2, Scalar(0,0,255), 3, CV_AA);
	}
	namedWindow( "detected_lines", CV_WINDOW_AUTOSIZE );
	imshow( "detected_lines", cdst);
#endif
#if 0
	Mat detected_edges = image;
	//blur(image,detected_edges, Size(3,3));
	Mat contours;
	Canny(detected_edges, contours, 50,150); 

	namedWindow("Canny");
	imshow("Canny", contours);
	vector<vector<Point> > contoursVector;
	vector<Vec4i>hierarchy;
	findContours(contours,contoursVector, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0,0)); 

	Mat drawing = Mat ::zeros(contours.size(), CV_8UC3);

	Vec4i largestContour;
	double maxSize = 0;
	vector<vector<Point> > contours_poly( contoursVector.size() );
	for(int i=0;i<contoursVector.size();i++) 
	{
		//approxPolyDP(Mat(contoursVector[i]), contours_poly[i],3,true);
		//Rect aBoundRect = boundingRect(Mat(contours_poly[i]));
		Rect aBoundRect = boundingRect(Mat(contoursVector[i]));
		Scalar color = Scalar(255,255,255 );
		rectangle(drawing, aBoundRect.tl(), aBoundRect.br(), color, 2,8,0);
	}
	/// Show in a window
	namedWindow( "Contours", CV_WINDOW_AUTOSIZE );
	imshow( "Contours", drawing );
#endif
	waitKey(0);

	return 0;
}
