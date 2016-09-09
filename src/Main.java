import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.dtp_dev.particle_view.ParticleView;

public class Main {

	public Main() throws Exception
	{
		AtomicInteger VisualLock = new AtomicInteger(); 
		AtomicInteger ParticleCountChangeLock = new AtomicInteger();
		
		ParticleArrays.ParticleCountChangeLock = ParticleCountChangeLock;
		ParticleArrays.VisualLock = VisualLock;
		
		ParticleView partView = new ParticleView("Particles",VisualLock);
		
		ParticleField.ConstructParticleField(ParticleCountChangeLock);
		
		float TimeStep = 1.0f;
		int TimesToRun = (int) Math.round(1.0f / TimeStep);
		
		ParticleField.populate();
		
		long new_frame_time = 0, wait_time = 0;
		
		long benchStartTime = System.nanoTime();
		
		int secsToSim = 1000;
		for (int p = 0; p < secsToSim; p++)
		{
			//Begin timer
			//new_frame_time = System.nanoTime();
			
			VisualLock.compareAndSet(0, 1);
			
			partView.PaintParticleView(ParticleArrays.n,ParticleArrays.X, ParticleArrays.Y, ParticleArrays.Size, ParticleArrays.Red, ParticleArrays.Green,ParticleArrays.Blue);
			
			for (int i = 0; i < TimesToRun; i++)
			{
				ParticleField.run(TimeStep);
			}
			
			synchronized(VisualLock)
			{
				while(VisualLock.get() == 1)
				{
					VisualLock.wait();
				}
			}
			
			//End Timer
			//wait_time =(long)(17000000 - (System.nanoTime() - new_frame_time));//17000000
			
			//This block is not measured by wait_time
			/*if (wait_time > 0)
			{
				try{
				TimeUnit.NANOSECONDS.sleep(wait_time);
				} catch (InterruptedException e){}
			}*/
		}
		
		long benchEndTime = System.nanoTime();
		
		long duration = (long) ((benchEndTime - benchStartTime)/(1e9f));
		
		System.out.println("Duration: " + duration + " S");
		System.out.println("FPS: " + secsToSim/duration);
		System.out.println("TimeStep: "+ TimeStep);
		System.out.println("Particles: " + ParticleArrays.n);
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception 
	{
		new Main();
	}

}
