function [ minIndex,maxIndex ] = findMaxMin(data)
minIndex=find(diff(sign(diff(data)))>0)+1;
maxIndex=find(diff(sign(diff(data)))<0)+1;
end

