import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import com.dtp_dev.jocl_wrapper.OpenCLKernelArg;

public class ParticleArrays 
{
	public enum ArrayName {X,Y,Z,velX,velY,velZ,accX,accY,accZ,Mass};
	
	static AtomicInteger ParticleCountChangeLock;
	static float[] TimeStep = new float[]{1.0f};
	
	static float[] X = new float[0];
	static float[] Y = new float[0];
	static float[] Z = new float[0];
	
	static float[] velX = new float[0];
	static float[] velY = new float[0];
	static float[] velZ = new float[0];
	
	static float[] accX = new float[0];
	static float[] accY = new float[0];
	static float[] accZ = new float[0];
	
	static float[] Mass = new float[0];
	
	static int n = 0;
	
	private ParticleArrays()
	{
		
	}
	
	public static int AddParticles(Particle[] parts) throws Exception
	{
		synchronized (ParticleArrays.ParticleCountChangeLock)
		{
			int oldLength = ParticleArrays.n;
			
			ParticleArrays.n += parts.length;
			
			ParticleArrays.X = changeArrayLength(ParticleArrays.X,n);
			ParticleArrays.Y = changeArrayLength(ParticleArrays.Y,n);
			ParticleArrays.Z = changeArrayLength(ParticleArrays.Z,n);
			
			ParticleArrays.velX = changeArrayLength(ParticleArrays.velX, n);
			ParticleArrays.velY = changeArrayLength(ParticleArrays.velY, n);
			ParticleArrays.velZ = changeArrayLength(ParticleArrays.velZ, n);
			
			ParticleArrays.accX = changeArrayLength(ParticleArrays.accX, n);
			ParticleArrays.accY = changeArrayLength(ParticleArrays.accY, n);
			ParticleArrays.accZ = changeArrayLength(ParticleArrays.accZ, n);
			
			ParticleArrays.Mass = changeArrayLength(ParticleArrays.Mass, n);
			
			for(int i = 0; i < parts.length; i++)
			{
				ParticleArrays.X[i+oldLength] = parts[i].X;
				ParticleArrays.Y[i+oldLength] = parts[i].Y;
				ParticleArrays.Z[i+oldLength] = parts[i].Z;
				
				ParticleArrays.velX[i+oldLength] = parts[i].velX;
				ParticleArrays.velY[i+oldLength] = parts[i].velY;
				ParticleArrays.velZ[i+oldLength] = parts[i].velZ;
				
				ParticleArrays.accX[i+oldLength] = 0.0f;
				ParticleArrays.accY[i+oldLength] = 0.0f;
				ParticleArrays.accZ[i+oldLength] = 0.0f;
				
				ParticleArrays.Mass[i+oldLength] = parts[i].Mass;
			}
			UpdateParticleField();
			return ParticleArrays.n;
		}
	}
	
	private static void UpdateParticleField() throws Exception
	{
		/*ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(0,ArgType.Input,ParticleArrays.X));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(1,ArgType.Input,ParticleArrays.Y));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(2,ArgType.Input,ParticleArrays.Z));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(3,ArgType.Output,ParticleArrays.accX));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(4,ArgType.Output,ParticleArrays.accY));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(5,ArgType.Output,ParticleArrays.accZ));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(6,ArgType.Input,ParticleArrays.Mass));
		ParticleField.kernel.SetKernelWorkSize(ParticleArrays.n);*/
		ParticleField.openCLControl.setData(new Object[] {X,Y,Z,accX,accY,accZ,Mass}, new Object[] {accX, accY, accZ} , ParticleArrays.n);;
		//ParticleField.openCLControl.setData(new Object[] {X,Y,Z,accX,accY,accZ,Mass}, ParticleArrays.n);;
		
	}
	
	private static float[] changeArrayLength(float[]in, int m)
	{
		float[] out = new float[m];
		
		for (int i = 0; i < m && i < in.length; i++)
		{
			out[i] = in[i];
		}
		
		return out;
	}
}
