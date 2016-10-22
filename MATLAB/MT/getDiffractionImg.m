function [images ] = getDiffractionImg(index_, flag)
w = 300;
f = 0.7;
N =1;
lmda = 1000e-9;

Z = 1+index_/10;
r =Z*1e-2;

K = linspace(-0.1,0.1,N);
lmda1 = lmda* ( 1 + K) ;
xm = w* lmda* f;
xs = linspace(-xm,xm,w) ;
z0 = zeros(w) ;

[x,y]= meshgrid( xs) ;
for i = 1:N
    s = 2 * pi * r * sqrt (x.^2 + y.^2)./( lmda1(i));
    z = 4* (besselj(1,s)./( s + eps)).^2;
    z0 = z0 + z;
end

z1 = z0 /N;
images= z1* 255;
if flag == 1
    imshow( z1*255) ;
    title( 'img')
    xlabel( 'x')
    ylabel( 'y')
end

end

