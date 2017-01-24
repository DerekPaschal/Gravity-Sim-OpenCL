
public class Particle 
{
	public float X;
	public float Y;
	public float Z;
	
	public float velX;
	public float velY;
	public float velZ;
	
	public float Mass;
	
	public Particle(float X, float Y, float Z, float velX, float velY, float velZ, float Mass)
	{
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		this.velX = velX;
		this.velY = velY;
		this.velZ = velX;
		this.Mass = Mass;
	}
}
