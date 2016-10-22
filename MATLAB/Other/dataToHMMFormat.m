function [  ret ] = dataToHMMFormat( filename )
%RR Summary of this function goes here
%   Detailed explanation goes here
    data = dlmread(filename);
     
    data = data - min(data);
    data = 10*data;
    len = max(size(data));
    index = 1:len;
    index = index';
    donor = zeros(len,1);
    donor(:) = 1 - data;
    buf = [index,donor,data];
    dlmwrite([filename '.dat'],buf,' ');
    ret = data;
end
