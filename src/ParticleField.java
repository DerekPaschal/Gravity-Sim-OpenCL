import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import com.dtp_dev.jocl_wrapper.OpenCLDevice;
import com.dtp_dev.jocl_wrapper.OpenCLDevice.DeviceType;
import com.dtp_dev.jocl_wrapper.OpenCLDeviceControl;
import com.dtp_dev.jocl_wrapper.OpenCLKernel;

public class ParticleField 
{
	static OpenCLDeviceControl openCLControl;
	static OpenCLKernel kernel;
	static AtomicInteger ParticleCountChangeLock;
	
	public static void ConstructParticleField(AtomicInteger ParticleCountChangeLock) throws Exception
	{
		ParticleField.ParticleCountChangeLock = ParticleCountChangeLock;
		String programsource = new String(Files.readAllBytes(Paths.get("kernels/Test2.cl")), StandardCharsets.UTF_8);
		openCLControl = new OpenCLDeviceControl();
		OpenCLDevice device = null;
		for(OpenCLDevice tempdevice : openCLControl.GetOpenCLDevices())
		{
			if (device == null)
			{
				device = tempdevice;
				continue;
			}
			if (tempdevice.GetDeviceType() == DeviceType.GPU)
			{
				device = tempdevice;
				continue;
			}
		}
		openCLControl.SetOpenCLDevice(device);
		
		ParticleField.kernel = openCLControl.CreateOpenCLKernel(programsource,"Test");
	}
	
	
	public static void populate(int ParticleCount) throws Exception
	{
		//float density = 1.0f;
		int Spacing = 20;
		int ApproxParticleCount = ParticleCount;
		int StartX = 0/Spacing, StartY = 0/Spacing;
		
		int PartsPerSide = (int) Math.round(Math.sqrt(ApproxParticleCount));
		int iEnd = StartX + PartsPerSide, jEnd = StartY + PartsPerSide;
		
		LinkedList<Particle> partsToAdd = new LinkedList<Particle>();
		
		partsToAdd.add(new Particle(0 - 100, 300, 1.0f, 0, 10.0f, 40,0,0,false,200,200,200));
		//partsToAdd.add(new Particle(0 -1300, 300, 0.5f, 0, 1.0f, 10,0,0,false,200,200,200));
		//partsToAdd.add(new Particle(1300 + 1000, 300, -0.5f, 0, 1.0f, 10,0,0,false,200,200,200));
				
		for (int i = StartX; i < iEnd; i++)
		{
			for (int j = StartY; j < jEnd; j++)
			{
				partsToAdd.add(new Particle(i*Spacing,j*Spacing,0,0,(float)(Math.random() * 0.01) + 0.1f,4,0,0,false,255,255,255));
			}
		}
		ParticleArrays.AddParticles(partsToAdd);
		
	}
	
	public static void run(float TimeStep) throws Exception
	{
		synchronized (ParticleField.ParticleCountChangeLock)
		{
			if (ParticleArrays.n > 0)
			{
				ParticleField.openCLControl.EnqueOpenCLKernels(ParticleField.kernel);
				for (int j = 0; j < ParticleArrays.n; j++)
				{				
					ParticleArrays.velX[j] += ParticleArrays.accX[j] * TimeStep;
					ParticleArrays.velY[j] += ParticleArrays.accY[j] * TimeStep;
					//ParticleArrays.velZ[j] += ParticleArrays.accZ[j] * TimeStep;
					
					ParticleArrays.X[j] += ParticleArrays.velX[j] * TimeStep;
					ParticleArrays.Y[j] += ParticleArrays.velY[j] * TimeStep;
					//ParticleArrays.Z[j] += ParticleArrays.velZ[j] * TimeStep;
				}
			}
		}
	}
}
