#include <stdio.h>
#include <stdlib.h>
#include <string.h>
	

int getIndex(char arr[][100], char str[], int M);


float forward(int t, int i, int N, int M, float a[][100], float b[][100], float pi[], char OBsym[][100], char OBseq[][100]);


int main(int argc, char *argv[])
{
	FILE *fp_in1, *fp_in2;
	
	fp_in1 = fopen(argv[1],"r");
	fp_in2 = fopen(argv[2],"r");

	int i,j;
	float x;	
	char line[100];
	char s[100];
	
	//read the hmm file
	fgets(line,100,fp_in1);
	int N,M,T;
	sscanf(line, "%d %d %d", &N, &M, &T);
	
	char state[N][100];
        for(i=0;  i<N; i++){
                fscanf(fp_in1, "%s", s);
		strcpy(state[i],s);
        }
	
	fgets(line,100,fp_in1);

	char OBsym[M][100];
        for(i=0;  i<M; i++){
                fscanf(fp_in1, "%s", s);
                strcpy(OBsym[i],s);
        }
	
	fgets(line,100,fp_in1);
	fgets(line,100,fp_in1);
	
	float a[N][N];
	for(i=0; i<N; i++){
		for(j=0; j<N; j++){
			fscanf(fp_in1, "%f", &x);
			a[i][j]= x;
		}
	}
	
	fgets(line,100,fp_in1);
	fgets(line,100,fp_in1);
	
	float b[N][M];
        for(i=0; i<N; i++){
                for(j=0; j<M; j++){
                        fscanf(fp_in1, "%f", &x);
                        b[i][j]= x;
                }
        }

        fgets(line,100,fp_in1);
        fgets(line,100,fp_in1);
	
	float pi[N];
	for(i=0;  i<N; i++){
		fscanf(fp_in1, "%f", &x);
		pi[i] = x;
	}
	
	
	//forward(a);
	//read the obs file and proccess the forward algorrithm
	fgets(line,100,fp_in2);
	int OBnum;
	sscanf(line, "%d", &OBnum);
		
	for(i=0; i<OBnum; i++){
		fgets(line,100,fp_in2);
		int OBlength;
		sscanf(line,"%d", &OBlength);
		
		int k;
		char OBseq[OBlength][100];
		for(k=0;  k<OBlength; k++){
			fscanf(fp_in2, "%s", s);
			strcpy(OBseq[k],s);
		}
		
		
		float test;
        	test = forward(2,2,N,M,a,b,pi,OBsym,OBseq);
        	printf("%f\n",test);
		

		/*
		float P;
		int l;
		for(l=0; l<N-1; l++){
			P += forward(OBlength-1,l,N,M,a,b,pi,OBsym,OBseq);
		}
		printf("%f\n", P);
		printf("\n");	
		*/

		fgets(line,100,fp_in2);			
	}

		
	fclose(fp_in1);
	fclose(fp_in2);
	return 0;
}

float forward(int t, int i, int N, int M, float a[][100], float b[][100], float pi[], char OBsym[][100], char OBseq[][100])
{
        if(t==0){
                int OBinb = getIndex(OBsym, OBseq[t], M);
                float f = pi[i]*b[i][OBinb];
		if (f<=0.0){
			return 0.0;
		}else{
			return f;
		}
		
        }else{
                float sum;
                int j;
                for(j=0; j<N-1; j++){
                        sum += forward(t-1,j,N,M,a,b,pi,OBsym,OBseq)*a[i][j];
                }
                int OBinb = getIndex(OBsym, OBseq[t], M);
                return sum*b[i][OBinb];
        }
        return 0.0;

}


int getIndex(char arr[][100], char str[], int M)
{
        int i;
        for(i=0; i<M; i++){
                int com = strcmp(str,arr[i]);
                if (com == 0){
                        return i;
                }
        }
	return 0;
}

