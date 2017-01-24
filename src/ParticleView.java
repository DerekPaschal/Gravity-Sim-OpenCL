import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ParticleView extends JFrame
{
	public ParticlePanel panel;
	private int windowX;
	private int windowY;
	
	public ParticleView(String WindowTitle)
	{
		this.setTitle(WindowTitle);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//this.setUndecorated(true);
		this.setVisible(true);

		this.windowX = this.getWidth();
		this.windowY = this.getHeight();
		
		this.panel = new ParticlePanel(32,32,32, this.windowX, this.windowY);
		this.getContentPane().add(this.panel);
		this.setIgnoreRepaint(true);
	}
	
	public void PaintParticleView(int n, float[]X, float[]Y)
	{
		if(panel.isRendering.compareAndSet(false, true))
		{
			return;
		}
		
		panel.n = n;
		panel.X = X;
		panel.Y = Y;

		this.repaint(0);
		
	}
}


@SuppressWarnings("serial")
class ParticlePanel extends JPanel
{
	Color bgColor;
	int windowX;
	int windowY;
	
	float[] X;
	float[] Y;
	
	int n;
	AtomicBoolean isRendering = new AtomicBoolean(false);
	
	protected ParticlePanel(int bgR, int bgG, int bgB, int windowX, int windowY)
	{
		this.bgColor = new Color(bgR, bgG, bgB);
		this.windowX = Math.max(windowX,0);
		this.windowY = Math.max(windowY,0);
		this.setIgnoreRepaint(true);
	}
	
	public void paintComponent(Graphics g) 
	{
		synchronized(ParticleArrays.ParticleCountChangeLock)
		{
			g.setColor(this.bgColor);
			g.fillRect(0,0,this.windowX,this.windowY);
			
			g.setColor(Color.WHITE);
			
			for (int i = 0; i < n; i++)
			{
				g.drawRect((int)X[i], (int)Y[i], 0, 0);
			}
			
			this.isRendering.set(false);
		}
	}
}
