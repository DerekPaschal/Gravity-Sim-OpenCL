 kernel void Test(global const float *X, global const float *Y,
					global const float *Xv, global const float *Yv,
					global float *Xa, global float *Ya,
					global const float *M, global const float *S)
{
	
	int gid = get_global_id(0);
	int n = get_global_size(0);
	
	float size = S[gid];
	float mass = M[gid];
	
	float2 A;
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
		
		if (i == gid)
		{
			continue;
		}
		
		if (distMag >= (size + S[i])/2.0f)
		{
			VectorG = M[i] / (distMag * distMag * distMag);
			A += VectorG * dist;
		}
		
		if (distMag < size + S[i])
		{
			UnitVector = dist / distMag;
			
			VelAlongNorm = ((Xv[i] - Xv[gid]) * UnitVector.x) + ((Yv[i] - Yv[gid]) * UnitVector.y);  
			
			restitution = (1.0f * (VelAlongNorm <= 0)) + (0.1f * (VelAlongNorm > 0));
			
			overlap = distMag - (size + S[i]);
			
			A += (UnitVector * restitution * 0.05f * overlap) / mass;
		}
	}
	
	Xa[gid] = A.x;
	Ya[gid] = A.y;
};