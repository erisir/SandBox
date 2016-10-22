function TJtirViewSmooth;
%From Ivan's program
close all;
fclose('all');

 
% define time unit
timeunit=0.1;

 
path = 'I:\trace1.smt';
path2 = 'D:\用户目录\下载\smFRET-package-example-data\smFRET package example data\smFRET trace\Long Movie\hel7.traces';
fid=fopen(path,'r');

len=fread(fid,1,'int32')
disp('The len of the time traces is: ')
disp(len);

Ntraces=fread(fid,1,'int32');


disp('The number of traces is:')
disp(Ntraces);
time=fread(fid,1,'float')

fname = 'a'
leakage = 0;
raw=fread(fid,Ntraces*len,'int32');
disp('Done reading data.');
fclose(fid);
return;
% convert into donor and acceptor traces
index=(1:Ntraces*len);
Data=zeros(Ntraces,len);
donor=zeros(Ntraces/2,len);
acceptor=zeros(Ntraces/2,len);
Data(index)=raw(index);
for i=1:(Ntraces/2),
    donor(i,:)=Data(i*2-1,:);
    acceptor(i,:)=Data(i*2,:);
end

time=(0:(len-1))*timeunit;
% calculate, plot and save average traces
dAvg=sum(donor,1)/Ntraces*2;
aAvg=sum(acceptor,1)/Ntraces*2;
%figure;
%hdl1=gcf;
%plot(time,dAvg,'g',time,aAvg,'r');
%title('Average donor and acceptor signal');
%zoom on;
avgOutput=[time' dAvg' aAvg'];
avgFileName=[fname '_avg.dat'];
save(avgFileName,'avgOutput','-ascii');
% calculate E level from the first 10 points and plot histograms of E level and total intensity. Also save the same info.
j=0;
elevel=zeros(Ntraces/2,1);
total=elevel;

output=[fname '_elevel_10p.dat'];
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
save(output,'e_output','-ascii');
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


k=0;
result=zeros(Ntraces/2,3);
dwntime=zeros(Ntraces/2,1);
uptime=zeros(Ntraces/2,1);
bndtime=zeros(Ntraces/2,1);
avgfret=zeros(Ntraces/2,1);
moleculenumber=zeros(Ntraces/2,1);
figure;
hdl3=gcf;
%figure;
%hdl4=gcf;
rk = 0;
lk = 0;
i=0;
%for i=1:(Ntraces/2);

while ((Ntraces/2) - 1) > 0 ,
    i = i + 1 ;
    % Trace window
    figure(hdl3);
    subplot(2,1,1);
    %   subplot(3,1,1);
    %donor(i,:)=filter(ones(1,3),3,donor(i,:));
    %acceptor(i,:)=filter(ones(1,3),3,acceptor(i,:));
    plot(time,donor(i,:),'g',time,acceptor(i,:)-leakage*donor(i,:),'r', time,donor(i,:)+acceptor(i,:)+1000,'k');
    %   title([pth '  Molecule ' num2str(i)]);
    title(['  Molecule ' num2str(i)]);
    temp=axis;

    temp(3)=-200;
    temp(4)=2000;
    grid on;
    axis(temp);
    zoom on;

    %   subplot(3,1,2);
    subplot(2,1,2);
    fretE=(acceptor(i,:)-leakage*donor(i,:))./(acceptor(i,:)+donor(i,:));
    for m=1:len,
        if acceptor(i,m)+donor(i,m)==0
            fretE(m)=-0.5;
        end
    end % This is to avoid undefined fretE interfering with future analysis

    %fretE=fretE*(1+0.12)-0.12;
    %binlen=int32( floor(len/2)-1 );
    binlen=int32( floor(len/2) );
    bintime=zeros( binlen, 1 );
    binE=zeros( binlen, 1 );

    for m=1:binlen,
        bintime(m)=(m-1)*timeunit*2;
        binE(m)=(fretE(2*m-1)+fretE(2*m))/2+0.5;
    end
    %   plot(time,fretE,bintime,binE);
    %   plot(time,fretE,'b+');
    plot(time,fretE,'b');
    temp=axis;
    temp(3)=-0.1;
    temp(4)=1.1;
    axis(temp);
    grid on;
    zoom on;

    %   subplot(3,1,3);
    %   antiC=fretE;
    %   antiC=(donor(i,:)-d_mean).*(acceptor(i,:)-a_mean);
    %   plot(time, antiC);
    %   temp=axis;
    %   temp(3)=-2*10^5;
    %   temp(4)=10^5;
    %   axis(temp);

    % Analysis window
    %  figure(hdl4);
    %   subplot(2,2,1);% FRET histogram
    %   hist(fretE,100);
    %   temp=axis;
    %   temp(1)=-0.3;
    %   temp(2)=1.1;
    %   axis(temp);

    %   subplot(2,2,2);% FRET autocorrelation
    %   temp=[time' fretE'];
    %   autoC=TJautocorrelation(temp,100,0);
    %   plot(autoC(:,1),autoC(:,2));


    figure(hdl3);


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

    if ans=='k'
        [Xb,Yb,buttonb] = ginput;
        bckgd = Yb(1);
        bckga = Yb(2);
        donor(i,:) = donor(i,:) - bckgd;
        acceptor(i,:) = acceptor(i,:)-bckga;
        i = i -1;

    end
    if ans=='s'
        j=j+1;
        mN(j)=i;
        fname1=[fname ' tr' num2str(i) '.dat'];
        output=[time' donor(i,:)' acceptor(i,:)'];
        save(fname1,'output','-ascii') ;
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
