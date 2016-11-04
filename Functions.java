 import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;  
 import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.VideoCapture;
import org.opencv.video.Video;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
 import org.opencv.imgproc.Moments;  
 import org.opencv.core.CvType;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.KeyPoint;
import org.opencv.core.Core;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;  
 import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

//import javax.swing.JLabel; 
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;

/**
 * @author Crystal Ren
 * a repository for commonly used methods
 */
public class Functions {

	/**
	 * 
	 */
	static double spf=.03333333; //for the butterfly only clips from superK
	
	//sampling one of two images tho is tha tpart accounted for
	
	public Functions() {
	}
	
	public static void showContours(ArrayList<MatOfPoint> contours, Mat src, Mat dst){
		//finding contours
		Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
		Imgproc.drawContours(dst, contours, -1, new Scalar(255,255,255),2);
	}
   
	//get COM of one contour and draw it
	public static Point getCOMofOneContour(MatOfPoint contour, Mat dst){

			Moments mu = Imgproc.moments(contour, false);
		//mass center
			MatOfPoint2f approxCurve = new MatOfPoint2f();
			Point p = new Point( mu.get_m10() / mu.get_m00() , mu.get_m01()/mu.get_m00() );
     		Core.circle(dst, p, 3, new Scalar(255,255,255), 3); //circle and related in imgproc for 3.0 and core for 2.4
 
     		//Convert contour from MatOfPoint to MatOfPoint2f
     		MatOfPoint2f contour2f = new MatOfPoint2f( contour.toArray() );
     		//Processing on mMOP2f1 which is in type MatOfPoint2f
     		double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
     		Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

     		//Convert back to MatOfPoint
     		MatOfPoint pointMat = new MatOfPoint( approxCurve.toArray() );

     		// Get bounding rect of contour
     		Rect rect = Imgproc.boundingRect(pointMat);
     		
     		// draw enclosing rectangle (all same color, but you could use variable i to make them unique)
     		Core.rectangle(dst, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255,255,255), 3);
     		//System.out.println("testing");
     		
     		return p;
	}
	public static Point COMnRec(ArrayList<MatOfPoint> contours, Mat dst){
	   //finding mass center of contours
		//return mass center of largest contour
		List<Moments> mu = new ArrayList<Moments>(contours.size());
		for (int i = 0; i < contours.size(); i++) {
			mu.add(i, Imgproc.moments(contours.get(i), false));
		}

		//mass center
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		List<Point> mc = new ArrayList<Point>(contours.size());
		
		for( int i = 0; i < contours.size(); i++ ){
			Point p = new Point( mu.get(i).get_m10() / mu.get(i).get_m00() , mu.get(i).get_m01()/mu.get(i).get_m00() );
			mc.add(i, p);
     		Core.circle(dst, p, 3, new Scalar(0,0,0), 3);
 
     		//Convert contours(i) from MatOfPoint to MatOfPoint2f
     		MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
     		//Processing on mMOP2f1 which is in type MatOfPoint2f
     		double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
     		Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

     		//Convert back to MatOfPoint
     		MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

     		// Get bounding rect of contour
     		Rect rect = Imgproc.boundingRect(points);

     		// draw enclosing rectangle (all same color, but you could use variable i to make them unique)
     		Core.rectangle(dst, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255,0,0), 3); 
		}
		int idxMax = getLargestContour(contours);
		
		
		//added to get the points of the largest contour
		Point[] contourEdgePoints=contours.get(idxMax).toArray();
		for (int i=0; i<contourEdgePoints.length;i++){
	    	Point p = contourEdgePoints[i];
		    Core.circle(dst, p, 3, new Scalar(0,255,255), 3);
		    //System.out.println("Contour Edge (x,y): ("+p.x+","+p.y+")");
	    }
	    
		
		
		return mc.get(idxMax);
	}
	

	public static int getLargestContour(ArrayList<MatOfPoint> contours){
		//return index of the largest contour
		 double maxArea = -1;
		 int maxAreaIdx = -1;
		
		//begin copy 
		   MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point
		   Mat largest_contour = contours.get(0);	   
		   for (int i = 0; i < contours.size(); i++) {
		        temp_contour = contours.get(i);
		        double contourArea = Imgproc.contourArea(temp_contour);
		        //compare this contour to the previous largest contour found
		        if (contourArea > maxArea) {
		            maxArea=contourArea;
		            maxAreaIdx=i;
		            largest_contour = temp_contour;
		        }
		    }
		//end copy
		
		return maxAreaIdx;
		
	}
	
	
	public static double getLargestArea(ArrayList<MatOfPoint> contours){
		//return index of the largest contour
		 double maxArea = -1;
		 int maxAreaIdx = -1;
		
		//begin copy 
		   MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point
		   Mat largest_contour = contours.get(0);	   
		   for (int i = 0; i < contours.size(); i++) {
		        temp_contour = contours.get(i);
		        double contourArea = Imgproc.contourArea(temp_contour);
		        //compare this contour to the previous largest contour found
		        if (contourArea > maxArea) {
		            maxArea=contourArea;
		            maxAreaIdx=i;
		            largest_contour = temp_contour;
		        }
		    }
		//end copy
		
		return maxArea;
		
	}
	
	public static int getSecondLargestContour(ArrayList<MatOfPoint> contours){
		//return index of the largest contour
		 double maxArea = -1;
		 int nextMaxAreaIdx =-1;
		 double nextMaxArea = -1;
		
		//begin copy 
		   MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point
		   for (int i = 0; i < contours.size(); i++) {
		        temp_contour = contours.get(i);
		        double contourArea = Imgproc.contourArea(temp_contour);
		        //compare this contour to the previous largest contour found
		        if (contourArea > maxArea) {
		            maxArea=contourArea;
		        }
		        else if(contourArea>nextMaxArea){
		        	nextMaxArea=contourArea;
		        	nextMaxAreaIdx=i;
		        }
		        
		    }
		//end copy
		
		return nextMaxAreaIdx;
		
	}
	
	//get contour with white in it??? for IR video to deal with the contour selection jumping to the hand

	
	/**
	//problems: entire line gets turned that color you dont get a continuous thing
	public static Scalar getColor(Point pt, ArrayList<Point> comPoints){
		//speed will be black =0 bc of contrast purposes
		double speed=getSpeed(pt,comPoints);
		//int colorTemp=Integer.valueOf((int) Math.round(speed));
		int colorTemp=255-Integer.valueOf((int) Math.round(speed));
		Scalar color= new Scalar(255,colorTemp, colorTemp); //the 0 should be 255 for dark blue=fast
		return color;
	}
	
	
	public static void drawLine(Mat dst, Point pt, ArrayList<Point> comPoints){
		//save all the com points in an arraylist to draw continuous line
		Scalar color = getColor(pt,comPoints);
		for(int i=0; i<comPoints.size()-1; i++){
			Core.line(dst, comPoints.get(i), comPoints.get(i+1), color, 1);
		}
		
	}
	
	
	 //the accel stuff saved for later
	 
	 

	
	public static void drawAccelLine(Mat dst, Point pt1, Point pt2){
		//speed will be blue
		double speed=getSpeed(pt1, pt2);
		Scalar color= new Scalar(255,speed,speed);
		Imgproc.line(dst, pt1, pt2, color, 1);
	}
	
	*/
	public static Point getPrevPt(ArrayList<Point> points){
		Point prevPoint = points.get(points.size()-1);
		return prevPoint;
	}
	
	
	//was boolean, temporarily List<KeyPoint>
	//public static List<KeyPoint> isMatchBRISK(Mat smallImage, Mat largeImage){
		public static boolean isMatchBRISK(Mat smallImage, Mat largeImage){

		FeatureDetector  fd = FeatureDetector.create(FeatureDetector.SIFT); 
	    final MatOfKeyPoint keyPointsLarge = new MatOfKeyPoint();
	    final MatOfKeyPoint keyPointsSmall = new MatOfKeyPoint();

	    fd.detect(largeImage, keyPointsLarge);
	    fd.detect(smallImage, keyPointsSmall);

	    //System.out.println("keyPoints.size() : "+keyPointsLarge.size());
	    //System.out.println("keyPoints2.size() : "+keyPointsSmall.size());

	    Mat descriptorsLarge = new Mat();
	    Mat descriptorsSmall = new Mat();

	    DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.OPPONENT_SIFT);
	    extractor.compute(largeImage, keyPointsLarge, descriptorsLarge);
	    extractor.compute(smallImage, keyPointsSmall, descriptorsSmall);
	    
	    

	    //System.out.println("descriptorsA.size() : "+descriptorsLarge.size());
	    //System.out.println("descriptorsB.size() : "+descriptorsSmall.size());

	    MatOfDMatch matches = new MatOfDMatch();

	    DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
	    matcher.match(descriptorsLarge, descriptorsSmall, matches);

	    //System.out.println("matches.size() : "+matches.size());

	    MatOfDMatch matchesFiltered = new MatOfDMatch();

	    List<DMatch> matchesList = matches.toList();
	    List<DMatch> bestMatches= new ArrayList<DMatch>();

	    Double max_dist = 0.0;
	    Double min_dist = 100.0;

	    for (int i = 0; i < matchesList.size(); i++)
	    {
	        Double dist = (double) matchesList.get(i).distance;

	        if (dist < min_dist && dist != 0)
	        {
	            min_dist = dist;
	        }

	        if (dist > max_dist)
	        {
	            max_dist = dist;
	        }

	    }

	    /**
	    System.out.println("max_dist : "+max_dist);
	    System.out.println("min_dist : "+min_dist);
		*/
	    
	  //added to show key points and print coordinates
	    List<KeyPoint> tempKeyPointArrL=keyPointsLarge.toList();
	    List<KeyPoint> tempKeyPointArrS=keyPointsSmall.toList();
	    //System.out.println("Tester key points");
	    
	    /**tester
	    System.out.println("Size of L keypoint array"+keyPointsLarge.elemSize());
    	System.out.println("Size of L temp array"+tempKeyPointArrL.size());
    	System.out.println("Size of S keypoint array"+keyPointsSmall.elemSize());
    	System.out.println("Size of S temp array"+tempKeyPointArrS.size());
    	end */
    	
	    for (int i=0; i<keyPointsLarge.elemSize();i++){
	    	Point p = tempKeyPointArrL.get(i).pt;
		    Core.circle(largeImage, p, 3, new Scalar(0,255,255), 3);
		    //System.out.println("(x,y): ("+p.x+","+p.y+")");
	    }
	    //System.out.println("Template key points:");
	    Mat tempSmall=smallImage.clone();
		
	    for (int i=0; i<keyPointsSmall.elemSize();i++){
	    	Point p = tempKeyPointArrS.get(i).pt;
		    Core.circle(tempSmall, p, 3, new Scalar(0,255,255), 3);
		    //System.out.println("(x,y): ("+p.x+","+p.y+")");
	    	
	    }
	    
	    //ImageViewer imageViewer = new ImageViewer();
	    //imageViewer.show(tempSmall, "template with key points");
	    
	    
	    
	    if(min_dist > 50 )
	    {
        return false;
	    }

	    double threshold = 2* min_dist; //was 3
	    double threshold2 = 1 * min_dist; //was 2

	    if (threshold > 50) //was 75
	    {
	        threshold  = 50; //was 75
	    }
	    else if (threshold2 >= max_dist)
	    {
	        threshold = min_dist * 1.1; //was 1.1
	    }
	    else if (threshold >= max_dist)
	    {
	        threshold = threshold2 * 1.4; //was 1.4
	    }
	    //System.out.println("Threshold : "+threshold);

	    for (int i = 0; i < matchesList.size(); i++)
	    {
	        Double dist = (double) matchesList.get(i).distance;

	        if (dist < threshold)
	        {
	            bestMatches.add(matches.toList().get(i));
	            //System.out.println(String.format(i + " best match added : %s", dist));
	        }
	    }

	    matchesFiltered.fromList(bestMatches);

	    //System.out.println("matchesFiltered.size() : " + matchesFiltered.size());


	    if(matchesFiltered.rows() >= 1) //changed 4 to 1
	    {
	    	getMatchLines(smallImage, largeImage,keyPointsSmall, keyPointsLarge, matches);	    	
	    	return true;
	    }
	    
	    else
	    {
	        return false;
	    }
		
	    //return tempKeyPointArrL;
	}
		
	public static List<KeyPoint> getKeyPoints(Mat image, Mat dst){
			FeatureDetector  fd = FeatureDetector.create(FeatureDetector.ORB); 
		    final MatOfKeyPoint keyPoints = new MatOfKeyPoint();
		    fd.detect(image, keyPoints);
		    List<KeyPoint> keyPointList=keyPoints.toList();
		    //for testing; show keypoints
		    
		    for (int i=0; i<keyPoints.elemSize();i++){
		    	Point p =keyPointList.get(i).pt;
			    Core.circle(dst, p, 3, new Scalar(0,255,255), 3);
			    //System.out.println("(x,y): ("+p.x+","+p.y+")");
		    }
		    
		    return keyPointList;
		}
	
	//get the contour index where the most KP are on or in the contour
	public static int getContIdxMostKP(ArrayList<MatOfPoint> contours, List<KeyPoint> keyPoints){
		int maxKPCont =0;
		int numKPCont=0;
		int maxIdx=0;
		MatOfPoint2f tempContour = new MatOfPoint2f();
		for(int i=0; i<contours.size();i++){
			contours.get(i).convertTo(tempContour, CvType.CV_32FC2);;
			for(int j=0; j<keyPoints.size();j++){
				KeyPoint keyPt = keyPoints.get(j);
				Point pt = keyPt.pt;
				if(Imgproc.pointPolygonTest(tempContour, pt, false)>=0){
					numKPCont++;
				}

			}
			if(numKPCont>maxKPCont){
				maxKPCont=numKPCont;
				maxIdx=i;
			}
			
		}
		return maxIdx;
	}
	
	
	public static void getMatchLines(Mat template, Mat tester, MatOfKeyPoint templateKeyPoints, MatOfKeyPoint testerKeyPoints, MatOfDMatch matches){
		Mat imageOut = tester.clone();
    	Features2d.drawMatches(tester, testerKeyPoints, template, templateKeyPoints, matches, imageOut);
	}
	
	public static BufferedImage Mat2BufferedImage(Mat m){ //to convert mat to java image
		// source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
		// Fastest code
		// The output can be assigned either to a BufferedImage or to an Image

		    int type = BufferedImage.TYPE_BYTE_GRAY;
		    if ( m.channels() > 1 ) {
		        type = BufferedImage.TYPE_3BYTE_BGR;
		    }
		    int bufferSize = m.channels()*m.cols()*m.rows();
		    byte [] b = new byte[bufferSize];
		    m.get(0,0,b); // get all the pixels
		    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		    System.arraycopy(b, 0, targetPixels, 0, b.length);  
		    return image;

		}
	
	public static void displayImage(Image img2) //to display an image
	{   
	    //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
	    ImageIcon icon=new ImageIcon(img2);
	    JFrame frame=new JFrame();
	    frame.setLayout(new FlowLayout());        
	    frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
	    JLabel lbl=new JLabel();
	    lbl.setIcon(icon);
	    frame.add(lbl);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	
	public static Mat colorThresh(Mat hsvMat, Mat array255, Mat distance, boolean isIRVid){
		 List<Mat> lhsv = new ArrayList<Mat>(3);
		 Mat thresholded=new Mat();  
	     Mat thresholded2=new Mat();
	     Scalar hsv_min = new Scalar(0, 100, 20, 0);  
	     Scalar hsv_max = new Scalar(30, 255, 255, 0);  
	     Scalar hsv_min2 = new Scalar(170, 100, 20, 0);  
	     Scalar hsv_max2 = new Scalar(180, 255, 255, 0);  
	     
	     if(isIRVid==false){
	    	 hsv_min.set(new double[]{0, 0, 0, 0});  
		     hsv_max.set(new double[]{40, 150, 100, 0});  
		     hsv_min2.set(new double[]{175, 50, 0, 0});  
		     hsv_max2.set(new double[]{180, 150, 100, 0}); 
		      
		     	/**
		     	 * hsv_min.set(new double[]{0, 90, 51, 0});  
		     hsv_max.set(new double[]{17, 255, 255, 0});  
		     hsv_min2.set(new double[]{175, 50, 50, 0});  
		     hsv_max2.set(new double[]{179, 255, 255, 0});
		     	 */
	     }
	     // One way to select a range of colors by Hue    
         Core.inRange(hsvMat, hsv_min, hsv_max, thresholded);           
         Core.inRange(hsvMat, hsv_min2, hsv_max2, thresholded2);
         Core.bitwise_or(thresholded, thresholded2, thresholded);
         
      // Notice that the thresholds don't really work as a "distance"  
         // Ideally we would like to cut the image by hue and then pick just  
         // the area where S combined V are largest.  
         // Strictly speaking, this would be something like sqrt((255-S)^2+(255-V)^2)>Range  
         // But if we want to be "faster" we can do just (255-S)+(255-V)>Range  
         // Or otherwise 510-S-V>Range  
         // Anyhow, we do the following... Will see how fast it goes...  
         Core.split(hsvMat, lhsv); // We get 3 2D one channel Mats  
         Mat S = lhsv.get(1);  
         Mat V = lhsv.get(2);  
         Core.subtract(array255, S, S);  
         Core.subtract(array255, V, V);  
         S.convertTo(S, CvType.CV_32F);  
         V.convertTo(V, CvType.CV_32F);  
         Core.magnitude(S, V, distance);  
         Core.inRange(distance,new Scalar(0.0), new Scalar(200.0), thresholded2);  
         Core.bitwise_and(thresholded, thresholded2, thresholded);
         
         return thresholded;
	}

	public static Mat getCannyEdge(Mat src, double threshold){
		Mat grayImage=src.clone(); //maybe should leave uninit
		Mat temp=src.clone(); //good to clone or no?
		Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY);
		Imgproc.blur(grayImage, temp, new Size(3, 3)); //arbitrarily chosen blur filter of kernel size 3
		Imgproc.Canny(temp, temp, threshold, threshold * 3, 3, false);
		Mat dest = new Mat();
		Core.add(dest, Scalar.all(0), dest);
		src.copyTo(dest, temp);
		//ImageViewer imageViewer = new ImageViewer();
		//imageViewer.show(dest, "Found edges");
		return dest;
	}

	public static double isShapeFound(Mat edgeS, Mat edgeL){
		ArrayList<MatOfPoint> contoursS = new ArrayList<MatOfPoint>();
		ArrayList<MatOfPoint> contoursL = new ArrayList<MatOfPoint>();
		
		Imgproc.findContours(edgeS, contoursS, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
		Imgproc.findContours(edgeL, contoursL, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
		Imgproc.drawContours(edgeS, contoursS, -1, new Scalar(255,255,255));
		Imgproc.drawContours(edgeL, contoursL, -1, new Scalar(255,255,255));
		
		//return Imgproc.matchShapes(edgeS, edgeL, Imgproc.CV_CONTOURS_MATCH_I3, 0);
		return Imgproc.matchShapes(edgeS, edgeL, Imgproc.CV_CONTOURS_MATCH_I3, 0);
	/**
		if(Imgproc.matchShapes(edgeS, edgeL, Imgproc.CV_CONTOURS_MATCH_I3, 0)<.4){
			return true;
		}
		return false;
		*/
	
	}
	
	/**
	 * gets extreme values of a contour 
	 * 
	 */
	public static Point[] getExtremeValues(MatOfPoint contour){
		Point[] exVals= new Point[4];
		Point top = new Point();
		Point bottom = new Point();
		Point left = new Point();
		Point right = new Point();
		List<Point> points = contour.toList();
		Point start =points.get(0);
		double xmax=start.x;
		double xmin=start.x;
		double ymax=start.y;
		double ymin=start.y;
		Point xmaxp= start;
		Point ymaxp= start;
		Point xminp= start;
		Point yminp= start;

		for(int i =0;i<points.size(); i++){
			Point temp=points.get(i);
			if(temp.x<xmin){
				xmin=temp.x;
				xminp = temp;
			}
			if(temp.x>xmax){
				xmax=temp.x;
				xmaxp = temp;

			}
			if (temp.y<ymin){
				ymin=temp.y;
				yminp = temp;

			}
			if(temp.y>ymax){
				ymax=temp.y;
				ymaxp = temp;

			}
		}
		
		exVals[0]=xminp;
		exVals[1]=xmaxp;
		exVals[2]=yminp;
		exVals[3]=ymaxp;
	
		return exVals;
		
	}
	
	
	/**
	 * using the extreme values calculate the two that make up the longest line
	 * 
	 */
	public static Point[] getOrientation(Point[] exVals){
		double tempDist;
		Point p1=exVals[0];
		Point p2=exVals[1];
		double longestLine= euDist(p1,p2);

		Point[] majorAxis = new Point[2];
		
		for(int i=0; i<exVals.length;i++){
			for (int j=0; j<exVals.length;j++){
				tempDist = euDist(exVals[i],exVals[j]);
				if(tempDist>longestLine){
					longestLine=tempDist;
					p1=exVals[i];
					p2=exVals[j];
				}
			}
		}
		
		majorAxis[0]=p1;
		majorAxis[1]=p2;
		return majorAxis;
	}
		
	
	
	/**
	 * using the extreme values calculate the longest one and then 
	 * track the closest exval in the next contour to keep the orientation line
	 * 
	 */
	public static Point[] trackOrientation(Point[] majorAxis, Point[] exVals){
		Point[] nextMA= new Point[2];
		nextMA[0]=exVals[0];
		nextMA[1]=exVals[0];
		//double shortestDist0=0; //this kinda works but clearly is wrong
		//double shortestDist1=0;
		
		//for the first new major axis point
		double shortestDist0=euDist(majorAxis[0],exVals[0]);		
		for(int i=0; i<exVals.length;i++){
			double dist0=euDist(majorAxis[0],exVals[i]);			
			if(dist0<=shortestDist0){
				nextMA[0]=exVals[i];
				shortestDist0=dist0;
			}
		}
		
		//for the second new major axis point
		
		double shortestDist1=euDist(majorAxis[1],exVals[0]);
		for(int i=0; i<exVals.length;i++){
			double dist1=euDist(majorAxis[1],exVals[i]);
			
			if(dist1<=shortestDist1){
				nextMA[1]=exVals[i];
				shortestDist1=dist1;
			}
		}
		return nextMA;
		
		
		
		/** old code
		 * 
		 * for(int i=0; i<exVals.length;i++){
			double dist0=euDist(majorAxis[0],exVals[i]);
			double dist1=euDist(majorAxis[1],exVals[i]);
			
			if(dist0<shortestDist0 && dist0!=0){
				nextMA[0]=exVals[i];
				shortestDist0=dist0;
			}
			if(dist1<shortestDist1 && dist1!=0){
				nextMA[1]=exVals[i];
				shortestDist1=dist1;
			}
			System.out.println("distance 0 "+shortestDist0+"distance 1 "+shortestDist1);
		}
		 */
	}
	
	/**
	 * finds the euclidean distance between two points
	 * @param p1
	 * @param p2
	 * @return distance between p1 and p2
	 */
	public static double euDist(Point p1, Point p2){
		double distance = Math.sqrt( (p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y) );
		return distance;
	}
	
	/**
	 * finds the pixels from the midpoint's y value to the top edge of the image
	 * @param p1
	 * @return distance between midpoint's y val and y=0
	 */
	public static double yDist(Point midpt){
		return midpt.y;
	}
	
	/**
	 * find the midpoint of two points (for speed and accel tracking)
	 * @param points
	 * @return
	 */
	public static Point getMidpoint(Point[] points){
		Point midpt = new Point();
		midpt.x=(points[0].x+points[1].x)/2;
		midpt.y=(points[0].y+points[1].y)/2;
		return midpt;
	}

	public static double getSpeed(Point oldMidpt, Point nextMidpt){
		//this gets a pixel per second???
		double speed=euDist(oldMidpt, nextMidpt)/spf;
		return speed;
	}
	
	public static double getAccel(double oldSpeed, double newSpeed){
		double accel = (oldSpeed-newSpeed) / spf;
		return accel;
	}
	
	/**
	 * find the angle of the orientation line from the horizontal
	 * @param points
	 * @return angle
	 */
	public static double getAngleTwoLines(Point[] endpts){
		Point r1=new Point(150,300); //r1 and r2 are reference points
		Point r2=new Point(200,300);
		Point x1=endpts[0];
		Point x2=endpts[1];
		double denominator = Math.sqrt((r2.x-r1.x)*(r2.x-r1.x)+(r2.y-r1.y)*(r2.y-r1.y))*Math.sqrt((x2.x-x1.x)*(x2.x-x1.x)+(x2.y-x1.y)*(x2.y-x1.y));
		double angle = Math.acos(((r2.x-r1.x)*(x2.x-x1.x)+(r2.y-r1.y)*(x2.y-x1.y))/denominator);
		angle=angle*(180/Math.PI); //acos returns angle between 0 and pi
		return angle;
	}
	
	/**
	 * find the angular speed
	 * @param the old angle and the new angle
	 * @return angular speed
	 */
	public static double getAngularSpeed(double angle1, double angle2){
		double angSpeed = (angle1-angle2)/spf;
		//System.out.println(angle2); //testing
		return angSpeed;
	}
	
	public static void laserAvoidanceCalibration(Point[] exVals, Mat img){

		for (int i=0; i < exVals.length; i++){
			
			Core.putText(img, Integer.toString(i), exVals[i] , 3,1.0,new Scalar(255,255,255),2);

		}
		
		
	}
	//BELOW RELATES TO DEALING WITH TWO DISTINCT PARTS OF ONE CONTOUR
	public static Point[] connectTailHead(MatOfPoint largestContour, ArrayList<MatOfPoint> contours, int radius, int upperBoundArea, Mat dst){
		Point pref = getCOMofOneContourV2(largestContour);
		ArrayList<Point> coms = getCOMofContourList(contours);
		double temp = 0;
		int idxSecContour=0;
		double contourArea;
		outerloop:
		for(int i=0; i<coms.size();i++){
			temp=euDist(pref,coms.get(i));
			//Core.circle(dst, coms.get(i), 100, new Scalar(255,255,255),3); //TESTING
			contourArea = Imgproc.contourArea(contours.get(i));
			//Core.putText(dst, String.valueOf(contourArea),coms.get(i) , 3,1.0,new Scalar(255,255,255),3); //TESTING
			if(temp<radius && temp!=0 && contourArea<upperBoundArea){//to prevent it picking the dish
				idxSecContour = i;
				break outerloop;
				}
			else{
				temp = 0;
			}
		}
		
		
		Point[] refExVal = getExtremeValues(largestContour);
		if(temp!=0){
			Point[] secExVal = getExtremeValues(contours.get(idxSecContour));
			Point[] unionExVal = new Point[4];
			List<Point> union = new ArrayList<Point>();
			for(int i=0;i<4;i++){
				union.add(refExVal[i]);
				union.add(secExVal[i]);
			}
			
			Point start = union.get(0);
			double xmax=start.x;
			double xmin=start.x;
			double ymax=start.y;
			double ymin=start.y;
			Point xmaxp= start;
			Point ymaxp= start;
			Point xminp= start;
			Point yminp= start;

			for(int i =1;i<union.size(); i++){
				Point test=union.get(i);
				if(test.x<xmin){
					xmin=test.x;
					xminp = test;
				}
				if(test.x>xmax){
					xmax=test.x;
					xmaxp = test;
				
				}
				if (test.y<ymin){
					ymin=test.y;
					yminp = test;

				}
				if(test.y>ymax){
					ymax=test.y;
					ymaxp = test;

				}
			}
		
			unionExVal[0]=xminp;
			unionExVal[1]=xmaxp;
			unionExVal[2]=yminp;
			unionExVal[3]=ymaxp;
	
			return unionExVal;
		}
		else{
			return refExVal;
		}
		//Point[] getExtremeValues(MatOfPoint contour)
		
	}
	
	/**
	 * finding com of a list of contours
	 * @param contours
	 * @return
	 */
	public static ArrayList<Point> getCOMofContourList(ArrayList<MatOfPoint> contours){

		ArrayList<Point> coms = new ArrayList<Point>();
		
		for(int i=0;i<contours.size();i++){
			coms.add(getCOMofOneContourV2(contours.get(i)));

		}
			return coms;
		}
	
	/**
	 * Just finding com of one contour and returning that
	 * @param contour
	 * @return com of the contour as a point
	 */
	public static Point getCOMofOneContourV2(MatOfPoint contour){
		Moments mu = Imgproc.moments(contour, false);
		Point p = new Point( mu.get_m10() / mu.get_m00() , mu.get_m01()/mu.get_m00() );
 		return p;
}
	
}
