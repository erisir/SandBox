%余弦信号
n=0:1000;
Ts=0.0001;
y1=pi*cos(40*pi*n*Ts);
y2=pi*cos(40*pi*n*Ts+pi/4);
subplot(2,1,1);hold on;plot(n,y1,'-g');plot(n,y2,'-r');
xlabel('采样点数n');ylabel('原始图像x(n)');title('余弦信号')
hy1=hilbert(y1);
xr=real(hy1);
xi=imag(hy1);
P1=atan2(xi,xr);%定义相位就是反正切

hy2=hilbert(y2);
xr=real(hy2);
xi=imag(hy2);
P2=atan2(xi,xr);%定义相位就是反正切
%subplot(2,1,2);
hold on;plot(P1,'-g');plot(P2,'-r')
delta = max(P1)-max(P2);
xlabel('采样点数n');ylabel('瞬时相位')
grid on;
subplot(2,1,2);plot(P1-P2,'-g');grid on;