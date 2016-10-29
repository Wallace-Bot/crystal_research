import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.features2d.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;

import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.contrib.Contrib;;

public class SiftTest {

		
	public static void main(String args[]){
		 // Load the native library.  
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			
			//setting native library path to access the opencv_ffmpeg310 dll!!! YAY
			//System.setProperty("java.library.path", "C:/Users/Crystal Ren/Downloads/opencv/build/java/x64/opencv_ffmpeg310_64.dll");
			//System.loadLibrary("opencv_ffmpeg310_64");
			
			/**
			//loading old version opencv to use sift and surf
			System.setProperty("java.library.path", "C:/Users/Crystal Ren/Downloads/opencv/build/java/x64/opencv_java2413.dll");
			System.loadLibrary("opencv_java2413"); 
			*/
			
		String filePath= "C:/Users/Crystal Ren/Desktop/butterflyTemplate2.PNG";
		Mat template = Highgui.imread(filePath);

		String filePath2= "C:/Users/Crystal Ren/Desktop/antLarge.PNG";
		Mat tempTester = Highgui.imread(filePath2);
	
			//added
		//Mat showEdgePic = Functions.getCannyEdge(tempTester, 65); //65 instead of 60??
		//cvtcolor for shapematch
		//Imgproc.cvtColor(showEdgePic, showEdgePic, Imgproc.COLOR_BGR2GRAY);
		//Imgproc.cvtColor(template, template, Imgproc.COLOR_BGR2GRAY);
			System.out.println("Found match " + Functions.isMatchBRISK(template, tempTester)); //added temp
			
			//imageViewer.show(showEdgePic, "Edges found");
			
		/**
			Mat edgePic = Functions.getCannyEdge(template, 60);
			Mat contourPic = edgePic.clone();
			Imgproc.cvtColor(contourPic, contourPic, Imgproc.COLOR_BGR2GRAY);
			ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Functions.showContours(contours, contourPic, contourPic);
			List<KeyPoint> keyPoints = Functions.getKeyPoints(template, template);
			int maxIdx= Functions.getContIdxMostKP(contours, keyPoints);
			Functions.getCOMofOneContour(contours.get(maxIdx), contourPic);
			*/
			
			Functions.displayImage(Functions.Mat2BufferedImage(template));
			//Functions.displayImage(Functions.Mat2BufferedImage(contourPic));
			//Functions.displayImage(Functions.Mat2BufferedImage(edgePic));
		
			//see how the displayed edge points are marked in the canny edge as well?
			//have to deal with that
			
	
	     
		    
		      
	}


}

