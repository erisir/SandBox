function smFRETAnalyzer
%From Ivan's program
close all;
fclose('all');

% read data
pth=input('Directory [default= F:\\Development\\smFRET\\IDLsmFRET\\traces]  ');
if isempty(pth)
    cd('F:\Development\smFRET\IDLsmFRET\traces');
end
if ~isempty(pth)
	cd(pth);
end

% read data
%fname=input('index # of filename [default=1]  ');
fname=input('enter the filename:','s');
if isempty(fname)
    fname=1;
end
fname=num2str(fname);

['' fname '.traces']
mkdir('C:\user\tir data\data\pic\',fname)
% define time unit
timeunit=0.25;

timeunit=input('Time Unit [default=0.1 sec]  ');
if isempty(timeunit)
    timeunit=0.1;
end

leakage=input('Donor leakage correction [default=0]  ');
if isempty(leakage)
    leakage=0.20;
    %leakage=0;
end


%fid=fopen(['hel' fname '.traces'],'r');
fid=fopen(['' fname '.traces'],'r');
disp(fid);


len=fread(fid,1,'int32');
disp('The len of the time traces is: ')
disp(len);
Ntraces=fread(fid,1,'int16')
disp('The number of traces is:')
disp(Ntraces);
time=(0:(len-1))*timeunit;

raw=fread(fid,Ntraces*len,'int16');
disp('Done reading pma data.');
fclose(fid);

a=textread(['' fname 'position.txt']);
x=a(:,2);
y=a(:,3);


% convert into donor and acceptor traces
index=(1:Ntraces*len);
Data=zeros(Ntraces,len);
donor=zeros(Ntraces/2,len);
acceptor=zeros(Ntraces/2,len);
Data(index)=raw(index);
methods = 'db5';
level = 2;
soft_or_hard = 's';   
thrSettings = 75.241758965267010;

for i=1:(Ntraces/2),
    donor(i,:)=Data(i*2,:);
    acceptor(i,:)=Data(i*2-1,:);
    %donor(i,:)=Data(i*2-1,:);
    %acceptor(i,:)=Data(i*2,:);
    
end

%time=(0:(len-1))*timeunit;
%time_num=1:len
% calculate, plot and save average traces
%dAvg=sum(donor,1)/Ntraces*2;
%aAvg=sum(acceptor,1)/Ntraces*2;
%figure;
%hdl1=gcf;
%plot(time,dAvg,'g',time,aAvg,'r');
%title('Average donor and acceptor signal');
%zoom on;
%avgOutput=[time' dAvg' aAvg'];
%avgFileName=[fname '_avg.dat'];
%save(avgFileName,'avgOutput','-ascii');
% calculate E level from the first 10 points and plot histograms of E level and total intensity. Also save the same info.
j=0;
elevel=zeros(Ntraces/2,1);
total=elevel;

%output=[fname '_elevel_10p.dat'];
for i=1:(Ntraces/2);
    tempD=sum(donor(i,(3:12)),2);
    tempA=sum(acceptor(i,(3:12)),2);
    total(i)=(tempA+tempD)/10;
    elevel(i)=tempA/(tempA+tempD);
end
e_output=[elevel total];
e_mean=mean(elevel);
i_mean=mean(total);
a_mean=e_mean*i_mean;
d_mean=i_mean-a_mean;
%save(output,'e_output','-ascii');

%figure;
%hdl2=gcf;
%subplot(2,2,1);
%hist(elevel,50);
%title('FRET histogram');
%subplot(2,2,2);
%hist(total,30);
%title('Total intensity histogram');
%subplot (2,2,3);
%plot(elevel, total,'bd');
%title('intensity vs. FRET');


figure;
hdl3=gcf;
i=0;

while ((Ntraces/2) - i) > 0 ,
    i=i+1 ;
    % Trace window
    figure(hdl3);
    subplot(2,2,1);
    
    donor_denoised(i,:) = cmddenoise(donor(i,:),methods,level,soft_or_hard,NaN,thrSettings);
    acceptor_denoised(i,:) = cmddenoise(acceptor(i,:),methods,level,soft_or_hard,NaN,thrSettings);
    %donor_denoised(i,:) = cmddenoise(donor(i,:)+200,methods,level)-200;
    %acceptor_denoised(i,:) = cmddenoise(acceptor(i,:)+200,methods,level)-200;
   % donor_denoised(i,:) = cmddenoise(donor(i,:),methods,level);
     %plot(time,donor_denoised(i,:),'r');
    %plot(time,donor,'Color',[0.756862745098039 0.866666666666667 0.776470588235294]);hold on;
    
    plot(time,donor_denoised(i,:),'g',time,acceptor_denoised(i,:),'r');
    %plot(time,donor(i,:),'g',time,acceptor(i,:),'r');
    
    %plot(time,acceptor,'Color',[0.756862745098039 0.866666666666667 0.776470588235294]);hold on;
    grid on;
    title(['  Molecule ' num2str(i) 'donor x=' num2str(x(2*i)) ' y=' num2str(y(2*i)) ' acceptor x=' num2str(x(2*i-1)) ' y=' num2str(y(2*i-1)) ]);
    subplot(2,2,2);
    plot(time,donor_denoised(i,:)+acceptor_denoised(i,:),'k')
    %   title([pth '  Molecule ' num2str(i)]);
    title(['  Total intensity ']);
    temp=axis;

    %temp(3)=-200;
    %temp(4)=1500;
    grid on;
    axis(temp);
    zoom on;

   
    fretE=(acceptor_denoised(i,:)-leakage*donor_denoised(i,:))./(acceptor_denoised(i,:)+donor_denoised(i,:));
    
    
    subplot(2,2,3);
    plot(time,fretE(:),'c');
    temp=axis;
    temp(3)=-0.2;
    temp(4)=1.2;
    axis(temp);
    grid on;
    zoom on;
    title(['  FRET E']);
    subplot(2,2,4);
    plot(x(2*i),y(2*i),'b');
    temp=axis;
    temp(1)=0;
    temp(2)=512;
    temp(3)=0;
    temp(4)=512;
    axis(temp);
    grid on;
    zoom on;
    title(['  Total intensity ']);
    
    %j=j+1;
    %    mN(j)=i;
    %    fname1=[num2str(x(2*i)) num2str(y(2*i)) num2str(x(2*i-1)) num2str(y(2*i-1))];
    %    saveas(hdl3,['C:\user\tir data\data\pic\1\' fname1 ],'fig');
    %    saveas(hdl3,['C:\user\tir data\data\pic\1\' fname1 ],'bmp');
    %    fname2=['C:\user\tir data\data\pic\1\' fname1 '.txt'];
    %    output = [time' fretE'];
        %output=[donor(i,:)' acceptor(i,:)']
    %   save(fname2,'output','-ascii');
       
    
 


    ans=input('press s to save and press p to pass.','s');

    if ans=='f'
        i = i+10;
    end

    if ans=='g'
        nml = input('which molecule?');
        i=nml - 1;
        % prompt={'molecule number?'};
        %def={1};
        %j = 0;
        %ttl = 'go to molecule No';
        %lineNo = 1;
        %answer=inputdlg(prompt,ttl,lineNo,def);
        %celldisp(answer)
        %gg = answer{1};
        %i = gg

    end

    if ans=='b'
        i=i - 2;
    end

    

    
    if ans=='s'
        j=j+1;
        mN(j)=i;
        fname1=[num2str(x(2*i)) '-' num2str(y(2*i)) '-' num2str(x(2*i-1)) '-' num2str(y(2*i-1))];
        saveas(hdl3,['C:\user\tir data\data\pic\' fname '\' fname1 ],'fig');
        saveas(hdl3,['C:\user\tir data\data\pic\' fname '\' fname1 ],'bmp');
        fname2=['C:\user\tir data\data\pic\' fname  '\' fname1 '.txt'];
        output = [time' fretE'];
        %output=[donor(i,:)' acceptor(i,:)']
       save(fname2,'output','-ascii');
    end
    
    if ans=='1'
       j=j+1;
        mN(j)=i;
        fname1=[num2str(i)];
        saveas(hdl3,['C:\user\tir data\data\pic\1\' fname1 ],'fig');
        saveas(hdl3,['C:\user\tir data\data\pic\1\' fname1 ],'bmp');
        fname2=['C:\user\tir data\data\pic\1\' fname1 '.txt'];
        output = [time_num' fretE'];
        %output=[time_num' donor(i,:)' time_num' acceptor(i,:)']
       save(fname2,'output','-ascii');
        
    end
    
    if ans=='2'
        j=j+1;
        mN(j)=i;
        fname1=[num2str(i)];
        saveas(hdl3,['C:\user\tir data\data\pic\2\' fname1 ],'fig');
        saveas(hdl3,['C:\user\tir data\data\pic\2\' fname1 ],'bmp');
        fname2=['C:\user\tir data\data\pic\2\' fname1 '.dat'];
        output=[time' donor(i,:)' acceptor(i,:)' fretE'];
        save(fname2,'output');%,'-ascii') ;
    end

    if ans=='3'
        j=j+1;
        mN(j)=i;
        fname1=[num2str(i)];
        saveas(hdl3,['C:\user\tir data\data\pic\3\' fname1 ],'fig');
         saveas(hdl3,['C:\user\tir data\data\pic\3\' fname1 ],'bmp');
          fname2=['C:\user\tir data\data\pic\3\' fname1 '.dat'];
        output=[time' donor(i,:)' acceptor(i,:)' fretE'];
        save(fname2,'output','-ascii') ;
    end
    
%    if ans=='c'
%        fname1=['fretall.dat'];
%        [Xc,Yc,buttonc] = ginput;
%        fp = round(Xc(1)/timeunit);
%        lp = round(Xc(2)/timeunit);
%        for k = fp:lp
%            if (acceptor(i,k)+ donor(i,k)) <= 10;
%                fretE(k) = 0;
%            end
%            if fretE(k) > 1;
%                fretE(k) = 1;
%            end
%            if fretE(k) < 0;
%                fretE(k) = 0;
%            end
%        end
%        tmptime = [tmptime time(fp:lp)];
%        tmpfr = [tmpfr fretE(fp:lp)];
%        tmpdn = [tmpdn donor(i,(fp:lp))];
%        tmpacc = [tmpacc acceptor(i,(fp:lp))];
%        output = [ tmptime' tmpfr' tmpdn' tmpacc'];
%        save(fname1,'output','-ascii') ;
%   end
%    if ans=='a'
%        reacted=reacted+1;
%    end
%    if ans=='z'
%        not_reacted=not_reacted+1;
%    end

%	if ans=='y'
%        lk = lk+1;
%        [X,Y, button]=ginput
%        npnts = length(button);
%        for k=2:2:npnts,
%            if button(k) == 3,
%                lkb = lkb+1;
%               tblnk(lkb) = X(k) - X(k-1);
%            end
%            if button(k) == 1,
%                lknb = lknb+1;
%                tnblnk(lknb) = X(k) - X(k-1);
%            end
%        end%
%		result=[tblnk(1:lkb)]';
%		save([fname 'blnktime.dat'],'result','-ascii');
%		result=[tnblnk(1:lknb)]';
%		save([fname 'bindtime.dat'],'result','-ascii');
%    end
%    if ans=='r'
%        [X,Y]=ginput(2);
%        rk=rk+1;
%        r_starttime(rk)=X(1);
%        r_endtime(rk)=X(2);
%        r_difference(rk) = 1.* (r_endtime(rk)-r_starttime(rk));
%        recoverytime(rk)=i;
%        result=[r_difference(rk)];
%        save([fname 'transitiontime.dat'],'result','-ascii');
%    end

    if ans=='x',
        %cd('y:\');
        fclose all;
        clear all;
        break;
    end
    
   

end

%g=hist(avg_fretE,1000)
      %temp=axis;
      %temp(1)=-0.1;
      %temp(2)=1.1;
      %temp(3)=0;
      %temp(4)=30;
      %axis(temp);
      %title(['avgFRET histogram ']);
      %saveas(g,['C:\user\tir data\data\pic\avg_FRET_' fname ],'fig');
      %saveas(g,['C:\user\tir data\data\pic\avg_FRET_' fname ],'bmp');
%fname3=['C:\user\tir data\data\pic\avg_FRET' fname '.dat'];
%output=[avg_fretE'];
 %       save(fname3,'output','-ascii');
        

end

