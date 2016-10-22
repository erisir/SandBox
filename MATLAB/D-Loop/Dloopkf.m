close all;
clear all;
nbp =30;%要处理30步
bowStartNum = 69;
nChordStart = 33;
counter = 0;
hold on;
grid on;
for nBow = bowStartNum:1:bowStartNum+nbp; % nBow 弓的碱基数，从 bowStartNum到bowStartNum+nbp增长，步长为1bp
    radius = 5:0.01:25;
    arcLength = nBow*0.34;%弓长nm
    temp = arcLength./(2.*radius);

    X = 2*radius.*sin(arcLength./(2*radius));%利用弓，弦，力的关系计算一个X（T)，曲线，横坐标是力，纵坐标是弦长
    T = 2*200*temp./(radius.^2)./(2*sin(temp) - 2.*temp.*cos(temp) );
    
    nNum = nChordStart + counter;%nNum 弦的碱基数目，nChordStart 到 nChordStart+nbp,步长为1nt

    T1 = 3:0.01:10; 
    X1 = nNum*0.63.*(coth( T1*1.5/4.2 ) -4.2./(T1*1.5)).*(1+T1/500);;%利用FJC模型计算一个X1（T1)，曲线，横坐标是力，纵坐标是ssDNA长度随力的变化

   
    index= myFindRoot(X,T,X1,T1);%找出曲线 X（T) 与X1（T1)的交点（即方程组的解，交点同时满足FJC模型和弓弦模型）
    plot(T,X,'k-');%画两条线
    plot(T1,X1,'g-'); 
    plot(T(index),X(index),'*');%画出交点来看看
    plot(T(index),X(index),'o');%画出交点来看看
  
    %==========================================================》以上，为了解方程组，得出弦长与力的关系，也即炫，弓分别是nNum,nBow时力多大，弦多长
    %==========================================================》以下，在已知弓，弦长的条件下计算荧光标签相对位置
    
    T_nNum = T(index); %交点，当前力的大小
    length = X(index)/nNum; %交点，当前力下一个nt的长度（FJC) [nm]
    chordLength =X(index); %当前的总弦长 [nm]
    r = radius(index);    %当前的半径 [nm]
    chordcy3 = counter*length; %ounter=cy3拉开多少个nt
    chordcy5= 2*r*sin((counter+9)*0.34/(2*r));%初始距离是9bp   x = 2R sin(s/2r)
   
    if chordcy3-0<0.00000001
         delatcy3cy5 = chordcy5;   %弦长==0
    end
    
    if chordcy3-0>0.00000001
       delatcy3cy5 = sqrt(chordcy5^2 +chordcy3^2 - 2*chordcy5*chordcy3*cos(60*0.34/(2*r)));%余弦定理求cy3cy5距离
    end
    
    
    FRET = 1/(1+(delatcy3cy5/6)^6) ;%FRET
    counter = counter +1;
    out(counter,:) = [nBow,nNum,nBow*0.34,chordLength,r,delatcy3cy5,FRET,T_nNum,length];%记录相关数据，等待输出
 

end
%out %输出
dlmwrite('DLoop_KF_calc_.csv',out);
file_h = fopen('Dloopkf_calc_.csv','wt');
fprintf(file_h, '弓长  ,  弦长  , 弓长  ,  弦长  ,  半径 , 荧光标签距离  ,  FRET  ,  力  ,  1nt单链长度 \n');
fprintf(file_h, 'bp  ,  nt  , nm  ,  nm  ,  nm , nm  ,  au  ,  pN  ,  nm \n');
for line = 1:size(out,1)

fprintf(file_h, '%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f\n',out(line,1),out(line,2),out(line,3),out(line,4),out(line,5),out(line,6),out(line,7),out(line,8),out(line,9));
 
end
fclose(file_h);