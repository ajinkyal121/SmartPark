/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parkingsystem;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.*;
import org.opencv.*;
import org.opencv.imgproc.Imgproc;
import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_WIDTH;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;
/**
 *
 * @author E & T
 */
public class ParkingSystem {

    /**
     * @param args the command line arguments
     */
    static VideoCapture vCapture;
   static Mat mat;

    public static void main(String[] args) {
        // TODO code application logic here
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
      Mat img2 = new Mat();
         img2=Highgui.imread("D:\\2016-2017_modulewise_projects\\parkingsystemjava\\28Jan\\ParkingSystem\\preprocess\\adaptive_threshold.png");
         Mat mask = new Mat(img2.rows() + 2, img2.cols() + 2, CvType.CV_8U);
        Imgproc.floodFill(img2, mask, new Point(0, 0), new Scalar(0,0,0));
        Highgui.imwrite("flood.png", img2);
    }
    public static BufferedImage Mat2BufferedImage(Mat m) {
    // Fastest code
    // output can be assigned either to a BufferedImage or to an Image

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
    
    private static void showInFrame(Mat mat) {
      // TODO Auto-generated method stub
      JFrame mediaFrame = new JFrame("Media");
      mediaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mediaFrame.setVisible(true);
      mediaFrame.setSize(300,300);
      Highgui.imwrite("c:/private/img.jpg", mat);
      
      ImageIcon image = new ImageIcon("c:/private/img.jpg");
      JLabel label = new JLabel("", image, JLabel.CENTER);

      mediaFrame.add(label);
      mediaFrame.repaint();
      mediaFrame.validate();
      mediaFrame.setVisible(true);
   }
}
