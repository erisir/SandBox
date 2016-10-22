
extern unsigned int PWM_high;               //PWM空比寄存器，即PWM输出高电平的PCA时钟脉冲个数（占空比写入变量）。
void LoadPWM(unsigned int high);
void PWMn_init();