
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;
public class HeatAvoidance {

	public static void main(String args[]){
		

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Scanner sc = new Scanner(System.in);
		String testerVid = sc.next();

		     VideoCapture capture = new VideoCapture(testerVid);

		     Mat video_image=new Mat();  
		  
		     int frame = 0;
		     int count=0;
		     Point[] track=new Point[2];
		     Point[] exVals = new Point[4];
 			 Point[] majorAxis = new Point[2];
 			 Point oldMidpt = new Point();
 			 double oldAngle=0.0;
 			 double newAngle =0.0;
		     //to do: find two lines of same intensity along the triangular shadow area (so that the light source can be traced)
		     

	       if( capture.isOpened())  //capture is the video file
		     { 
	    	

		      while( true )  
		      {  
		    	  frame++;
		    		  capture.read(video_image);
		        
		        if (video_image.empty()){
			           System.out.println(" --(!) No captured frame--");  
			           break; 
		        } 
		       
	        capture.read(video_image); //reads captured frame into the Mat image
	        

			
	        		//Mat video_image= video_image.clone();
	    			Imgproc.cvtColor(video_image, video_image, Imgproc.COLOR_BGR2GRAY);
	    			

	
	    				Imgproc.threshold(video_image, video_image, 95, 255, Imgproc.THRESH_BINARY);
	    				//Functions.displayImage(Functions.Mat2BufferedImage(video_image));
	    				
	    				Imgproc.dilate(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3,3)));
	    				Imgproc.dilate(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3,3)));

	    				Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));
	    				Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));
	    				Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));
	    				Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));
	    				Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));
	    				
	    				Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));
	    				//Imgproc.dilate(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3,3)));
		    			//Imgproc.dilate(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3,3)));
	    				//Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));
	    				//Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));
	    				//Core.putText(video_image, "Frame"+frame,new Point(55, 150) , 3,1.0,new Scalar(255,255,255),3); //for test
	    				//}
	    				//else if (frame>=504){

		    			
			    		//Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(4,4)));

	    					
	    				//}
		    			Imgproc.adaptiveThreshold(video_image, video_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15,2); 
		    			Imgproc.dilate(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3,3)));
		    			//Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(4,4)));

		    			//Functions.displayImage(Functions.Mat2BufferedImage(video_image));
	    			
	    			Core.bitwise_not(video_image, video_image);
	    			
			      //below horizontal and vertical to remove unwanted contours in image
	    			//horizontal
	    			if(frame<=105 || frame>=407){
	    			for(int i=0;i<video_image.cols();i++){
		    			 for(int j=10;j<100;j++){
		    				 video_image.put(j, i, 0);
		    				 }
		    			}
	    			}
	    			else if(frame>=380){
	    				for(int i=0;i<video_image.cols();i++){
			    			 for(int j=10;j<50;j++){
			    				 video_image.put(j, i, 0);
			    				 }
			    			}	
	    			}
	    			
	    			if(frame==0){
		    			for(int i=0;i<video_image.cols();i++){
			    			 for(int j=10;j<200;j++){
			    				 video_image.put(j, i, 0);
			    				 }
			    			}
		    			}
	    			
	    			//horizontal
	    			//if(frame<=199){
	    			for(int i=0;i<video_image.cols();i++){
		    			 for(int j=400;j<600;j++){
		    				 video_image.put(j, i, 0);
		    				 }
		    			}
	    			//}
	    			//vertical
		    			for(int i=0;i<300;i++){
			    			 for(int j=0;j<video_image.rows();j++){
			    				 video_image.put(j, i, 0);
			    				 }
			    			}

		    		//vertical
		    			if(frame>=1){
		    				for(int i=650;i<800;i++){
				    			 for(int j=0;j<video_image.rows();j++){
				    				 video_image.put(j, i, 0);
				    				 }
				    			}
		      }
		    			if(frame>=360){
		    				//vertical
		    				for(int i=600;i<800;i++){
				    			 for(int j=0;j<video_image.rows();j++){
				    				 video_image.put(j, i, 0);
				    				 }
				    			}
		    				
		    				
		    			}
		    			
	

		    			
	    			ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    			Imgproc.findContours(video_image, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
	    			

	    			
	    			//Imgproc.drawContours(video_image, contours, -1, new Scalar(255,255,255), -1); //drawing filled contours

	    			//Functions.displayImage(Functions.Mat2BufferedImage(video_image));
	    			

	    			
	    			ArrayList<MatOfPoint> contours2 = new ArrayList<MatOfPoint>();
	    			

	    			Imgproc.findContours(video_image, contours2, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

	    			contours2.trimToSize();
	    			Imgproc.drawContours(video_image, contours2, -1, new Scalar(255,255,255),-1);
	    			
	    			//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	    			
	    			 //int maxIdx=Functions.getLargestContour(contours);
	    			
	    			if(contours2.size()!=0){
	    				
	    				count++;
	    			double maxArea = -1;
	    			int maxAreaIdx = -1;
	    			MatOfPoint temp_contour; //contours.get(0); //the largest is at the index 0 for starting point
	    			MatOfPoint largest_contour = contours2.get(0);
	    			
   
	    			
	    			for( int i = 0; i < contours2.size(); i++ ){
	    				temp_contour = contours2.get(i);
   			       Rect rect = Imgproc.boundingRect(temp_contour);
   			       if(rect.x<600 && rect.x>250 && rect.y>10 && rect.y<400){
   			    	   
	    	     			 double contourArea = Imgproc.contourArea(temp_contour);
	    	     			if (contourArea > maxArea) {
	    	     				maxArea=contourArea;
	    	     				maxAreaIdx=i;
	    	     				largest_contour = temp_contour;
	    	     		   	}
	    	     		}
	    	     		
	    			}

	    			Imgproc.drawContours(video_image, contours2, -1, new Scalar(255,255,255), -1); //drawing filled contours

	        		
	    			//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	        		
	    			//RADIUS FOR CHOOSING HERE
	    			exVals = Functions.connectTailHead(largest_contour, contours2, 100, 1500, video_image);
	    			majorAxis=Functions.getOrientation(exVals);
	    			

	    			Core.circle(video_image, exVals[0], 5, new Scalar(255,255,255),3);
	    			Core.circle(video_image, exVals[1], 5, new Scalar(255,255,255),3);
	    			Core.circle(video_image, exVals[2], 5, new Scalar(255,255,255),3);
	    			Core.circle(video_image, exVals[3], 5, new Scalar(255,255,255),3);
	    			//Core.circle(video_image, exVals[3], 60, new Scalar(255,255,255),3);

	    			
	    			if(count==1){
	    				track=majorAxis;
	    				//Core.line(video_image, track[0], track[1], new Scalar(255,255,255));
	    				Core.line(video_image, exVals[1], exVals[2], new Scalar(0,0,0)); //testing&calibration
	    				//System.out.println("TEST");//testing
	    				//for this particular video clip either 0,1 or 2,1
	    				//Functions.laserAvoidanceCalibration(exVals, video_image);
	    				track[0]=exVals[1];
	    				track[1]=exVals[2];
	    				oldMidpt=Functions.getMidpoint(track);
	    				//System.out.println(Functions.yDist(oldMidpt));

	    				//oldAngle=Functions.getAngleTwoLines(track);
	    				//so in this case have to force fit the orientation to the first two values of exVals
	    				//Core.circle(video_image, majorAxis[0], 5, new Scalar(255,255,255),3);
		    			//Core.circle(video_image, majorAxis[1], 5, new Scalar(255,255,255),3);
		    			//Functions.displayImage(Functions.Mat2BufferedImage(video_image)); //testing
		    		
	    			}
	    			
	    			/*
	    			else if(frame==280){
	    				track[0]=exVals[1];
	    				track[1]=exVals[2];
	    				track = Functions.trackOrientation(track, exVals);
	    				Point nextMidpt = Functions.getMidpoint(track);
	    				//System.out.println(Functions.yDist(nextMidpt));
	    				System.out.println(Functions.getAngleTwoLines(track)); //prints just angle
	    				//"frame: "+frame+ " " + 
	    				oldAngle = newAngle;
	    				Core.line(video_image, track[0], track[1], new Scalar(0,0,0));
		    			//Core.circle(video_image, track[1], 5, new Scalar(255,255,255),3);
		    			oldMidpt = nextMidpt;
			    		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	    			}
	    			*/
	    			
	    			
	    			else{ // if(frame<=544)
	    				
	    				track = Functions.trackOrientation(track, exVals);
	    				Point nextMidpt = Functions.getMidpoint(track);
	    				//System.out.println(Functions.yDist(nextMidpt));
	    				System.out.println(Functions.getAngleTwoLines(track)); //prints just angle
	    				//"frame: "+frame+ " " + 
	    				oldAngle = newAngle;
	    				Core.line(video_image, track[0], track[1], new Scalar(0,0,0));
		    			//Core.circle(video_image, track[1], 5, new Scalar(255,255,255),3);
		    			oldMidpt = nextMidpt;
			    		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	    			}
	    			
	    			
	    			Core.putText(video_image, "Frame"+frame,new Point(55, 150) , 3,1.0,new Scalar(255,255,255),3); //for test

	    		if(frame>=1){//frame>=250 || frame <= 300){ //void frame 415
		    		Functions.displayImage(Functions.Mat2BufferedImage(video_image));
		    		
		    		try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
		    	}
	    		
}
    		
		    		
	    		}
	    		
		      
   	     		//Rect maxRect = Imgproc.boundingRect(largest_contour); //for testing
   	     		//Core.rectangle(video_image, new Point(maxRect.x,maxRect.y), new Point(maxRect.x+maxRect.width,maxRect.y+maxRect.height), new Scalar(255,0,0), 3); //for testing
	    		//Point tster = new Point(700, 100);
	    		//Core.circle(video_image, tster, 5, new Scalar(255,255,255));
	    		//Point tster = new Point(600,650);
	    		//Core.circle(video_image, tster, 30, new Scalar(255,255,255));
   	     		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	    		 //end of if contours2 size checking
		      }
		      
		     } 


}
	      
