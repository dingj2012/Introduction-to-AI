/**
	CS440_P1.cpp
	@author:
	@version:

	CS585 Image and Video Computing Fall 2014
	Assignment 2
	--------------
	This program:
		 Recognizes hand shapes or gestures and creates a graphical display 
	--------------
	
*/

#include "stdafx.h"

#include "opencv2/core/core.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <iostream>

using namespace cv;
using namespace std;

int myMax(int a, int b, int c);
int myMin(int a, int b, int c);
void mySkinDetect(Mat& src, Mat& dst);
void myFrameDifferencing(Mat& prev, Mat& curr, Mat& dst);
//void myMotionEnergy(Vector<Mat> mh, Mat& dst);



int main()
{
	VideoCapture cap(0); 

	// if not successful, exit program
    if (!cap.isOpened())  
    {
        cout << "Cannot open the video cam" << endl;
        return -1;
    }
	
	//create the window
	namedWindow("MyVideo",WINDOW_AUTOSIZE);

	// read a new frame from video
	Mat frame0;
	bool bSuccess0 = cap.read(frame0); 

	//if not successful, break loop
    if (!bSuccess0) 
	{
			cout << "Cannot read a frame from video stream" << endl;
	}

	while (1)
    {

		// read a new frame from video
        Mat frame;
        bool bSuccess = cap.read(frame); 

		//if not successful, break loop
        if (!bSuccess) 
        {
             cout << "Cannot read a frame from video stream" << endl;
             break;
        }

		Mat frameDest;
		frameDest = Mat::zeros(frame.rows, frame.cols, CV_8UC1); //Returns a zero array of same size as src mat, and of type CV_8UC1
		myFrameDifferencing(frame0, frame, frameDest);

		mySkinDetect(frame, frameDest);

        //detects the whether there is skin in the camera.
        

		std::map<int,int> frameChanges;
		int maxVal = 0;
		int maxKey = -1;

		int count = 0;
		int i = 0;
        while (i < frameDest.rows) {
            
            int equalizer = 0;
            int colLength = 0;
            for (int j = 0; j < frameDest.cols; j++) {
                

                int color_hull = frameDest.at<uchar>(i,j);

                if (color_hull == 255 && equalizer == 0){ 
					colLength = equalizer + 1;
					equalizer = 255;
				}else if(color_hull == 255 && equalizer != 0){
					colLength++;
					equalizer = 255;
                }else if (color_hull == 0 && equalizer == 255) {
					if (colLength > 20 && colLength < 80) {
						count++;
					}
					equalizer = 0;
				}else if (color_hull == 0 && equalizer != 255){
                    equalizer = 0;
                }
            }
            
			
            if (count != 0) {
				frameChanges[count]++;
            }

			i++;
			
        }
        

        for (std::map<int,int>::iterator it=frameChanges.begin(); it!=frameChanges.end(); ++it) {
            if (it->second > maxVal) {
                maxVal = it->second;
                maxKey = it->first;
            }
        }
		
        
		if (maxKey <= 100){
			putText(frameDest, "corner", Point(30,30), FONT_HERSHEY_PLAIN, 3.0, cvScalar(200,200,250), 1, CV_AA);
		}else if(maxKey > 300){
			putText(frameDest, "motion detected", Point(30,30), FONT_HERSHEY_PLAIN, 3.0, cvScalar(200,200,250), 1, CV_AA);
		}else{
			putText(frameDest, "center", Point(30,30), FONT_HERSHEY_PLAIN, 3.0, cvScalar(200,200,250), 1, CV_AA);
		}


		imshow("MyVideo", frameDest);

		if (waitKey(30) == 27) 
		{
			cout << "esc key is pressed by user" << endl;
			break; 
		}

	}
	
	cap.release();
	return 0;
}

int myMax(int a, int b, int c) {
	int m = a;
    (void)((m < b) && (m = b)); 
    (void)((m < c) && (m = c));
     return m;
}

int myMin(int a, int b, int c) {
	int m = a;
    (void)((m > b) && (m = b)); 
    (void)((m > c) && (m = c)); 
     return m;
}

void myFrameDifferencing(Mat& prev, Mat& curr, Mat& dst) {
	//For more information on operation with arrays: http://docs.opencv.org/modules/core/doc/operations_on_arrays.html
	//For more information on how to use background subtraction methods: http://docs.opencv.org/trunk/doc/tutorials/video/background_subtraction/background_subtraction.html
	absdiff(prev, curr, dst);
	Mat gs = dst.clone();
	cvtColor(dst, gs, CV_BGR2GRAY);
	dst = gs > 50;
	Vec3b intensity = dst.at<Vec3b>(100,100);
}


void mySkinDetect(Mat& src, Mat& dst) {
	for (int i = 0; i < src.rows; i++){
		for (int j = 0; j < src.cols; j++){
			Vec3b intensity = src.at<Vec3b>(i,j);
			int B = intensity[0]; int G = intensity[1]; int R = intensity[2];
			if ((R > 95 && G > 40 && B > 20) && (myMax(R,G,B) - myMin(R,G,B) > 15) && (abs(R-G) > 15) && (R > G) && (R > B)){
				dst.at<uchar>(i,j) = 255;
			}
		}
	}
}



