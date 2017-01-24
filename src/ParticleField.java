import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import com.dtp_dev.jocl_wrapper.OpenCLControl;
import com.dtp_dev.jocl_wrapper.OpenCLDevice;
import com.dtp_dev.jocl_wrapper.OpenCLDevice.DeviceType;
import com.dtp_dev.jocl_wrapper.OpenCLKernel;

public class ParticleField 
{
	static OpenCLControl openCLControl;
	static OpenCLKernel kernel;
	static AtomicInteger ParticleCountChangeLock;
	
	public static void ConstructParticleField(AtomicInteger ParticleCountChangeLock) throws Exception
	{
		ParticleField.ParticleCountChangeLock = ParticleCountChangeLock;
		String programsource = new String(Files.readAllBytes(Paths.get("kernels/Test2.cl")), StandardCharsets.UTF_8);
		openCLControl = new OpenCLControl("Test",programsource,new Object[] {ParticleArrays.X,ParticleArrays.Y,ParticleArrays.Z,ParticleArrays.accX,ParticleArrays.accY,ParticleArrays.accZ,ParticleArrays.Mass}, new Object[]{ParticleArrays.accX, ParticleArrays.accY, ParticleArrays.accZ},ParticleArrays.n);
		OpenCLDevice device = null;
		for(OpenCLDevice tempdevice : openCLControl.getOpenCLDevices())
		{
			if (device == null)
			{
				device = tempdevice;
				continue;
			}
			if (tempdevice != null && tempdevice.getDeviceType() == DeviceType.GPU)
			{
				device = tempdevice;
				continue;
			}
		}
		openCLControl.setOpenCLDevice(device);
		
		//ParticleField.kernel = openCLControl.CreateOpenCLKernel(programsource,"Test");
	}
	
	
	public static void populate(int ParticleCount) throws Exception
	{
		int Spacing = 2;
		int ApproxParticleCount = ParticleCount;
		int StartX = 500/Spacing, StartY = 200/Spacing;
		
		int PartsPerSide = (int) Math.round(Math.sqrt(ApproxParticleCount));
		int iEnd = StartX + PartsPerSide, jEnd = StartY + PartsPerSide;
		
		Particle[] partsToAdd = new Particle[(iEnd-StartX)*(jEnd-StartY)];
			
		for (int i = StartX; i < iEnd; i++)
		{
			for (int j = StartY; j < jEnd; j++)
			{
				partsToAdd[((i-StartX)*(jEnd - StartY))+(j-StartY)] = new Particle(i*Spacing,j*Spacing,0,0,0,0,(float)(Math.random() * 0.0005) + 0.005f);
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
				ParticleField.openCLControl.run();
				for (int j = 0; j < ParticleArrays.n; j++)
				{				
					ParticleArrays.velX[j] += ParticleArrays.accX[j] * TimeStep;
					ParticleArrays.velY[j] += ParticleArrays.accY[j] * TimeStep;
					ParticleArrays.velZ[j] += ParticleArrays.accZ[j] * TimeStep;
					
					ParticleArrays.X[j] += ParticleArrays.velX[j] * TimeStep;
					ParticleArrays.Y[j] += ParticleArrays.velY[j] * TimeStep;
					ParticleArrays.Z[j] += ParticleArrays.velZ[j] * TimeStep;
				}
			}
		}
	}
}
