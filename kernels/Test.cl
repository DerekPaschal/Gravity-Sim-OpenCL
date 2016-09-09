 kernel void Kernel(global const float *X, global const float *Y, global const float *Z, 
					global float *Xa, global float *Ya, global float *Za,
					global const float *M, global const float *S)
{
	
	int gid = get_global_id(0);
	float dist = 0.0f;
	//int distInvalid = 1;
	float VectorG = 0.0f;
	int n = get_global_size(0);
	float A[3] = {0.0f,0.0f,0.0f};
	
	//barrier(CLK_LOCAL_MEM_FENCE);

	for (int i = 0; i < n; i++)
	{				
		dist = sqrt(((X[gid] - X[i])*(X[gid] - X[i]))  + ((Y[gid] - Y[i])*(Y[gid] - Y[i])) + ((Z[gid] - Z[i])*(Z[gid] - Z[i])));
		//distInvalid = (dist < E[0]); //Find if dist is invalid (< Epsilon)
		//dist = dist + (E[0] * distInvalid); //If dist is invalid, add Epsilon to prevent NaN
		
		if (dist >= 4.0f)
		{
			VectorG = (M[i] / (dist * dist * dist));// * (!distInvalid); //Calculate VectorG, if dist is invalid multiply by zero
			//Multiply VectorG by distance componant wise and accumulate
			A[0] += VectorG * (X[i] - X[gid]);
			A[1] += VectorG * (Y[i] - Y[gid]);
			A[2] += VectorG * (Z[i] - Z[gid]);
		}
	}
	
	//barrier(CLK_LOCAL_MEM_FENCE);
	
	Xa[gid] = A[0];
	Ya[gid] = A[1];
	Za[gid] = A[2];
};