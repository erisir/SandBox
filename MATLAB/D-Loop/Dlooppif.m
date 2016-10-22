close all;
clear all;
nbp =30;
bowStartNum = 63;
nChordStart = 33;
counter = 0;
hold on;
grid on;
for nBow = bowStartNum:1:bowStartNum+nbp; 
    radius = 5:0.01:25;
    arcLength = nBow*0.34;
    temp = arcLength./(2.*radius);

    X = 2*radius.*sin(arcLength./(2*radius));
    T = 2*200*temp./(radius.^2)./(2*sin(temp) - 2.*temp.*cos(temp) );

    nNum = nChordStart + counter;

    T1 = 3:0.01:10; 
    X1 = nNum*0.63.*(coth( T1*1.5/4.2 ) -4.2./(T1*1.5)).*(1+T1/500);

   
    index= myFindRoot(X,T,X1,T1);
    plot(T(index),X(index),'*');
    plot(T(index),X(index),'o');
  
    T_nNum = T(index);
    length = X(index)/nNum;
    chordLength =X(index); %弦长 nm
    r = radius(index);
    chordcy3 = counter*length+2;%ounter=cy3拉开的距离
    chordcy5= 2*r*sin((counter+3)*0.34/(2*r));%初始距离是3bp
   
    if chordcy3-0<0.00000001
         delatcy3cy5 = chordcy5;
    end
    if chordcy3-0>0.00000001
       delatcy3cy5 = sqrt(chordcy5^2 +chordcy3^2 - 2*chordcy5*chordcy3*cos(60*0.34/(2*r)));
    end
    FRET = 1/(1+(delatcy3cy5/6)^6) ;
    counter = counter +1;
    out(counter,:) = [nBow,nNum,nBow*0.34,chordLength,r,delatcy3cy5,FRET,T_nNum,length];
    plot(T,X,'k-');
    plot(T1,X1,'g-'); 

end
%out %输出
file_h = fopen('DLoop_PIF_calc_.csv','wt');
fprintf(file_h, '弓长  ,  弦长  , 弓长  ,  弦长  ,  半径 , 荧光标签距离  ,  FRET  ,  力  ,  1nt单链长度 \n');
fprintf(file_h, 'bp  ,  nt  , nm  ,  nm  ,  nm , nm  ,  au  ,  pN  ,  nm \n');
for line = 1:size(out,1)

fprintf(file_h, '%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f\n',out(line,1),out(line,2),out(line,3),out(line,4),out(line,5),out(line,6),out(line,7),out(line,8),out(line,9));
 
end
fclose(file_h);
figure;
plot(out(:,6),out(:,7),'*')
set(gca,'XTick',3:0.5:8);
axis([3 8 0 1]);
set(gca,'YTick',0:0.1:1);
grid on;