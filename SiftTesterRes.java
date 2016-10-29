import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

//code from http://dummyscodes.blogspot.com/2015/12/using-siftsurf-for-object-recognition.html
public class SiftTesterRes {

	public static void main(String args[]){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		System.setProperty("java.library.path", "C:/Users/Crystal Ren/eclipse/jee-neon/eclips/opencv/build/java/x64/opencv_ffmpeg2413_64");
		System.loadLibrary("opencv_ffmpeg2413_64"); 
		
	        String template = "C:/Users/Crystal Ren/Desktop/butterflyTemplate3.PNG";
	        //String template = "C:/Users/Crystal Ren/Desktop/butterflyBelly.PNG";

	        Mat objectImage = Highgui.imread(template, Highgui.CV_LOAD_IMAGE_COLOR);
	        //Mat video_image = Highgui.imread(Tester, Highgui.CV_LOAD_IMAGE_COLOR);
	        
	    	
		   String testerVid ="C:/Users/Crystal Ren/Desktop/ButterflyVideos/Visible/Rec-butterfly_11_spot_superK_20percent_fore_leading-001181_cropped_resized.avi";
		   //String testerVid ="C:/Users/Crystal Ren/Desktop/Rec-butterfly_11_spot_superK_CLIP_1_editedTest3.avi"; //y2 is decr 462 px (check by px count)
	     

		     VideoCapture capture = new VideoCapture(testerVid);
			//capture.open(testerVid);
				
		     Mat video_image=new Mat();  

		     capture.read(video_image);  
	                    
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

	        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
	        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
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
	        descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 4);

	        //System.out.println("Calculating good match list...");
	        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();

	        float nndrRatio = 0.7f;

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

	        } else {
	            System.out.println("Object Not Found");
	        }
		    }
	}
}
}
