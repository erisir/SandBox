function TJBatchFRET();
close all;

pth=input('Directory [default=C:\\User\\tir data\\yyyy\\New Folder]  ');
	if isempty(pth)
   	pth='C:\User\tir data\yyyy\New Folder';
	end
cd(pth);
disp(pth);
A=dir;
[nf,dumn]=size(A);
dateNow=date;
AnalyzeDir=zeros(nf,1);

Result=zeros(1,2);
for i=1:nf,
   if A(i).isdir == 0
      s=A(i).name
      if s(end-5:end) == 'traces'
         disp(s);
         fid=fopen(s,'r');
			len=fread(fid,1,'int32');
			disp('The len of the time traces is: ')
			disp(len);
			Ntraces=fread(fid,1,'int16')
			disp('The number of traces is:')
			disp(Ntraces);

			raw=fread(fid,Ntraces*len,'int16');
			disp('Done reading data.');
			fclose(fid);
			index=(1:Ntraces*len);
			Data=zeros(Ntraces,len);
			donor=zeros(Ntraces/2,len);
			acceptor=zeros(Ntraces/2,len);
			Data(index)=raw(index);
			for i=1:(Ntraces/2),
 				donor(i,:)=Data(i*2-1,:);
   			acceptor(i,:)=Data(i*2,:);
			end


			time=(0:(len-1))*0.1; %Bin time is 0.1sec.

			elevel=zeros(Ntraces/2,1);
         total=elevel;
        
			for i=1:(Ntraces/2),
   			tempD=sum(donor(i,(3:12)),2)/10;
   			tempA=sum(acceptor(i,(3:12)),2)/10;
            total(i)=(tempA+tempD);
            %total(i)=(tempD)/5.;
            elevel(i)=tempA/(tempA+tempD);
			end
			tempResult=[elevel total];
         Result=[Result' tempResult']';
      end
      
   end
end
figure;
plot(Result(:,1),Result(:,2),'b.');
zoom on;
figure;
subplot(2,1,1);
hist(Result(:,1),80);
zoom on;
title(s);
subplot(2,1,2);
hist(Result(:,2),80);
zoom on;
fcutoff1=input('low cutoff intensity: ','s');
cutoff1=str2num(fcutoff1);
fcutoff2=input('high cutoff intensity: ','s');
cutoff2=str2num(fcutoff2);

index=(Result(:,2)>cutoff1) & (Result(:,2)<cutoff2);
Result=Result(index,:);
subplot(2,1,1);
hist(Result(:,1),80);
title(s);
zoom on;
subplot(2,1,2);
hist(Result(:,2),80);
zoom on;

save(['FRET ' fcutoff1 '_' fcutoff2 '.dat'],'Result','-ascii');
