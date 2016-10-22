function [ ret ] = findDataFromTo( len,index )
%FINDDATAFROMTO Summary of this function goes here
%   Detailed explanation goes here

y = zeros(len,1);
y(index) = 1;
continueInd = find(smooth(y,500)>0);
 
jumpPoint = find(diff(continueInd)>250);
ret = [min(index) jumpPoint max(index)]; 
end

