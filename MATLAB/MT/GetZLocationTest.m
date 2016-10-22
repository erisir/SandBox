function [ ret ] = GetZLocationTest( testIndex1,fixFlag )
%GETZLOCATIONTEST Summary of this function goes here
%   Detailed explanation goes here  
    close all;
 
    position = dlmread('Position.txt');
    posProfiles0 = dlmread('calProfiles.txt');
    a1 = posProfiles0(1,:);
    a2 = posProfiles0(2,:);
    a3 = posProfiles0(3,:);
    n1 = norm(a1);
    n2 = norm(a2);
    n3 = norm(a3);
    
    d1 = dot(a1,a3);
    d2 = dot(a1,a2);
    acos(d1/(n1*n3))
    acos(d2/(n1*n2))
    k1 = n3*n1
    k2 = norm(a1)*norm(a3)
    acos(d1/(k2))    
    PhraseShift(a1,a2)
    PhraseShift(a1,a3)
    
    return;
    trimStart = 20;
    trimEnd = size(posProfiles0,2)-1;
    testIndex = testIndex1;
    subplot(4,1,1);
    for i = 1:size(posProfiles0,1)
        posProfiles(i,:) = posProfiles0(i,trimStart:trimEnd);
        plot(posProfiles(i,:));hold on;
    end
 
    hold off;
 
      subplot(4,1,2);
        for i = 1:size(posProfiles,1)
         h(i) =   posProfiles(i,:) * posProfiles(testIndex,:)';
        end
    
       plot(position,h');hold on;
       plot(position,h','*');
       grid on;
       hold off;
	    subplot(4,1,3);
	 temp=0;
	 for i=1:fixFlag:(size(posProfiles,1)-fixFlag)
	 
	   for j=1:fixFlag
	    T1=PhraseShift(posProfiles(i,:),posProfiles(i+j,:))+temp;
	    plot(position(i+j),T1,'*r');hold on;
		if j==fixFlag
		temp = T1;
		end
	   end
	   
	 end
	 hold off;
     [a index]=max(h);
     a1 = PhraseShift(posProfiles(index,:),posProfiles(index-1,:));
     a2 = PhraseShift(posProfiles(index,:),posProfiles(index+1,:));
     a3 = PhraseShift(posProfiles(index-1,:),posProfiles(index+1,:));
     cc = [a1,a2,a3]
     dd = [position(index-1),position(index),position(index+1)]
     subplot(4,1,4);
     for i = 1:size(posProfiles,1)   
       plot(position(i),PhraseShift(posProfiles(testIndex,:),posProfiles(i,:)),'*r');hold on;
     end
      
    ret = 0;
end

