#ifndef __PWM_H_
#define __PWM_H_

#define U8 unsigned char

void PWM_Initial(void);
void PWM_clock(U8 clock);
void PMW_mode(void);
void DelayMs(char ms);
void PWM_start(U8 module,U8 mode);
void PWM0_set (unsigned char a);
void PWM1_set (unsigned char a);
void PWM1_set9 (unsigned int a);

#endif