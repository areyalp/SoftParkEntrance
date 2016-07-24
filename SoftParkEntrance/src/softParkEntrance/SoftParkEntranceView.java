package softParkEntrance;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.UUID;

import com.github.sarxos.webcam.*;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class SoftParkEntranceView extends JFrame implements UncaughtExceptionHandler {
	
	private Webcam webcam;
	private WebcamPanel camPanel;
	private WebcamPicker camPicker;
	private WebCamListener lForWebcam;
	
	public static void main(String[] args){
		new SoftParkEntranceView(0);
	}

	public SoftParkEntranceView(int stationId) {
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(800,600));
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		
		int x = (dim.width / 2) - (this.getWidth() / 2);
		int y = (dim.height / 2) - (this.getHeight() / 2);
		
		this.setLocation(x, y);
		
		this.setTitle("Web Cam Testing");
		
		this.setLayout(new BorderLayout(20,20));
		
		//gs = new GrabberShow();
		//this.add(gs,BorderLayout.CENTER);
		
		lForWebcam = new WebCamListener();
		
		Webcam.addDiscoveryListener(lForWebcam);
		
		PickerListener lForPicker = new PickerListener();
		
		camPicker = new WebcamPicker();
		camPicker.addItemListener(lForPicker);
		
		this.add(camPicker, BorderLayout.NORTH);
		
		webcam = Webcam.getDefault();
		webcam.setViewSize(new Dimension(640,480));
		
		WebcamPanel camPanel = new WebcamPanel(webcam);
		
		camPanel.setFPSDisplayed(true);
		camPanel.setDisplayDebugInfo(true);
		camPanel.setImageSizeDisplayed(true);
		camPanel.setMirrored(true);
		this.add(camPanel);
		
		JButton button = new JButton("Click");
		ButtonListener lForButton = new ButtonListener();
		button.setActionCommand("click");
		button.addActionListener(lForButton);
		this.add(button, BorderLayout.EAST);
		
		this.setVisible(true);
	}
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.err.println(String.format("Exception in thread %s", t.getName()));
		e.printStackTrace();
	}
	
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {
			if(ev.getActionCommand().equals("click")) {
				//gs.run();
				BufferedImage img = webcam.getImage();
				
				try {
					ImageIO.write(img, "jpg", new File(UUID.randomUUID().toString() + ".jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private class WebCamListener implements WebcamListener, WebcamDiscoveryListener {

		@Override
		public void webcamClosed(WebcamEvent arg0) {
			
		}

		@Override
		public void webcamDisposed(WebcamEvent arg0) {
			
		}

		@Override
		public void webcamImageObtained(WebcamEvent arg0) {
			
		}

		@Override
		public void webcamOpen(WebcamEvent arg0) {
			
		}

		@SuppressWarnings("unchecked")
		@Override
		public void webcamFound(WebcamDiscoveryEvent ev) {
			if(camPicker != null){
				camPicker.addItem(ev.getWebcam());
			}
		}

		@Override
		public void webcamGone(WebcamDiscoveryEvent ev) {
			if(camPicker != null){
				camPicker.removeItem(ev.getWebcam());
			}
		}
		
	}

	private class PickerListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent ev) {
			if(ev.getItem() != webcam) {
				if(webcam != null) {
					camPanel.stop();
					
					remove(camPanel);
					
					webcam.removeWebcamListener(lForWebcam);
					webcam.close();
					
					webcam = (Webcam) ev.getItem();
					webcam.setViewSize(WebcamResolution.VGA.getSize());
					webcam.addWebcamListener(lForWebcam);
					
					System.out.println("Selected " + webcam.getName());
					
					camPanel = new WebcamPanel(webcam, false);
					camPanel.setFPSDisplayed(true);
					
					add(camPanel, BorderLayout.CENTER);
					pack();
					
					Thread t = new Thread() {
						
						@Override
						public void run(){
							camPanel.start();
						}
					};
					
					t.setName("example-stoper");
					t.setDaemon(true);
					//t.setUncaughtExceptionHandler(this);
					t.start();
				}
			}
		}
		
	}


	
}
