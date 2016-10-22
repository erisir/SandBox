close all;
clear all;
nbp =30;%Ҫ����30��
bowStartNum = 69;
nChordStart = 33;
counter = 0;
hold on;
grid on;
for nBow = bowStartNum:1:bowStartNum+nbp; % nBow ���ļ�������� bowStartNum��bowStartNum+nbp����������Ϊ1bp
    radius = 5:0.01:25;
    arcLength = nBow*0.34;%����nm
    temp = arcLength./(2.*radius);

    X = 2*radius.*sin(arcLength./(2*radius));%���ù����ң����Ĺ�ϵ����һ��X��T)�����ߣ����������������������ҳ�
    T = 2*200*temp./(radius.^2)./(2*sin(temp) - 2.*temp.*cos(temp) );
    
    nNum = nChordStart + counter;%nNum �ҵļ����Ŀ��nChordStart �� nChordStart+nbp,����Ϊ1nt

    T1 = 3:0.01:10; 
    X1 = nNum*0.63.*(coth( T1*1.5/4.2 ) -4.2./(T1*1.5)).*(1+T1/500);;%����FJCģ�ͼ���һ��X1��T1)�����ߣ���������������������ssDNA���������ı仯

   
    index= myFindRoot(X,T,X1,T1);%�ҳ����� X��T) ��X1��T1)�Ľ��㣨��������Ľ⣬����ͬʱ����FJCģ�ͺ͹���ģ�ͣ�
    plot(T,X,'k-');%��������
    plot(T1,X1,'g-'); 
    plot(T(index),X(index),'*');%��������������
    plot(T(index),X(index),'o');%��������������
  
    %==========================================================�����ϣ�Ϊ�˽ⷽ���飬�ó��ҳ������Ĺ�ϵ��Ҳ���ţ����ֱ���nNum,nBowʱ������Ҷ೤
    %==========================================================�����£�����֪�����ҳ��������¼���ӫ���ǩ���λ��
    
    T_nNum = T(index); %���㣬��ǰ���Ĵ�С
    length = X(index)/nNum; %���㣬��ǰ����һ��nt�ĳ��ȣ�FJC) [nm]
    chordLength =X(index); %��ǰ�����ҳ� [nm]
    r = radius(index);    %��ǰ�İ뾶 [nm]
    chordcy3 = counter*length; %ounter=cy3�������ٸ�nt
    chordcy5= 2*r*sin((counter+9)*0.34/(2*r));%��ʼ������9bp   x = 2R sin(s/2r)
   
    if chordcy3-0<0.00000001
         delatcy3cy5 = chordcy5;   %�ҳ�==0
    end
    
    if chordcy3-0>0.00000001
       delatcy3cy5 = sqrt(chordcy5^2 +chordcy3^2 - 2*chordcy5*chordcy3*cos(60*0.34/(2*r)));%���Ҷ�����cy3cy5����
    end
    
    
    FRET = 1/(1+(delatcy3cy5/6)^6) ;%FRET
    counter = counter +1;
    out(counter,:) = [nBow,nNum,nBow*0.34,chordLength,r,delatcy3cy5,FRET,T_nNum,length];%��¼������ݣ��ȴ����
 

end
%out %���
dlmwrite('DLoop_KF_calc_.csv',out);
file_h = fopen('Dloopkf_calc_.csv','wt');
fprintf(file_h, '����  ,  �ҳ�  , ����  ,  �ҳ�  ,  �뾶 , ӫ���ǩ����  ,  FRET  ,  ��  ,  1nt�������� \n');
fprintf(file_h, 'bp  ,  nt  , nm  ,  nm  ,  nm , nm  ,  au  ,  pN  ,  nm \n');
for line = 1:size(out,1)

fprintf(file_h, '%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f ,%.5f\n',out(line,1),out(line,2),out(line,3),out(line,4),out(line,5),out(line,6),out(line,7),out(line,8),out(line,9));
 
end
fclose(file_h);