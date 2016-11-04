
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

public class LaserAvoidance {

	public static void main(String args[]){
		//EDITED TO HAVE FILLED CONTOUR IN ORDER TO DEAL WITH THE SHAKY LINE FINDING from the first clip: eroded the filled contour

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Scanner sc = new Scanner(System.in);
		String testerVid = sc.next();

		     VideoCapture capture = new VideoCapture(testerVid);

		     Mat video_image=new Mat();  
		  
		     int frame =0;
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
	    			
	    			
	    				Imgproc.threshold(video_image, video_image, 110, 255, Imgproc.THRESH_BINARY);

	    				Imgproc.dilate(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3,3)));
	    				Imgproc.dilate(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3,3)));

	    				Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));
	    				Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));
	    				//Core.putText(video_image, "Frame"+frame,new Point(55, 150) , 3,1.0,new Scalar(255,255,255),3); //for test
	    				//Functions.displayImage(Functions.Mat2BufferedImage(video_image));
	    				//}
	    				//else if (frame>=504){
	    				Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));

	    				Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));

			    		//Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(4,4)));

	    					
	    				//}
		    			Imgproc.adaptiveThreshold(video_image, video_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15,2); 
		    			Imgproc.dilate(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3,3)));
		    			//Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(4,4)));

		    			//Functions.displayImage(Functions.Mat2BufferedImage(video_image));
	    			
	    			Core.bitwise_not(video_image, video_image);

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
   			       if(rect.x<550 && rect.x>250 && rect.y>50 && rect.y<500){
   			    	   
	    	     			 double contourArea = Imgproc.contourArea(temp_contour);
	    	     			if (contourArea > maxArea) {
	    	     				maxArea=contourArea;
	    	     				maxAreaIdx=i;
	    	     				largest_contour = temp_contour;
	    	     		   	}
	    	     		}
	    	     		
	    			}

	    			Imgproc.drawContours(video_image, contours2, -1, new Scalar(255,255,255), 2); //drawing filled contours

	        		
	    			//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	        		
	    			//FIGURE OUT THE CHRONOLOGY
	    			exVals = Functions.getExtremeValues(largest_contour);
	    			majorAxis=Functions.getOrientation(exVals);
	    			
	    			Core.circle(video_image, exVals[0], 5, new Scalar(255,255,255),3);
	    			Core.circle(video_image, exVals[1], 5, new Scalar(255,255,255),3);
	    			Core.circle(video_image, exVals[2], 5, new Scalar(255,255,255),3);
	    			Core.circle(video_image, exVals[3], 5, new Scalar(255,255,255),3);

	    			
	    			if(count==1){
	    				track=majorAxis;
	    				//Core.line(video_image, track[0], track[1], new Scalar(255,255,255));
	    				Core.line(video_image, exVals[2], exVals[3], new Scalar(0,0,0)); //testing&calibration
	    				//for this particular video clip either 0,1 or 2,1
	    				//Functions.laserAvoidanceCalibration(exVals, video_image);
	    				track[0]=exVals[2];
	    				track[1]=exVals[3];
	    				oldMidpt=Functions.getMidpoint(track);
	    				System.out.println(Functions.yDist(oldMidpt));

	    				//oldAngle=Functions.getAngleTwoLines(track);
	    				//so in this case have to force fit the orientation to the first two values of exVals
	    				//Core.circle(video_image, majorAxis[0], 5, new Scalar(255,255,255),3);
		    			//Core.circle(video_image, majorAxis[1], 5, new Scalar(255,255,255),3);
		    			//Functions.displayImage(Functions.Mat2BufferedImage(video_image)); //testing
		    		
	    			}
	    			/*
	    			else if(frame==298){
		    			
	    				track[0]=exVals[3];
	    				track[1]=exVals[2];
	    				track = Functions.trackOrientation(track, exVals);
	    				Point nextMidpt = Functions.getMidpoint(track);
	    				//System.out.println(Functions.getSpeed(oldMidpt, nextMidpt));
	    				System.out.println(Functions.getAngleTwoLines(track)); //prints just angle
	    				//System.out.println(Functions.getAngularSpeed(oldAngle, newAngle));
	    				//Functions.getAngularSpeed(oldAngle, newAngle);
	    				oldAngle = newAngle;
	    				Core.line(video_image, track[0], track[1], new Scalar(0,0,0));
	    				Core.circle(video_image, track[0], 5, new Scalar(255,255,255),3);
		    			Core.circle(video_image, track[1], 5, new Scalar(255,255,255),3);
		    			oldMidpt = nextMidpt;
		    			
		    			//Core.putText(video_image, "Frame"+frame,new Point(55, 150) , 3,1.0,new Scalar(255,255,255),3); //for test

		    			//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	    			}
	    		*/
	    			else{
	    				
	    				track = Functions.trackOrientation(track, exVals);
	    				Point nextMidpt = Functions.getMidpoint(track);
	    				//System.out.println(Functions.yDist(nextMidpt));
	    				System.out.println(Functions.getAngleTwoLines(track)); //prints just angle
	    				//"frame: "+frame+ " " + 
	    				oldAngle = newAngle;
	    				Core.line(video_image, track[0], track[1], new Scalar(0,0,0));
		    			//Core.circle(video_image, track[1], 5, new Scalar(255,255,255),3);
		    			oldMidpt = nextMidpt;
		    			
	    			}
	    			
	    			
	    			Core.putText(video_image, "Frame"+frame,new Point(55, 150) , 3,1.0,new Scalar(255,255,255),3); //for test

	    		//if(frame<=272)
		    		Functions.displayImage(Functions.Mat2BufferedImage(video_image));
	    		
	    		
		    		
	    		}
					
	    		
		      
   	     		//Rect maxRect = Imgproc.boundingRect(largest_contour); //for testing
   	     		//Core.rectangle(video_image, new Point(maxRect.x,maxRect.y), new Point(maxRect.x+maxRect.width,maxRect.y+maxRect.height), new Scalar(255,0,0), 3); //for testing
	    			//Point tster = new Point(700, 100);
	    			//Core.circle(video_image, tster, 5, new Scalar(255,255,255));
	    			//Point tster = new Point(600,650);
	    			//Core.circle(video_image, tster, 30, new Scalar(255,255,255));
   	     		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));
		    		
		    		
		    	
	    			} //end of if contours2 size checking
		      }
		      
		     } 
	      
	    		   	 
	    		   	
	    		      }
	    		  
