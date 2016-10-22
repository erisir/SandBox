close all;
cy5backWard = 5;
cy3backWard = 0;
nDNA = 12;
xmin = 0;
xmax = 60;%nDNA*1.5;
ymin = 0;
ymax = 12;
xinc = 1;
yinc = 0.5;
sDNA = [];
for sDNATemp = 2:3:nDNA
    sDNATemp = sDNATemp*1.5;
sDNA = [sDNA sDNATemp-2 sDNATemp-1 sDNATemp];
end
 
 
RecA =2:3*1.5:nDNA*1.5;
dsDNAup = 1:nDNA;
dsDNAdown = 1:nDNA;

dsDNAupy = ones(1,nDNA)*5.35;
dsDNAdowny = ones(1,nDNA)*5;

sDNAy = ones(1,max(size(sDNA)))*4.3;
RecAy = ones(1,max(size(RecA)))*4.5;
cy5x = dsDNAdown(max(size(dsDNAdown))-cy5backWard);
cy5y = max(dsDNAdowny)+0.5;
cy3x = sDNA(max(size(sDNA)) - cy3backWard);
cy3y = max(sDNAy)-0.5;
fret0x = (cy3x - cy5x)*0.15;
hold on

hRecA = plot(RecA,RecAy,'o','MarkerEdgeColor','c','MarkerFaceColor','w','MarkerSize',42,'EraseMode','xor');
%Str = ['delta' num2str(x/10) ' FRET = ' num2str(y/10)  ];
for i=1:max(size(RecA))
text(RecA(i)-0.8,RecAy(i),num2str(i));
end

hsDNA = plot(sDNA,sDNAy,'^','MarkerEdgeColor','m','MarkerFaceColor','m','MarkerSize',6,'EraseMode','xor');

hdsDNAup = plot(dsDNAup,dsDNAupy,'v','MarkerEdgeColor','g','MarkerFaceColor','g','MarkerSize',6,'EraseMode','xor');
hdsDNAdown = plot(dsDNAdown,dsDNAdowny,'^','MarkerEdgeColor','m','MarkerFaceColor','m','MarkerSize',6,'EraseMode','xor');
hcy3 = plot(cy3x,cy3y,'p','MarkerEdgeColor','r','MarkerFaceColor','r','MarkerSize',30,'EraseMode','xor');
hcy5 = plot(cy5x,cy5y,'v','MarkerEdgeColor','g','MarkerFaceColor','g','MarkerSize',30,'EraseMode','xor');
hfret = plot(fret0x*10,10/(1+(fret0x/6).^6),'o','MarkerEdgeColor','b','MarkerFaceColor','b','MarkerSize',20,'EraseMode','xor');
htext = text( 'String','0,0', 'Position',[0 0]);
delta = 1:0.1:10;
FRET = 1./(1+(delta/6).^6);
x1 = delta*(xmax/10);
y1 = FRET*(ymax);
    
fret =  plot(x1,y1,'-','MarkerEdgeColor','b','MarkerFaceColor','b','MarkerSize',10,'EraseMode','xor');
hold off
axis([xmin xmax ymin ymax]);
set(gca,'XTick',xmin:xinc:xmax);
set(gca,'YTick',ymin:yinc:ymax);
grid on
pause;
dsDNAupOrigin = dsDNAup;
dsDNAdownOrigin = dsDNAdown;
for i=1:3:nDNA
    dsDNAupy(i) = 5;
    dsDNAupy(i+1) = 5;
    dsDNAupy(i+2) = 5;
    dsDNAdowny(i) = 4.7;
    dsDNAdowny(i+1) = 4.7;
    dsDNAdowny(i+2) = 4.7;
    if (i<nDNA -1)
    dsDNAup((i+3):nDNA) = dsDNAup((i+3):nDNA) - min(dsDNAup((i+3):nDNA))+sDNA(i+3);
    dsDNAdown((i+3):nDNA) = dsDNAup((i+3):nDNA) - min(dsDNAup((i+3):nDNA))+sDNA(i+3);
    end
    set(hdsDNAup,'ydata',dsDNAupy);
    set(hdsDNAdown,'ydata',dsDNAdowny);
    set(hdsDNAup,'xdata',dsDNAup);
    set(hdsDNAdown,'xdata',dsDNAdown);
    
    cy5x = dsDNAdown(max(size(dsDNAdown))-cy5backWard);
    set(hcy5,'xdata',cy5x);
   

    delta = (cy5x - cy3x)*0.51;
    delta = abs(delta);
    FRET = 1/(1+(delta/6)^6);
    x = abs(delta*(xmax/10));
    y = FRET*ymax;
    set(hfret,'xdata',x);
    set(hfret,'ydata',y);
    set(hcy5,'MarkerSize',y*5+10);
    set(hcy3,'MarkerSize',100-y*5);
    Str = ['(' num2str(x/10) ',' num2str(y/10)  ')'];
    set(htext,'String',Str);
    set(htext,'Position',[x+1 y+1]);
    drawnow;
    pause;
end