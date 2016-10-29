import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LaserAvoidance {

	//angular speed; ang diff/time diff
	public static void main(String args[]){
		

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

	String testerVid ="C:/Users/Crystal Ren/Desktop/ButterflyVideos/Visible/butterfly only clips from superK/Rec-butterfly_11_spot_superK_CLIP_2.avi";

		  //String testerVid ="C:/Users/Crystal Ren/Desktop/ButterflyVideos/Visible/butterfly only clips from superK/calibrate_resized.avi";
		     VideoCapture capture = new VideoCapture(testerVid);
			//capture.open(testerVid);
				//System.out.println("total frames:"+capture.get(7));
		     Mat video_image=new Mat();  

		     int frame =0;
		     Point[] track=new Point[2];
		     Point[] exVals = new Point[4];
 			 Point[] majorAxis = new Point[2];
 			 Point oldMidpt = new Point();
		     //to do: find two lines of same intensity along the triangular shadow area (so that the light source can be traced)
		     
		     
	       if( capture.isOpened())  //capture is the video file
		     { 
	    	

		      while( true )  
		      {  
		    	  frame++;
		        capture.read(video_image);
		 
		        
		        //TimeUnit.SECONDS.sleep(1);
		        if (video_image.empty()){
			           System.out.println(" --(!) No captured frame -- Break!");  
			           break; 
		        } 
		       
	        capture.read(video_image); //reads captured frame into the Mat image
	        

	        int centerX =video_image.cols()/2;
			int centerY =video_image.rows()/2;
			//int radius = 330;

			Point center= new Point(centerX,centerY);
			
	        		//Mat video_image= video_image.clone();
	    			Imgproc.cvtColor(video_image, video_image, Imgproc.COLOR_BGR2GRAY);
	    			Imgproc.threshold(video_image, video_image, 75, 255, Imgproc.THRESH_BINARY); //thresh was 55 changed to 75
//was 145
	    			Imgproc.adaptiveThreshold(video_image, video_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15,2); 
	    			//Mat temp = new Mat();
	    	        //Imgproc.Canny(video_image, temp, 60, 60 * 3, 3, false);
	    			//Imgproc.threshold(video_image, video_image, 120, 255, Imgproc.THRESH_BINARY_INV); //thresh was 55 changed to 75
	        		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));
	        		//Functions.displayImage(Functions.Mat2BufferedImage(temp));


/**
	    			//make a circle of radius 507px and make everything outside of it white
	    			//Mat mask = Mat.zeros( video_image.rows()+2,video_image.cols()+2, CvType.CV_8UC1 );
		 			Mat mask = Mat.zeros( video_image.rows()+2,video_image.cols()+2, CvType.CV_8UC1 );
	    			Core.ellipse(mask, center, new Size(325,270), 10, 0, 360, new Scalar(255,255,255), 1);
	    			Core.ellipse(video_image, center, new Size(325,270), 10, 0, 360, new Scalar(255,255,255),1);
		    		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	    			Point tster = new Point(centerX+300,centerY+200);
	    			Point tster2 = new Point(centerX+300,centerY-50);
	    			Point tster3 = new Point(centerX-30,centerY+240);
	    			//Core.circle(video_image, tster, 5, new Scalar(255,255,255));
	        		

					//Core.circle(mask, center, radius, new Scalar(255,255,255), 3);

	    			Imgproc.floodFill(video_image, mask, tster3, new Scalar(255,255,255));
	    			Imgproc.floodFill(video_image, mask, new Point(0,video_image.rows()/2), new Scalar(255,255,255));

	    			Imgproc.floodFill(video_image, mask, new Point(video_image.cols()-1,0), new Scalar(255,255,255));
	    			Imgproc.floodFill(video_image, mask, tster2, new Scalar(255,255,255));
	    			Imgproc.floodFill(video_image, mask, new Point(video_image.cols()-1,video_image.rows()-1), new Scalar(255,255,255));
	    			Imgproc.floodFill(video_image, mask, tster, new Scalar(255,255,255));
					//Core.circle(video_image, center, radius, new Scalar(255,255,255), 3);
*/
	    			//Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(2,2)), new Point(-1,-1),3);
	    			Imgproc.dilate(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(2,2)));
	    			Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(2,2)));

	    			//Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));

	    			

	        		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));
	    			
	    			Core.bitwise_not(video_image, video_image);
	        		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));
	    			
	    		

	    			//Core.circle(video_image, tster2, 5, new Scalar(0,0,0));
	        		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	    			ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    			Imgproc.findContours(video_image, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
	    			contours.trimToSize();
	    			Imgproc.drawContours(video_image, contours, -1, new Scalar(255,255,255),2);
	    			
		    		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	    			 //int maxIdx=Functions.getLargestContour(contours);
	    			double maxArea = -1;
	    			int maxAreaIdx = -1;
	    			MatOfPoint temp_contour; //contours.get(0); //the largest is at the index 0 for starting point
	    			MatOfPoint largest_contour = contours.get(0);
	    			   
	    			for( int i = 0; i < contours.size(); i++ ){
	    				temp_contour = contours.get(i);
    			       Rect rect = Imgproc.boundingRect(temp_contour);
    			       if(rect.x+rect.width<(centerX+400)&& rect.x>100){
	    	     			 double contourArea = Imgproc.contourArea(temp_contour);
	    	     			if (contourArea > maxArea) {
	    	     				maxArea=contourArea;
	    	     				maxAreaIdx=i;
	    	     				largest_contour = temp_contour;
	    	     		   	}
	    	     		}
	    	     		
	    			}
	    			//FIGURE OUT THE CHRONOLOGY
	    			exVals = Functions.getExtremeValues(largest_contour);
	    			majorAxis=Functions.getOrientation(exVals);
	    			
	    			Core.circle(video_image, exVals[0], 5, new Scalar(255,255,255),3);
	    			Core.circle(video_image, exVals[1], 5, new Scalar(255,255,255),3);
	    			Core.circle(video_image, exVals[2], 5, new Scalar(255,255,255),3);
	    			Core.circle(video_image, exVals[3], 5, new Scalar(255,255,255),3);

	    			
	    			if(frame==1){
	    				track=majorAxis;
	    				//Core.line(video_image, track[0], track[1], new Scalar(255,255,255));
	    				Core.line(video_image, exVals[2], exVals[1], new Scalar(255,255,255)); //testing&calibration
	    				//for this particular video clip either 0,1 or 2,1
	    				track[0]=exVals[1];
	    				track[1]=exVals[2];
	    				oldMidpt=Functions.getMidpoint(track);
	    				//so in this case have to force fit the orientation to the first two values of exVals
	    				Core.circle(video_image, majorAxis[0], 5, new Scalar(255,255,255),3);
		    			Core.circle(video_image, majorAxis[1], 5, new Scalar(255,255,255),3);
		    			//Functions.displayImage(Functions.Mat2BufferedImage(video_image)); //testing
		    			

	    			}
	    			else{
	    				
	    				track = Functions.trackOrientation(track, exVals);
	    				Point nextMidpt = Functions.getMidpoint(track);
	    				System.out.println(Functions.getSpeed(oldMidpt, nextMidpt));
	    				Core.line(video_image, track[0], track[1], new Scalar(255,255,255));
	    				Core.circle(video_image, track[0], 5, new Scalar(255,255,255),3);
		    			Core.circle(video_image, track[1], 5, new Scalar(255,255,255),3);
		    			oldMidpt = nextMidpt;
	    			}
	    			
	    			
	    			Core.putText(video_image, "Frame"+frame,new Point(55, 150) , 3,1.0,new Scalar(255,255,255),3); //for test

	    			Functions.displayImage(Functions.Mat2BufferedImage(video_image));

    	     		Rect maxRect = Imgproc.boundingRect(largest_contour); //for testing
    	     		Core.rectangle(video_image, new Point(maxRect.x,maxRect.y), new Point(maxRect.x+maxRect.width,maxRect.y+maxRect.height), new Scalar(255,0,0), 3); //for testing
	    			//Point tster = new Point(centerX+400, centerY);
	    			//Core.circle(video_image, tster, 5, new Scalar(255,255,255));
	    			//Point tster = new Point(600,650);
	    			//Core.circle(video_image, tster, 30, new Scalar(255,255,255));
    	     		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));
		    		//System.out.println(maxArea);
		    		
		    	
    	     		
	    			
		      }
		     } 
	      
	    		   	 
	    		   	
	    		      }}
	    		  
