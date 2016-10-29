import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//code from http://dummyscodes.blogspot.com/2015/12/using-siftsurf-for-object-recognition.html
public class BaskingVidProc {

	public static void main(String args[]){
		
		boolean isKeyPointMatching = false;
		
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		System.setProperty("java.library.path", "C:/Users/Crystal Ren/eclipse/jee-neon/eclips/opencv/build/java/x64/opencv_ffmpeg2413_64");
		System.loadLibrary("opencv_ffmpeg2413_64"); 
		
	        String template = "C:/Users/Crystal Ren/Desktop/templateBasking.PNG";
	        //String template = "C:/Users/Crystal Ren/Desktop/butterflyBelly.PNG";

	        Mat objectImage = Highgui.imread(template, Highgui.CV_LOAD_IMAGE_COLOR);
	        //Mat video_image = Highgui.imread(Tester, Highgui.CV_LOAD_IMAGE_COLOR);
	        
	    	
			   String testerVid ="C:/Users/Crystal Ren/Desktop/finalVidClips/Rec-butterfly_13_basking_Xenon_30percent_60mW_10C-001212_frames_3436_3776.avi";
	     

		     VideoCapture capture = new VideoCapture(testerVid);
			//capture.open(testerVid);
				
		     Mat video_image=new Mat();  

		     capture.read(video_image);  
	                    
		     
		     //to do: find two lines of same intensity along the triangular shadow area (so that the light source can be traced)
		     
		     
	       if( capture.isOpened())  
		     {  
		      while( true )  
		      {  
		        capture.read(video_image);  
		        if (video_image.empty()){
			           System.out.println(" --(!) No captured frame -- Break!");  
			           break; 
		        } 
		        
	        capture.read(video_image); //reads captured frame into the Mat image
	          
	           //Functions.displayImage(Functions.Mat2BufferedImage(video_image));
	        
	        if(isKeyPointMatching){

	        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
	        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
	        //System.out.println("Detecting key points...");
	        featureDetector.detect(objectImage, objectKeyPoints);
	        KeyPoint[] keypoints = objectKeyPoints.toArray();

	        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
	        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
	        //System.out.println("Computing descriptors...");
	        descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);

	        // Create the matrix for output image.
	        Mat outputImage = new Mat(objectImage.rows()+20, objectImage.cols()+20, Highgui.CV_LOAD_IMAGE_COLOR);
	        Scalar newKeypointColor = new Scalar(255, 0, 0);

	        //System.out.println("Drawing key points on object image...");
	        Features2d.drawKeypoints(objectImage, objectKeyPoints, outputImage, newKeypointColor, 0);

	        // Match object image with the scene image
	        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
	        MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
	        //System.out.println("Detecting key points in background image...");
	        featureDetector.detect(video_image, sceneKeyPoints);
	        //System.out.println("Computing descriptors in background image...");
	        descriptorExtractor.compute(video_image, sceneKeyPoints, sceneDescriptors);

	        Mat matchoutput = new Mat(video_image.rows() * 2, video_image.cols() * 2, Highgui.CV_LOAD_IMAGE_COLOR);
	        //Scalar matchestColor = new Scalar(0, 255, 0);

	        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
	        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
	        //System.out.println("Matching object and scene images...");
	        descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);

	        //System.out.println("Calculating good match list...");
	        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();

	        float nndrRatio = 0.9f; //.825 for surf^2 and sift^2; .9 for SURF/SIFT

	        for (int i = 0; i < matches.size(); i++) {
	            MatOfDMatch matofDMatch = matches.get(i);
	            DMatch[] dmatcharray = matofDMatch.toArray();
	            DMatch m1 = dmatcharray[0];
	            DMatch m2 = dmatcharray[1];

	            if (m1.distance <= m2.distance * nndrRatio) {
	                goodMatchesList.addLast(m1);

	            }
	        }

	        if (goodMatchesList.size() >= 7) {
	            System.out.println("Object Found!");

	            List<KeyPoint> objKeypointlist = objectKeyPoints.toList();
	            List<KeyPoint> scnKeypointlist = sceneKeyPoints.toList();

	            LinkedList<Point> objectPoints = new LinkedList<>();
	            LinkedList<Point> scenePoints = new LinkedList<>();

	            for (int i = 0; i < goodMatchesList.size(); i++) {
	                objectPoints.addLast(objKeypointlist.get(goodMatchesList.get(i).queryIdx).pt);
	                scenePoints.addLast(scnKeypointlist.get(goodMatchesList.get(i).trainIdx).pt);
	            }

	            MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
	            objMatOfPoint2f.fromList(objectPoints);
	            MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
	            scnMatOfPoint2f.fromList(scenePoints);

	            Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 2.0); // was 2

	            Mat obj_corners = new Mat(3, 1, CvType.CV_32FC2);
	            Mat scene_corners = new Mat(3, 1, CvType.CV_32FC2);

	            obj_corners.put(0, 0, new double[]{0, 0});
	            obj_corners.put(1, 0, new double[]{objectImage.cols(), 0});
	            obj_corners.put(2, 0, new double[]{objectImage.cols(), objectImage.rows()});
	            obj_corners.put(3, 0, new double[]{0, objectImage.rows()});

	            //System.out.println("Transforming object corners to scene corners...");
	            Core.perspectiveTransform(obj_corners, scene_corners, homography);

	            //Mat img = Highgui.imread(Tester, Highgui.CV_LOAD_IMAGE_COLOR);

	            Core.line(video_image, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(255, 255, 255), 4);
	            Core.line(video_image, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(255, 255, 255), 4);
	            Core.line(video_image, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(255, 255, 255), 4);
	            Core.line(video_image, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(255, 255, 255), 4);

	           //System.out.println("Drawing matches image...");
	            MatOfDMatch goodMatches = new MatOfDMatch();
	            goodMatches.fromList(goodMatchesList);

	            
	            //Features2d.drawMatches(objectImage, objectKeyPoints, video_image, sceneKeyPoints, goodMatches, matchoutput, matchestColor, newKeypointColor, new MatOfByte(), 4);

	            Features2d.drawMatches(objectImage, objectKeyPoints, video_image, sceneKeyPoints, goodMatches, matchoutput, Scalar.all(-1), Scalar.all(-1), new MatOfByte(), Features2d.DRAW_RICH_KEYPOINTS);
	            //matchesMask Mask determining which matches are drawn. If the mask is empty, all matches are drawn. this is the penultimate parameter

				Functions.displayImage(Functions.Mat2BufferedImage(outputImage));
				Functions.displayImage(Functions.Mat2BufferedImage(matchoutput));
				//Functions.displayImage(Functions.Mat2BufferedImage(video_image));
				
	        	}
	        
	        }
	        
	        	else{
	        		
	        		Mat contourPic= video_image.clone();
	    			Imgproc.cvtColor(contourPic, contourPic, Imgproc.COLOR_BGR2GRAY);
	    			//Imgproc.threshold(contourPic, contourPic, 60, 255, Imgproc.THRESH_BINARY);
	    			//Functions.displayImage(Functions.Mat2BufferedImage(contourPic));
	    			//Imgproc.blur(contourPic, contourPic, new Size(2, 2)); //arbitrarily chosen blur filter of kernel size 2

	    			Imgproc.adaptiveThreshold(contourPic, contourPic, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15,2); 
	    			//Imgproc.blur(contourPic, contourPic, new Size(3, 3)); //arbitrarily chosen blur filter of kernel size 3
	    			Core.putText(contourPic, "ADAPTIVE_THRESH_GAUSSIAN_C (15,3)",new Point(30, 30) , 3,1.0,new Scalar(100,10,10,255),3); 
	    			//Functions.displayImage(Functions.Mat2BufferedImage(contourPic));

	    			//Imgproc.erode(contourPic, contourPic, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(2,2)), new Point(-1,-1),3);
	    			//Imgproc.erode(contourPic, contourPic, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(2,2)));
	    			//Imgproc.dilate(contourPic, contourPic, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(2,2)));

	    			

	    			Mat temp=video_image.clone(); //good to clone or no?
	    			
	    			Imgproc.blur(contourPic, temp, new Size(3, 3)); //arbitrarily chosen blur filter of kernel size 3
	    			Imgproc.Canny(temp, temp, 60, 60 * 3, 3, false);
	    			Mat dest = new Mat();
	    			Core.add(dest, Scalar.all(0), dest);
	    			video_image.copyTo(dest, temp);
	    			
	        		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	        		Functions.displayImage(Functions.Mat2BufferedImage(contourPic));
	        		
	        		Imgproc.findContours(contourPic, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
	        		Imgproc.drawContours(contourPic, contours, -1, new Scalar(255,0,0),2);
	        		

	        	}

	        } } else {
	            System.out.println("Object Not Found");
	        
		    }
	}
}

