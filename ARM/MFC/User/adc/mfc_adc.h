#ifndef __ADC_H
#define	__ADC_H


#include "stm32f10x.h"
void ADC1_Init(void);
void GetPosition(void);
unsigned int getADCValue(void);
void SetVotageTimes(unsigned int val);
#endif /* __ADC_H */

