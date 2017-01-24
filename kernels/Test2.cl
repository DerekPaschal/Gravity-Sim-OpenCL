 kernel void Test(global const float *X, global const float *Y, global const float *Z,
					global float *Xa, global float *Ya, global float *Za,
					global const float *M)
{
	
	int gid = get_global_id(0);
	int n = get_global_size(0);
	
	float3 Acc = {0.0f,0.0f,0.0f};
	float3 Pos = {X[gid], Y[gid], Z[gid]};
	
	float3 Pos2;
	float3 dist;
	float distMag;
	
	for (int i = 0; i < n; i++)
	{		
		Pos2 = (float3){X[i], Y[i], Z[i]};
	
		dist = Pos2 - Pos;
	
		distMag = (sqrt((dist.x*dist.x)  + (dist.y*dist.y) + (dist.z*dist.z))) +0.00001;
		
		//Gravity
	
		Acc += M[i] * (dist / distMag) * (((distMag>2.236f)*(1.0f/(distMag*distMag))));// + ((distMag<=2.236f)*(-(0.2f*(distMag*distMag))+0.4f)));
		
		/*
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
		*/
	}
	Xa[gid] = Acc.x;
	Ya[gid] = Acc.y;
	Za[gid] = Acc.z;
};