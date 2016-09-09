
public class Particle 
{
	public float X;
	public float Y;
	public float Z;
	
	public float velX;
	public float velY;
	public float velZ;
	
	public float Mass;
	public float Size;
	
	public boolean Remove;
	public float Elasticity;
	public float Repusivity;
	public boolean Bounces;
	
	public float Red;
	public float Green;
	public float Blue;
	
	public Particle(float X, float Y, float Z, float velX, float velY, float velZ, float Mass, float Size, float Elasticity, 
					float Repulsivity, boolean Bounces, float Red, float Green, float Blue)
	{
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		this.velX = velX;
		this.velY = velY;
		this.velZ = velX;
		this.Mass = Mass;
		this.Size = Size;
		this.Elasticity = Elasticity;
		this.Repusivity = Repulsivity;
		this.Bounces = Bounces;
		this.Red = Red;
		this.Green = Green;
		this.Blue = Blue;
	}
}
