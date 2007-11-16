package uk.org.squirm3.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.DraggingPoint;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.IAtomListener;
import uk.org.squirm3.listener.ILevelListener;

import com.oreilly.java.awt.RoundGradientPaint;


/**  
Copyright 2007 Tim J. Hutton, Ralph Hartley, Bertrand Dechoux

This file is part of Organic Builder.

Organic Builder is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Organic Builder is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Organic Builder; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

public class AtomsView implements IView, IAtomListener, ILevelListener {
	private ApplicationEngine applicationEngine;
	
	private DraggingPoint draggingPoint;
	private Atom[] latestAtomsCopy;
	
	private int simulationWidth;
	private int simulationHeight;
	
	private BufferedImage bimg;
	private boolean needRepaint = true;
	private byte scale;
	
	private JCheckBox auto;
	private JSlider scaleSlider;
	
	private JPanel imagePanel;
	private JPanel controlsPanel;
	
	private final JComponent collisionsPanel;
	private JScrollPane scrollPane;
	
	// yellow, grey, blue, purple, red, green
	private static final Color atomsColors[] = {new Color(0xbdcf00), new Color(0x5f5f5f), new Color(0x0773db),  new Color(0xee10ac), new Color(0xef160f), new Color(0x00df06)};
	private static final BufferedImage[] atomsImages = new BufferedImage[atomsColors.length];
	

	public AtomsView(ApplicationEngine applicationEngine) {
		createAtomsImages();
		needRepaint = true;
		scale = 100;
		collisionsPanel = createCollisionsPanel();
		this.applicationEngine = applicationEngine;
		simulationSizeHasChanged();
		atomsHaveChanged();
		draggingPointHasChanged();
		applicationEngine.getEngineDispatcher().addAtomListener(this);
		applicationEngine.getEngineDispatcher().addLevelListener(this);
	}
	
	private static void createAtomsImages() {
		//size
		final float R = Atom.getAtomSize()-2;
		final int w = (int)(2*R);
		final int h = (int)(2*R);
		for(int i = 0; i<atomsColors.length; i++) {
			// creation of the image
			atomsImages[i] = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
			// creation of the graphic
			Graphics2D g2 = atomsImages[i].createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			// creation of the colors
			Color baseColor = atomsColors[i];
			final int colorOffset = 220;
			int red = (baseColor.getRed()<255-colorOffset)?baseColor.getRed()+colorOffset:255;
			int green = (baseColor.getGreen()<255-colorOffset)?baseColor.getGreen()+colorOffset:255;
			int blue = (baseColor.getBlue()<255-colorOffset)?baseColor.getBlue()+colorOffset:255;
			Color lightColor = new Color(red, green, blue);
			// drawing the image
			RoundGradientPaint gradient = new RoundGradientPaint(w/3, h/3, lightColor, new Point2D.Double(w/2, h/2), baseColor);
			g2.setPaint(gradient);
			g2.fillOval(0, 0, w, h);
		}
	}
	
	public JComponent getCollisionsPanel() {
		return collisionsPanel;
	}
	
	private JComponent createCollisionsPanel() {
		imagePanel = new ImagePanel();
		scrollPane = new JScrollPane(imagePanel);
		scrollPane.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent arg0) {
				imageSizeHasChanged();
			}
			public void componentHidden(ComponentEvent arg0) {}
			public void componentMoved(ComponentEvent arg0) {}
			public void componentShown(ComponentEvent arg0) {}
		});
		
		controlsPanel = new JPanel();
		controlsPanel.add(new JLabel(Application.localize(new String[] {"interface","scale"})));
		auto = new JCheckBox(Application.localize(new String[] {"interface","scale","auto"}));
		auto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				scaleSlider.setEnabled(!auto.isSelected());
			    imageSizeHasChanged();
			}
		});
		auto.setSelected(true);
		controlsPanel.add(auto);
		scaleSlider = new JSlider(30, 100, scale);
		scaleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    if (!scaleSlider.getValueIsAdjusting()) {
			        scale = (byte) scaleSlider.getValue();
			        imageSizeHasChanged();
			    }
			}
		});
		scaleSlider.setToolTipText(Application.localize(new String[] {"interface","scale","manual"}));
		scaleSlider.setEnabled(false);
		controlsPanel.add(scaleSlider);

		return scrollPane;
	}
	
	public JPanel getControlsPanel() {
		return controlsPanel;
	}
	
	public void atomsHaveChanged() {
		Collection c = applicationEngine.getAtoms();
		Iterator it = c.iterator();
		latestAtomsCopy = new Atom[c.size()];
		int i = 0;
		while(it.hasNext()) {
			latestAtomsCopy[i] = (Atom)it.next();
			i++;
		}
		imageHasChanged();
	}

	public void draggingPointHasChanged() {
		draggingPoint = applicationEngine.getCurrentDraggingPoint();
		imageHasChanged();
	}

	public void simulationSizeHasChanged() {
		simulationHeight = (int)applicationEngine.getCurrentLevel().getConfiguration().getHeight();
		simulationWidth = (int)applicationEngine.getCurrentLevel().getConfiguration().getWidth();
		imageSizeHasChanged();
	}
	
	private void imageSizeHasChanged() {
		if(auto!=null) {
			if(auto.isSelected()) {
				if(imagePanel!=null) {
					Dimension d = scrollPane.getSize();
					int pseudoScale = (int) (Math.min((double)((double)d.height*0.99/(double)simulationHeight),(double)((double)d.width*0.99/(double)simulationWidth))*100);
					scale = (pseudoScale>=100)? 100 : (byte)pseudoScale;
				} 
			} else {
				scale = (byte) scaleSlider.getValue();
			}
		}
		float zoom = ((float)scale)/100;
		imagePanel.setPreferredSize(new Dimension((int)(simulationWidth*zoom), (int)(simulationHeight*zoom)));
		SwingUtilities.updateComponentTreeUI(scrollPane);
		imageHasChanged();
	}
	
	private void imageHasChanged() {
		needRepaint = true;
		if(imagePanel== null || !imagePanel.isDisplayable()) return;
		imagePanel.repaint();
	}
	
	private void updateImage() {
		if(!needRepaint) return;
		needRepaint = false;
		int R = (int)Atom.getAtomSize();
		 //to avoid the array to be changed (multithreading issue maybe later)
		Atom[] atoms = latestAtomsCopy;
		
		// get the dimensions
		int w = simulationWidth;
		int h = simulationHeight;
		
		// do we have a correct bimg ?
		if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h)
			bimg = (BufferedImage) collisionsPanel.createImage(w,h);
		if(bimg==null) return;// collisionsPanel is not displayable
		
		// create graphics	
		Graphics2D g2 = bimg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, w, h);
			
		// draw the atoms themselves
		g2.setStroke(new BasicStroke(4));
		g2.setFont(new Font("Arial", Font.BOLD, R));
		g2.setPaint(Color.black);
		if(atoms!=null) {
			int offset_x= R;
			int offset_y= R;
			int text_offset_y= (int)(R*8.0/22.0);
			for (int i = 0; i < atoms.length; i++){
				if(!atoms[i].isKiller()) {
					// draw the normal colour atom image and label it
					g2.drawImage(atomsImages[atoms[i].getType()], 
							(int)atoms[i].getPhysicalPoint().getPositionX()-offset_x,
							(int)atoms[i].getPhysicalPoint().getPositionY()-offset_y,
							R*2, R*2, collisionsPanel);
					String label = atoms[i].toString();
					int width = g2.getFontMetrics().stringWidth(label);
					g2.drawString(label, (int)atoms[i].getPhysicalPoint().getPositionX()-width/2,
							(int)atoms[i].getPhysicalPoint().getPositionY()+text_offset_y); 
				} else {
					// draw a special spiky image and no label
					g2.drawImage(Resource.getSpikyImage(),(int)atoms[i].getPhysicalPoint().getPositionX()-offset_x, 
							(int)atoms[i].getPhysicalPoint().getPositionY()-offset_y, R*2, R*2, collisionsPanel);
				}
			}
		}
		
		// draw the bonds 
		g2.setPaint(new Color(0,0,0,50));
		if(atoms!=null) {
			for (int i = 0; i < atoms.length; i++){
				Iterator it = atoms[i].getBonds().iterator();
				while(it.hasNext()) {
					Atom other = (Atom)it.next();
					float x1 = atoms[i].getPhysicalPoint().getPositionX();
					float y1 = atoms[i].getPhysicalPoint().getPositionY();
					float dx = other.getPhysicalPoint().getPositionX() - x1;
					float dy = other.getPhysicalPoint().getPositionY() - y1;
					float d = (float)Math.sqrt(dx*dx+dy*dy);
					float x_cut = dx*R*0.8f/d;
					float y_cut = dy*R*0.8f/d;
					g2.drawLine((int)(x1+x_cut),(int)(y1+y_cut),(int)(x1+dx-x_cut),(int)(y1+dy-y_cut));
				}
			}
		}
		
		// draw the dragging line if currently dragging
		if(draggingPoint!=null) {
			g2.setStroke(new BasicStroke(5));
			g2.setPaint(new Color(0,0,0,100));
			g2.drawLine((int)draggingPoint.getX(),(int)draggingPoint.getY(),
					(int)(atoms[draggingPoint.getWhichBeingDragging()].getPhysicalPoint().getPositionX()),
					(int)(atoms[draggingPoint.getWhichBeingDragging()].getPhysicalPoint().getPositionY()));
			g2.setStroke(new BasicStroke(4)); // else the stroke would have been changed
				// when outlining the collider area
		}
		
		// draw the dragging point used
		if(applicationEngine.getLastUsedDraggingPoint()!=null) {
			DraggingPoint lastUsedDraggingPoint = applicationEngine.getLastUsedDraggingPoint();
			g2.setStroke(new BasicStroke(1));
			g2.setPaint(new Color(200,0,0,100));
			g2.drawLine((int)lastUsedDraggingPoint.getX(),(int)lastUsedDraggingPoint.getY(),
					(int)(atoms[lastUsedDraggingPoint.getWhichBeingDragging()].getPhysicalPoint().getPositionX()),
					(int)(atoms[lastUsedDraggingPoint.getWhichBeingDragging()].getPhysicalPoint().getPositionY()));
			g2.setStroke(new BasicStroke(4)); // else the stroke would have been changed
				// when outlining the collider area
		}
		
		// outline the collider area
		g2.setPaint(new Color(100,100,200));
		g2.drawRoundRect(2,1,simulationWidth-4,simulationHeight-4,9,9);
		g2.dispose();
	}

	class ImagePanel extends JPanel implements Scrollable{

		public ImagePanel() {
			addMouseListener(
					new MouseListener() {
						public void mouseClicked(MouseEvent event) {}
						public void mouseEntered(MouseEvent event) {}
						public void mouseExited(MouseEvent event) {}
						public void mousePressed(MouseEvent event) {
							// who did we click on?
							Point p = event.getPoint();
							int R = (int) Atom.getAtomSize();
							float zoom = ((float)scale)/100;
							Point2D.Float p1 = new Point2D.Float();
							Point2D.Float p2 = new Point2D.Float(p.x/zoom,p.y/zoom);
							for(int i=0;i<latestAtomsCopy.length;i++){
								p1.x = latestAtomsCopy[i].getPhysicalPoint().getPositionX();
								p1.y = latestAtomsCopy[i].getPhysicalPoint().getPositionY();
								if(p2.distanceSq(p1)<R*R) {
									applicationEngine.setDraggingPoint(
											new DraggingPoint((long)p2.x,
													(long)p2.y, i));
									break;
								}
							}
						}
						public void mouseReleased(MouseEvent event) {
							applicationEngine.setDraggingPoint(null);
						}
					});
			addMouseMotionListener(
					new MouseMotionListener() {
						public void mouseDragged(MouseEvent event) {
							if(draggingPoint!=null) {
								float zoom = ((float)scale)/100;
								applicationEngine.setDraggingPoint(
										new DraggingPoint((long)(event.getPoint().x/zoom),
												(long)(event.getPoint().y/zoom), 
												draggingPoint.getWhichBeingDragging()));
							}
						}
						public void mouseMoved(MouseEvent event) {
							if(draggingPoint!=null) {
								float zoom = ((float)scale)/100;
								applicationEngine.setDraggingPoint(
										new DraggingPoint((long)(event.getPoint().x/zoom),
												(long)(event.getPoint().y/zoom), 
												draggingPoint.getWhichBeingDragging()));
							}
						}
					});
		}
		
		public void paint(Graphics g) {
			super.paint(g);
			updateImage();
			float zoom = ((float)scale)/100;
			g.drawImage(bimg, 0, 0, (int)(bimg.getWidth()*zoom), (int)(bimg.getHeight()*zoom), this);
		}

		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
			return 1;	//TODO
		}

		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

		public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
			return 1; //TODO
		}
	
	}

	public void levelHasChanged() {
		simulationSizeHasChanged();
	}

	public void configurationHasChanged() {
		simulationSizeHasChanged();
	}

}

