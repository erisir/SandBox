import time 
import msvcrt, sys
print("123456789",end = '*')
msvcrt.putch(b'\b')
msvcrt.putch(b'\x20')#space
msvcrt.putch(b'\b')
time.sleep(200)