function [ ret ] = findMax( data,interval )
%DEDRIFT1 Summary of this function goes here
%   Detailed explanation goes here
x = 1:size(data,1);
[pks,locs]  = findpeaks(data,'minpeakdistance',interval);
ret = interp1(locs,pks,x,'linear');
ret = ret';
end

