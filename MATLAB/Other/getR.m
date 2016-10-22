
%给大侠写的算圆半径
%
%
%
%
%
close all;
nbp =30;
xx = [12.336
12.5302
12.72
12.8931
13.0662
13.2195
13.3776
13.5124
13.6534
13.7787
13.9
14.0015
14.1204
14.2201
14.3088
14.3955
14.49
14.57
14.6544
14.7294
14.79
14.8359
14.8928
14.9672
15.0282
15.081
15.12
15.1506
15.196
15.2338];
xx = xx';
for nBow = 60:1:60+nbp-1;
stepN = nBow - 60+1;
Radiu = 5:0.01:25;
cLen = nBow*0.34;
cJ1 = cLen./(2.*Radiu);

X = 2*Radiu.*sin(cLen./(2*Radiu));%弦长
  delta = 10000;
   index = 0;
 delta1 = 1000000;
 for i = 1:max(size(X))
   temp = (X(i) - xx(stepN))^2;           
   if temp <delta1
       delta1 = temp; 
       index = i;
   end
end 
   
plot(X,Radiu,'k-'); hold on;
plot(X(index),Radiu(index),'*b');
Radiu(index)
grid on;
 

end
