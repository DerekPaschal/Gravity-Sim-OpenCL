import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

	public Main() throws Exception
	{
		AtomicInteger ParticleCountChangeLock = new AtomicInteger();
		
		ParticleArrays.ParticleCountChangeLock = ParticleCountChangeLock;
		
		ParticleView partView = new ParticleView("Particles");
		
		ParticleField.ConstructParticleField(ParticleCountChangeLock);
		
		//Simulation Parameters
		float TimeStep = 1.0f;
		float SecsPerFrame = 1.0f * TimeStep;
		float SecsToSim = 1000.0f * SecsPerFrame;
		int ParticleCount = (int) Math.pow(70, 2);
		
		
		ParticleField.populate(ParticleCount);
		long new_frame_time = 0, wait_time = 0;
		
		long benchStartTime = System.nanoTime();

		for (float p = 0.0f; p < SecsToSim; p += SecsPerFrame)
		{
			//Begin timer
			new_frame_time = System.nanoTime();
			
			partView.PaintParticleView(ParticleArrays.n, ParticleArrays.X, ParticleArrays.Y);
			
			for (float i = 0.0f; i < SecsPerFrame; i += TimeStep)
			{
				ParticleField.run(TimeStep);
			}
			
			
			//End Timer
			wait_time =(long)(16000000 - (System.nanoTime() - new_frame_time));//17000000
			
			//This block is not measured by wait_time
			if (wait_time > 0)
			{
				try{
				TimeUnit.NANOSECONDS.sleep(wait_time);
				} catch (InterruptedException e){}
			}
		}
		
		long benchEndTime = System.nanoTime();
		
		float duration = (float)((int)((benchEndTime - benchStartTime)/(1e8f))/10.0);
		
		System.out.println(ParticleField.openCLControl.getCurrentDevice().getDeviceName());
		
		System.out.println("Duration of Simulation: \t\t" + duration + " S");

		System.out.println("Simulated Seconds per Second: \t\t" + (Math.round(SecsToSim*10.0/duration)/10.0));
		System.out.println("Frames Per Second: \t\t\t" + (Math.round((SecsToSim/SecsPerFrame)*10.0/duration)/10.0) + " FPS");
		System.out.println("TimeSteps Computed Per Second: \t\t" + Math.round((SecsToSim*10.0/TimeStep)/duration)/10.0);
		
		System.out.println("Particles: \t\t\t\t" + ParticleArrays.n);
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception 
	{
		new Main();
	}

}
