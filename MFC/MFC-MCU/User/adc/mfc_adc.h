#ifndef __ADC_H
#define	__ADC_H


#include "stm32f10x.h"
void ADC1_Init(void);
void GetPosition(void);
unsigned int GetADCVoltage(void);
void SetVotageTimes(unsigned int val);
unsigned int ADC_Filter(void) ;
unsigned int ADC_Mean(void);
#endif /* __ADC_H */

