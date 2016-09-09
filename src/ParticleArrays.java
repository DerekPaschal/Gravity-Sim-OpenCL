import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import com.dtp_dev.jocl_wrapper.OpenCLKernelArg;
import com.dtp_dev.jocl_wrapper.OpenCLKernelArg.ArgType;

public class ParticleArrays 
{
	public enum ArrayName {X,Y,Z,velX,velY,velZ,accX,accY,accZ,Mass,Size,Remove,Elasticity,Repulsivity,Bounces,Red,Green,Blue};
	
	static AtomicInteger ParticleCountChangeLock;
	static AtomicInteger VisualLock;
	
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
	static float[] Size = new float[0];
	
	static boolean[] Remove = new boolean[0];
	static float[] Elasticity = new float[0];
	static float[] Repulsivity = new float[0];
	static boolean[] Bounces = new boolean[0];
	
	static float[] Red = new float[0];
	static float[] Green = new float[0];
	static float[] Blue = new float[0];
	
	static int n = 0;
	
	private ParticleArrays()
	{
		
	}
	
	public static int AddParticles(LinkedList<Particle> parts) throws Exception
	{
		synchronized (ParticleArrays.ParticleCountChangeLock)
		{
			synchronized (ParticleArrays.VisualLock)
			{
				for(Particle part : parts)
				{
					ParticleArrays.AddParticle(part.X,part.Y,part.Z,part.velX,part.velY,part.velZ,part.Mass,part.Size,part.Elasticity,part.Repusivity,part.Bounces,part.Red,part.Green,part.Blue);
				}
				UpdateParticleField();
				return ParticleArrays.n;
			}
		}
	}
	
	private static void AddParticle(float X, float Y, float Z, float velX, float velY, float velZ, float Mass, float Size, float Elasticity, 
										float Repusivity, boolean Bounces, float Red, float Green, float Blue)
	{
		ParticleArrays.n++;
		
		ParticleArrays.X = AddToArray(ParticleArrays.X,X);
		ParticleArrays.Y = AddToArray(ParticleArrays.Y,Y);
		ParticleArrays.Z = AddToArray(ParticleArrays.Z,Z);
		
		ParticleArrays.velX = AddToArray(ParticleArrays.velX, velX);
		ParticleArrays.velY = AddToArray(ParticleArrays.velY, velX);
		ParticleArrays.velZ = AddToArray(ParticleArrays.velZ, velX);
		
		ParticleArrays.accX = AddToArray(ParticleArrays.accX, 0.0f);
		ParticleArrays.accY = AddToArray(ParticleArrays.accY, 0.0f);
		ParticleArrays.accZ = AddToArray(ParticleArrays.accZ, 0.0f);
		
		ParticleArrays.Mass = AddToArray(ParticleArrays.Mass, Mass);
		ParticleArrays.Size = AddToArray(ParticleArrays.Size, Size);
		ParticleArrays.Elasticity = AddToArray(ParticleArrays.Elasticity, Elasticity);
		ParticleArrays.Repulsivity = AddToArray(ParticleArrays.Repulsivity, Repusivity);
		
		ParticleArrays.Bounces = AddToArray(ParticleArrays.Bounces, Bounces);
		
		ParticleArrays.Red = AddToArray(ParticleArrays.Red, Red);
		ParticleArrays.Green = AddToArray(ParticleArrays.Green, Green);
		ParticleArrays.Blue = AddToArray(ParticleArrays.Blue, Blue);
	}
	
	private static void UpdateParticleField() throws Exception
	{
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(0,ArgType.Input,ParticleArrays.X));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(1,ArgType.Input,ParticleArrays.Y));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(2,ArgType.Input,ParticleArrays.Z));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(3,ArgType.Output,ParticleArrays.accX));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(4,ArgType.Output,ParticleArrays.accY));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(5,ArgType.Output,ParticleArrays.accZ));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(6,ArgType.Input,ParticleArrays.Mass));
		ParticleField.kernel.kernelArgs.add(new OpenCLKernelArg(7,ArgType.Input,ParticleArrays.Size));
		ParticleField.kernel.SetKernelWorkSize(ParticleArrays.n);
	}
	
	private static float[] AddToArray(float[]in, float p)
	{
		int size = 0;
		if (in != null)
			size = in.length;
		float[] out = new float[size+1];
		for(int i = 0; i < size; i++)
		{
			out[i] = in[i];
		}	
		out[size] = p;
		return out;
	}
	
	private static boolean[] AddToArray(boolean[]in, boolean p)
	{
		int size = 0;
		if (in != null)
			size = in.length;
		boolean[] out = new boolean[size+1];
		for(int i = 0; i < size; i++)
		{
			out[i] = in[i];
		}	
		out[size] = p;
		return out;
	}
}
