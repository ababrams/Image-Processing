import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * GUI for Image Browsing 
 * 
 * @author Alicia Abrams, Corwin Lipkin
 *
 */

public class ImageGUI extends JFrame implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private JButton submitDirectory;
	private JPanel topPanel;
	private JLabel topLabel;
	private JTextField directoryTextField;
	private JLabel label;
	private JTextField sizeTextFieldX;
	private JTextField sizeTextFieldY;
	private JButton previous;
	private JButton next;
	private JPanel bottomPanel;
	private JPanel imagePanel;
	private JLabel imageLabel;
	JFrame frame;
	String directoryName;
	String sizeX;
	String sizeY;
	AcquireImage image;
	int yAxis;
	int xAxis;

	public ImageGUI() {
		this.setLayout(new BorderLayout());
		topPanel = new JPanel();
		topLabel = new JLabel(" Directory ");
		directoryTextField = new JTextField(20);
		imagePanel = new JPanel();
		imageLabel = new JLabel();
		label = new JLabel(" Image Size ");
		sizeTextFieldX = new JTextField(4);
		sizeTextFieldY = new JTextField(4);
		submitDirectory = new JButton("Submit");
		bottomPanel = new JPanel();
		previous = new JButton("Previous");
		next = new JButton("  Next  ");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 800);
		topPanel.add(topLabel);
		topPanel.add(directoryTextField);
		topPanel.add(label);
		topPanel.add(sizeTextFieldX);
		topPanel.add(sizeTextFieldY);
		topPanel.add(submitDirectory);

		imagePanel.add(imageLabel);
		bottomPanel.add(previous);
		bottomPanel.add(next);

		this.addKeyListener(this);
		this.setFocusable(true);
		submitDirectory.addKeyListener(this);
		submitDirectory.setFocusable(true);
		imagePanel.addKeyListener(this);
		imagePanel.setFocusable(true);
		submitDirectory.addActionListener(this);
		previous.addActionListener(this);
		next.addActionListener(this);
		this.getContentPane().add(BorderLayout.NORTH, topPanel);
		this.getContentPane().add(BorderLayout.CENTER, imagePanel);
		this.getContentPane().add(BorderLayout.SOUTH, bottomPanel);
		this.setVisible(true);

	}

	public ImageGUI(String dir, int row, int col) {
		this();
		directoryName = dir;
		xAxis = row;
		yAxis = col;
		image = new AcquireImage(directoryName, yAxis, xAxis);
		setImage();
	}

	/**
	 * Displays current image in GUI
	 */
	public void setImage() {
		BufferedImage bufImage = image.getImage();
		
		// if statement for image if over 600 width or 800 height
		// IDE should make you surround in try catch
		// BufferedImage atImage = affineTransform(bufImage);
		
		imageLabel.setIcon(new ImageIcon(bufImage));
		System.out.println(image.getFile() + " " + bufImage.getHeight() + "x" + bufImage.getWidth() );
	}
	
	/**
	 * Returns image at reduced size
	 * @param image
	 * @return 
	 * @throws IOException
	 */
	public BufferedImage affineTransform(BufferedImage image) throws IOException {
		// create byte array from buffered image
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

		// create matrix empty matrix size of image
		Mat convert = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
			    
		// add image to matrix
		convert.put(0, 0, pixels);
		
		// empty matrix for transformed image
		Mat transform = new Mat();
		
		// coordinate points for original image
	    Point p1 = new Point( 0,0 );
	    Point p2 = new Point( convert.cols() - 1, 0 );
	    Point p3 = new Point( 0, convert.rows() - 1 );
	    
	    // coordinate points for transformed image, currently set to reduce 50%
	    Point p4 = new Point(0,0);
	    Point p5 = new Point( convert.cols()*0.5,0);
	    Point p6 = new Point( 0, convert.rows()*0.5 );
	    
	    // setting the two sets of coordinates
	    MatOfPoint2f ma1 = new MatOfPoint2f(p1,p2,p3);
	    MatOfPoint2f ma2 = new MatOfPoint2f(p4,p5,p6);
	    
	    // transform
	    Mat tranformMatrix = Imgproc.getAffineTransform(ma1,ma2);
	    	    
	    // transforming image
	    Imgproc.warpAffine(convert, transform, tranformMatrix, convert.size());
		
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".jpeg", transform, matOfByte);
		
		byte[] byteArray = matOfByte.toArray();
				
		InputStream in = new ByteArrayInputStream(byteArray);
		image = ImageIO.read(in);
		
		return image;
	}

	/**
	 * Sets the directoryName, sizeX, and sizeY in AcquireImage when the
	 * submitDirectory button is pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(submitDirectory)) {
			if (getInput()) {
				image = new AcquireImage(directoryName, yAxis, xAxis);
				if (image.getImages().size() > 0) {
					setImage();
				}
			} else {
				JOptionPane.showMessageDialog(this, "Invalid Input");
			}
		} else if (e.getSource().equals(previous)) {
			image.previousImage();
			setImage();
		} else if (e.getSource().equals(next)) {
			image.nextImage();
			setImage();
		}
	}

	/**
	 *
	 * @return true if values inputed to sizeX and sizeY can be converted to
	 *         integers
	 */
	public boolean getInput() {
		directoryName = directoryTextField.getText();
		sizeX = sizeTextFieldX.getText();
		sizeY = sizeTextFieldY.getText();
		if (sizeX == null || sizeY == null) {
			return false;
		}
		try {
			xAxis = Integer.parseInt(sizeX);
			yAxis = Integer.parseInt(sizeY);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == 'n' || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode()== KeyEvent.VK_RIGHT) {
			image.nextImage();
			setImage();
		}

		if (e.getKeyChar() == 'p' || e.getKeyCode()== KeyEvent.VK_LEFT) {
			image.previousImage();
			setImage();
		}

		if (e.getKeyChar() == 'q' || e.getKeyCode()== KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent a) {}
}
