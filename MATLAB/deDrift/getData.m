function [ ret ] = getData(filename,split,skipRow,invert)
% R��Cָ�����������ļ��е����Ͻ�λ�á�R��C��ֵ��0��ʼ��R = 0, C = 0ָ�������ļ��еĵ�һ��ֵ��λ���ļ������Ͻǡ�
%return value :data of interest (nm)(integer)
[data1,data2,data3,data4,data5,data6,data7] = textread(filename, '%d %f %f %f %f %f %f %*[^\n]','delimiter',split,'headerlines', skipRow);
data  = data7;
%dlmread(filename,split,skipRow,dataCloumn);
data = round(data*1000);%  um to nm
data =  data - min(data);% trim bottom 
if(invert ==1)%invert data make sure baseline is in the bottom
    data   = -1*data ;
end
ret = data;%important
end

