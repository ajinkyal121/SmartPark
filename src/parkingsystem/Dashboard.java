/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parkingsystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;
import javax.swing.ImageIcon;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.*;
import org.opencv.*;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_WIDTH;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;
import parkingsystem.MatToBufImg;


/**
 *
 * @author User
 */
public class Dashboard extends javax.swing.JFrame {

    private DaemonRGBThread myRGBThread;
    private Thread rgbT;
    private VideoCapture video = new VideoCapture();
    
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    volatile BufferedImage img = null;

    long l = 20;
    int count = 14;

    /**
     * Daemon Thread to framing the video or camera
     */
    class DaemonRGBThread implements Runnable {

        protected volatile boolean runnable = false;

       
        @Override
        public void run() {
            synchronized (this) {
                while (runnable) {
                    //  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                    /**
                     * Returns true if video capturing has been initialized
                     * already. If the previous call to VideoCapture constructor
                     * or VideoCapture.open succeeded, the method returns true
                     */

                    
                        /**
                         * Grabs, decodes and returns the next video frame.
                         */
                        if(count==2168)
                        {
                        count=14;
                        }
                        count++;
                        try {
                           // System.out.println(count);
                            Mat backgrd=Highgui.imread("Images\\15.jpg");
                            Mat imgX = new Mat(); 
                            int slotAvl=0;
                            int carPark=0;
                           imgX=Highgui.imread("Images\\"+count+".jpg");
                           Mat gray1=new Mat();
                           Mat gray2=new Mat();
                           //convert to gray
                           Imgproc.cvtColor(backgrd, gray1, Imgproc.COLOR_BGR2GRAY);
                           Imgproc.cvtColor(imgX, gray2, Imgproc.COLOR_BGR2GRAY);
                           Mat diff=new Mat();
                           //background subtraction
                           Core.absdiff(gray1, gray2, diff);
                           Mat thresh=new Mat();
                           //convert to binary
                           Imgproc.threshold(diff, thresh, 25, 255,Imgproc.THRESH_BINARY);
                           
                           
                            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                            Mat mHierarchy=new Mat();
                            //finding contours
                            Imgproc.findContours(thresh, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
                            for(int i=0; i< contours.size();i++){
                            if ((Imgproc.contourArea(contours.get(i)) > 20000)&&(Imgproc.contourArea(contours.get(i)) < 50000)){//40000//20000//10000//5000
                               // System.out.println(Imgproc.contourArea(contours.get(i)));
                             Rect rect = Imgproc.boundingRect(contours.get(i));      
                             carPark++;
                               Core.rectangle(imgX, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,0,255));
                                
            
        }}
                 //calculating available slots
                 slotAvl=8-carPark;   
                 if(slotAvl<0)
                 {slotAvl=0;}
               
                            if (count % 15 == 0) {
//                                URL url = new URL("http://192.168.1.50:8080/parking_system/rest/AppService/UpdateAvailableParkingSlots?parkingId=1&slotAvailable=" + slotAvl);
//                                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//                                String line = in.readLine();
//                                System.out.println(line);
                            }
              //   System.out.println("Parking Slots Available="+slotAvl);
                            setImage(imgX);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                            System.out.println(ee.toString());
                        }

                    

                    if (runnable == false) {
                        System.out.println("Going to wait()");
                    }
                }
            }
        }
    }

    /**
     * Creates new form Dashboard
     *
     * @param id
     */
    public Dashboard() {

        initComponents();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(sd.width / 2 - this.getWidth() / 2, sd.height / 2 - this.getHeight() / 2);

        loadConfiguaration();

        /**
         * Start the daemon thread
         */
        myRGBThread = new DaemonRGBThread();
        rgbT = new Thread(myRGBThread);
        rgbT.setDaemon(true);
        myRGBThread.runnable = true;
        rgbT.start();

    }

    /**
     * Load default configuration,
     */
    private void loadConfiguaration() {

       

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dashboard");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel19.setFont(new java.awt.Font("Calibri", 0, 36)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 102, 0));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("PARKING SYSTEM 2");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(278, 278, 278))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preview", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 18))); // NOI18N
        jPanel3.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N

        jLabel11.setBackground(new java.awt.Color(0, 0, 0));
        jLabel11.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jLabel11.setOpaque(true);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Converts mat To BufferedImage
     *
     * @param matrix
     * @return
     */
    public static BufferedImage matToBufferedImage(Mat matrix) {
        if (matrix.channels() == 1) {
            int cols = matrix.cols();
            int rows = matrix.rows();
            int elemSize = (int) matrix.elemSize();
            byte[] data = new byte[cols * rows * elemSize];
            int type;
            matrix.get(0, 0, data);
            switch (matrix.channels()) {
                case 1:
                    type = BufferedImage.TYPE_BYTE_GRAY;
                    break;
                case 3:
                    type = BufferedImage.TYPE_3BYTE_BGR;
                    // bgr to rgb
                    byte b;
                    for (int i = 0; i < data.length; i = i + 3) {
                        b = data[i];
                        data[i] = data[i + 2];
                        data[i + 2] = b;
                    }
                    break;
                default:
                    return null;
            }

            BufferedImage image2 = new BufferedImage(cols, rows, type);
            image2.getRaster().setDataElements(0, 0, cols, rows, data);
            return image2;
        }

        if (matrix.channels() == 3) {
            int widthm = matrix.width(), heightm = matrix.height(), channels = matrix.channels();
            byte[] sourcePixels = new byte[widthm * heightm * channels];
            matrix.get(0, 0, sourcePixels);
            // create new image and get reference to backing data
            BufferedImage image = new BufferedImage(widthm, heightm, BufferedImage.TYPE_3BYTE_BGR);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
            return image;
        }
        return null;
    }
    
    public static BufferedImage MatToBufferedImage(Mat matBGR){  
       
        int width = matBGR.width(), height = matBGR.height(), channels = matBGR.channels() ;  
        byte[] sourcePixels = new byte[width * height * channels];  
        matBGR.get(0, 0, sourcePixels);  
        // create new image and get reference to backing data  
      //  System.out.println(width+"wid and height"+height);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);  
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();  
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);  
        
        
        return image;  
    }  

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        int rtn = JOptionPane.showConfirmDialog(this, "Are you Sure want to close the window ?", "Close Window", YES_NO_OPTION);
        if (rtn == JOptionPane.YES_OPTION) {
            if (!(rgbT == null)) {
                myRGBThread.runnable = false;
                rgbT.interrupt();
                video.release();
            }
            setDefaultCloseOperation(EXIT_ON_CLOSE);//yes
        } else {
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);//cancel
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * Set image to jlable11 ie preview image
     *
     * @param mat
     */
    public void setImage(Mat mat) {
        if (mat == null) {
            img = null;
        } else {
            //MatToBufImg mb=new MatToBufImg(mat, ".png");
            this.img = MatToBufferedImage(mat);
            //this.img=mb.getImage();
            Image imageScaled = this.img.getScaledInstance(
                    640,
                    500, Image.SCALE_SMOOTH);
            ImageIcon iic = new ImageIcon(imageScaled);
            jLabel11.setIcon(iic);
        }
    }

    /**
     * Query to system to for get connected cameras
     *
     * @return
     */
    private int[] loadCamera() {
        ArrayList camDevice = new ArrayList();
        int count = 0;
        for (int device = 0; device < 10; device++) {
            VideoCapture cap = new VideoCapture(device);
            if (cap.isOpened()) {
                camDevice.add((count++), device);
            }
            cap.release();
        }

        int[] intArray = new int[camDevice.size()];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = (int) camDevice.get(i);
        }

        return intArray;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        try{
      //  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
        String path="D:\\opencv_java2410.dll";
        System.load(path);       
        }
        catch(Exception e){e.printStackTrace();}
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    System.out.println("Failed loading L&F: ");
                    System.out.println(ex);
                    System.out.println("Loading default Look & Feel Manager!");
                }
                new Dashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
