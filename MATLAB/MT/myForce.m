function [ Force ] = myForce(stdx)
  L = 15*10^(-6);%opt(0);  
  T = 300;%opt(1);
  P = 50*10^(-9);
  Kb = 1.3806505*10^(-23);

  stdx = stdx*80*10^(-9);
  stdx2 = stdx^2;
  theta = 1/(Kb*T);
  A = theta*stdx2/L;  
  B = 4*P*theta-4*A;
  
  a = (A^2)*B;
  b = A - 2*A*B;
  c = B -2*A;
  detal =b^2 - 4*a*c;  
  if detal < 0 
      Force = -1;
  else
      Force = 10^(12)*( -b - sqrt(detal))/(2*a);
  end
 
end

