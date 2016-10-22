function fit(ind)
%�����Բ�߽�
hold on;
A=imread([ int2str(ind) '.tif']);%��ȡ��Բ
imshow(A);%��ʾͼƬ
B=edge(A);%��ȡ��Բ�߽�
C=bwboundaries(B);
D=C{1,1};%�����Բ�߽�����
row=length(D);%��Բ�߽��ĸ���������������ķ��̸���
%���¹��쳬���������ϵ������5��
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