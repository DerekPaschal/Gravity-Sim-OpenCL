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
		
		//Simulation Parameters
		float TimeStep = 1.0f;
		float SecsPerFrame = 1.0f * TimeStep;
		float SecsToSim = 2000.0f * SecsPerFrame;
		int ParticleCount = (int) Math.pow(60, 2);
		
		
		ParticleField.populate(ParticleCount);
		//long new_frame_time = 0, wait_time = 0;
		
		long benchStartTime = System.nanoTime();

		for (float p = 0.0f; p < SecsToSim; p += SecsPerFrame)
		{
			//Begin timer
			//new_frame_time = System.nanoTime();
			
			VisualLock.compareAndSet(0, 1);
			partView.PaintParticleView(ParticleArrays.n,ParticleArrays.X, ParticleArrays.Y, ParticleArrays.Size, ParticleArrays.Red, ParticleArrays.Green,ParticleArrays.Blue);
			
			for (float i = SecsPerFrame; i >= 0.0f; i =- TimeStep)
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
		
		float duration = (float)((int)((benchEndTime - benchStartTime)/(1e8f))/10.0);
		
		System.out.println("Duration of Simulation: \t\t" + duration + " S");
		//System.out.println("Simulated Seconds: \t\t" + SecsToSim + " S");
		System.out.println("Simulated Seconds per Second: \t" + (Math.round(SecsToSim*10.0/duration)/10.0));
		System.out.println("Frames Per Second: \t\t" + (Math.round((SecsToSim/SecsPerFrame)*10.0/duration)/10.0) + " FPS");
		//System.out.println("TimeStep: \t\t\t"+ ParticleArrays.TimeStep[0]);
		System.out.println("Particles: \t\t\t" + ParticleArrays.n);
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception 
	{
		new Main();
	}

}
