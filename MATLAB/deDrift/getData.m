function [ ret ] = getData(filename,split,skipRow,invert)
% R和C指定了数据在文件中的左上角位置。R和C的值从0开始。R = 0, C = 0指定了了文件中的第一个值，位于文件的左上角。
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

