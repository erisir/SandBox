function [ ret ] = getRad( )
%GETRAD Summary of this function goes here
%   Detailed explanation goes here
   n = 10000000;
   s = 1;
   R = normrnd( 4,s ,1,n);
   R( find(R<0)) = [];
   R( find(R>=6.5)) = [];
   R( find(0<R&R<= 1.5)) =1;
   R( find(1.5<R&R<= 2.5)) =2;
   R( find(2.5<R&R<= 3.5)) =3;
   R( find(3.5<R&R<= 4.5)) =4;
   R( find(4.5<R&R<= 5.5)) =5;
   R( find(5.5<R&R<= 6.5)) =6;
   sum = 0;
   count = 1;
   for i=1:max(size(R))
       sum = R(i)+sum;
       if(sum>19)
           sum =0;
       end
       if(sum>10)
           out(count) = sum;
           count = count+1;          
       end

   end
   
   ret = out;
end

