 kernel void Test(global const float *X, global const float *Y,
					global const float *Xv, global const float *Yv,
					global float *Xa, global float *Ya,
					global const float *M, global const float *S)
{
	
	int gid = get_global_id(0);
	int n = get_global_size(0);
	
	float size = S[gid];
	float mass = M[gid];
	
	float2 A = {0.0f,0.0f};
	float2 P = {X[gid], Y[gid]};
	
	float2 PN;
	float2 dist;
	float distMag;
	float VectorG;
	
	float2 UnitVector;
	float VelAlongNorm;
	float restitution;
	float overlap;
	
	for (int i = 0; i < n; i++)
	{		
		PN = (float2){X[i], Y[i]};
	
		dist = PN - P;
	
		distMag = sqrt((dist.x*dist.x)  + (dist.y*dist.y));
		
		//Gravity
		if (distMag >= (size + S[i]))
		{
			VectorG = M[i] / (distMag * distMag * distMag);
			A += VectorG * dist;
		}
		
		//Repulsion Collision
		if (distMag >= 1.0f && distMag <= (size + S[i]))
		{
			UnitVector = dist / distMag;
			
			VelAlongNorm = ((Xv[i] - Xv[gid]) * UnitVector.x) + ((Yv[i] - Yv[gid]) * UnitVector.y);  
			
			restitution = (1.0f * (VelAlongNorm <= 0)) + (0.7f * (VelAlongNorm > 0));
			
			overlap = (distMag - (size + S[i]));
			
			//VectorG = (restitution * 1 * overlap) / (distMag * distMag * distMag);
			
			//A += VectorG * UnitVector;
			
			//overlap = (3.14159f *  * (size + S[i] - (distMag * distMag))* (size + S[i] - (distMag * distMag)) * ((distMag * distMag) + (2 * distMag * S[i]) - (3 * S[i] * S[i]) + (2 * distMag * size) + (6 * S[i] * size) - (3 * size * size)))/ (12 * distMag);
			//overlap = (1/(distMag*distMag)) * (-distMag + S[i] - size) * (-distMag - S[i] + size) * (- distMag + S[i] + size) * (distMag + S[i] + size);
			
			A += (UnitVector * restitution * 0.05 * overlap) / mass;
		}
		
		//Collision friction
		if (distMag < (size + S[i]) && distMag >= 1.0f)
		{
			
		}
	}
	Xa[gid] = A.x;
	Ya[gid] = A.y;
};