close all;
 
nbp = 30;
for nBow = 60:1:60+nbp;
 
Radiu = 5:0.01:25;
cLen = nBow*0.34;
cJ1 = cLen./(2.*Radiu);

X = 2*Radiu.*sin(cLen./(2*Radiu));
T = 2*200*cJ1./(Radiu.*Radiu)./(2*sin(cJ1) - 2.*cJ1.*cos(cJ1) );

 


nNum = nBow-30;
T1 = 3:0.01:10; 
X1 = nNum*0.63.*(coth( T1*1.5/4.2 ) -4.2./(T1*1.5)).*(1+T1/500);

   delta = 10000;
   index = 0;
   for i = 1:max(size(T1))
      
       delta1 = 10000;
       for j = 1:max(size(T))
           temp = (T1(i) - T(j))^2 + (X1(i) - X(j))^2;
           if temp <delta1
               delta1 = temp;
           end
       end
        if delta1 <delta
               delta = delta1;
               index = i;
        end
   end
   plot(T1(index),X1(index),'*');
   TT = T1(index);
   length = X1(index)/nNum;
   distance = length*(nNum-30)*2;
   En = 1/(1+(distance/6)^6)
   
plot(T,X,'k-'); hold on;
grid on;
plot(T1,X1,'g-'); 

end
