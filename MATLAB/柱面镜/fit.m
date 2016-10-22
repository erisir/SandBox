function fit(ind)
%拟合椭圆边界
hold on;
A=imread([ int2str(ind) '.tif']);%读取椭圆
imshow(A);%显示图片
B=edge(A);%读取椭圆边界
C=bwboundaries(B);
D=C{1,1};%获得椭圆边界坐标
row=length(D);%椭圆边界点的个数，超定方程组的方程个数
%以下构造超定方程组的系数矩阵，5列
for i=1:row
    E(i,1)=D(i,2)^2;
    E(i,2)=D(i,2)*D(i,2);
    E(i,3)=D(i,1)^2;
    E(i,4)=D(i,2);
    E(i,5)=D(i,1);
end
b=-10000*ones(row,1);
x=E\b;
syms xx yy;
h=ezplot(x(1)*xx^2+x(2)*xx*(139-yy)+x(3)*(139-yy)^2+x(4)*xx+x(5)*(139-yy)+10000,[0,195,0,139]);
set(h,'Color','red')
hold off;
end