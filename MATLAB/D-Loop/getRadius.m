
%������д����Բ�뾶
%
%
%
%
%
close all;
nbp =30;
xx = xx';%xian chang
for nBow = 60:1:60+nbp-1;
stepN = nBow - 60+1;
Radiu = 5:0.01:25;
cLen = nBow*0.34;
cJ1 = cLen./(2.*Radiu);

X = 2*Radiu.*sin(cLen./(2*Radiu));%�ҳ�
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
