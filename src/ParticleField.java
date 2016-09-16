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
		int ApproxParticleCount = ParticleCount;
		int StartX = 10/10, StartY = 10/10;
		
		int PartsPerSide = (int) Math.round(Math.sqrt(ApproxParticleCount));
		int iEnd = StartX + PartsPerSide, jEnd = StartY + PartsPerSide;
		
		LinkedList<Particle> partsToAdd = new LinkedList<Particle>();
		
		for (int i = StartX; i < iEnd; i++)
		{
			for (int j = StartY; j < jEnd; j++)
			{
				partsToAdd.add(new Particle(i*10,j*10,0,0,(float) 0.05,4,0,0,false,255,255,255));
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
