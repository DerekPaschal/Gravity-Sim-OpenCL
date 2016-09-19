package com.dtp_dev.particle_view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ParticleView extends JFrame
{
	public ParticlePanel panel;
	private int windowX;
	private int windowY;
	
	public ParticleView(String WindowTitle, AtomicInteger VisualLock)
	{
		this.setTitle(WindowTitle);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//this.setUndecorated(true);
		this.setVisible(true);

		this.windowX = this.getWidth();
		this.windowY = this.getHeight();
		
		this.panel = new ParticlePanel(32,32,32, this.windowX, this.windowY, VisualLock);
		this.getContentPane().add(this.panel);
		this.setIgnoreRepaint(true);
	}
	
	public void PaintParticleView(int n, float[]X, float[]Y, float[]Size, float[]R, float[]G, float[]B)
	{
		panel.n = n;
		panel.X = X;
		panel.Y = Y;
		panel.Size = Size;
		panel.R = R;
		panel.G = G;
		panel.B = B;
		this.repaint(0);
	}
}


@SuppressWarnings("serial")
class ParticlePanel extends JPanel
{
	AtomicInteger VisualLock;
	Color bgColor;
	int windowX;
	int windowY;
	
	float[] X;
	float[] Y;
	float[] Size;
	float[] R;
	float[] G;
	float[] B;
	
	int n;
	
	protected ParticlePanel(int bgR, int bgG, int bgB, int windowX, int windowY, AtomicInteger VisualLock)
	{
		this.VisualLock = VisualLock;
		this.bgColor = new Color(bgR, bgG, bgB);
		this.windowX = Math.max(windowX,0);
		this.windowY = Math.max(windowY,0);
		this.setIgnoreRepaint(true);
	}
	
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(this.bgColor);
		g2.fillRect(0,0,this.windowX,this.windowY);
		
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		
		int draw_diameter = 0;
		float currentSize = 0;
		synchronized (VisualLock)
		{
			for (int i = 0; i < n; i++)
			{
				currentSize = Size[i];
				g2.setColor(new Color((int)R[i],(int)G[i],(int)B[i]));
				draw_diameter = (int)(currentSize *2);
				g2.fillOval((int)(X[i] - currentSize), (int)(Y[i] - currentSize), draw_diameter, draw_diameter);
				
			}
		
			VisualLock.compareAndSet(1, 0);
			VisualLock.notifyAll();
		}
		
	}
}
