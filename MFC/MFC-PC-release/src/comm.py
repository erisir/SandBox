import sys,threading,time; 
import serial; 
import binascii,encodings; 
import re; 
import socket; 


class ReadThread: 
    def __init__(self, Output=None, Port=0, Log=None, i_FirstMethod=True): 
        self.l_serial = None; 
        self.alive = False; 
        self.waitEnd = None; 
        self.bFirstMethod = i_FirstMethod; 
        self.sendport = ''; 
        self.log = Log; 
        self.output = Output; 
        self.port = Port; 
        self.re_num = None; 
    
    
    def waiting(self): 
        if not self.waitEnd is None: 
            self.waitEnd.wait(); 
    
    
    def SetStopEvent(self): 
        if not self.waitEnd is None: 
            self.waitEnd.set(); 
            self.alive = False; 
            self.stop(); 
    
    
    def start(self): 
        self.l_serial = serial.Serial(); 
        self.l_serial.port = self.port; 
        self.l_serial.baudrate = 4800; 
        self.l_serial.timeout = 2; 
       
    
        self.re_num = re.compile('\d'); 
    
    
        try: 
            if not self.output is None: 
                self.output.WriteText(u'打开通讯端口\r\n'); 
            if not self.log is None: 
                self.log.info(u'打开通讯端口'); 
            self.l_serial.open(); 
            
        except : 
            if self.l_serial.isOpen(): 
                self.l_serial.close(); 
                self.l_serial = None; 
        
        
            if not self.output is None: 
                self.output.WriteText(u'出错：\r\n %s\r\n' ); 
            if not self.log is None: 
                self.log.error(u'%s' ); 
            return False; 
        
        
        if self.l_serial.isOpen(): 
            if not self.output is None: 
                self.output.WriteText(u'创建接收任务\r\n'); 
            if not self.log is None: 
                self.log.info(u'创建接收任务'); 
                self.waitEnd = threading.Event(); 
                self.alive = True; 
                self.thread_read = None; 
                self.thread_read = threading.Thread(target=self.FirstReader); 
                self.thread_read.setDaemon(1); 
                self.thread_read.start(); 
                return True; 
        else: 
            if not self.output is None: 
                self.output.WriteText(u'通讯端口未打开\r\n'); 
            if not self.log is None: 
                self.log.info(u'通讯端口未打开'); 
            return False; 
    
    
    def InitHead(self): 
    #串口的其它的一些处理 
        try: 
            time.sleep(3); 
            if not self.output is None: 
                self.output.WriteText(u'数据接收任务开始连接网络\r\n'); 
            if not self.log is None: 
                self.log.info(u'数据接收任务开始连接网络'); 
                self.l_serial.flushInput(); 
                self.l_serial.write('this is a test msg'.encode('ascii')); 
                data1 = self.l_serial.read(1024); 
        except : 
            if not self.output is None: 
                self.output.WriteText(u'出错：\r\n %s\r\n' ); 
            if not self.log is None: 
                self.log.error(u'%s' ); 
                self.SetStopEvent(); 
            return; 
        
        
        if not self.output is None: 
            self.output.WriteText(u'开始接收数据\r\n'); 
        if not self.log is None: 
            self.log.info(u'开始接收数据'); 
        self.output.WriteText(u'===================================\r\n'); 
    
    
    def SendData(self, i_msg): 
        lmsg = ''; 
        isOK = False; 
        if isinstance(i_msg, unicode): 
            lmsg = i_msg.encode('gb18030'); 
        else: 
            lmsg = i_msg; 
        try: 
        #发送数据到相应的处理组件 
            pass 
        except : 
            pass; 
        return isOK; 
    
    
    def FirstReader(self): 
        data1 = ''; 
        isQuanJiao = True; 
        isFirstMethod = True; 
        isEnd = True; 
        readCount = 0; 
        saveCount = 0; 
        RepPos = 0; 
        #read Head Infor content 
        self.InitHead(); 
        
        
        while self.alive: 
            try: 
                data = ''; 
                n = self.l_serial.inWaiting(); 
                if n: 
                    data = data+self.l_serial.read(n); 
                    print (data.decode("GBK") )                                                   
                    #self.SendData(data); 
                    data = ''; 
                continue; 
            except : 
                if not self.log is None: 
                    self.log.error(u'%s'  ); 
        
        
        self.waitEnd.set(); 
        self.alive = False; 
    
    
    def stop(self): 
        self.alive = False; 
        self.thread_read.join(); 
        if self.l_serial.isOpen(): 
            self.l_serial.close(); 
        if not self.output is None: 
            self.output.WriteText(u'关闭通迅端口：[%d] \r\n' % self.port); 
        if not self.log is None: 
            self.log.info(u'关闭通迅端口：[%d]' % self.port); 
    
    
    def printHex(self, s): 
        s1 = binascii.b2a_hex(s); 
        print( s1); 

class OutPut:
    def WriteText(self,x):
        print("OutPut[WriteText]"+x)
class Log:
    def error(self,x):
        print("Log[error]"+x)
    def info(self,x):
        print("Log[info"+x)
#测试用部分 
#    def __init__(self, Output=None, Port=0, Log=None, i_FirstMethod=True): 
if __name__ == '__main__': 
    output = OutPut()
    log = Log()
    rt = ReadThread(output,"COM3",log,True); 
    rt.sendport = "COM3"
    try: 
        if rt.start(): 
            rt.waiting(); 
            rt.stop(); 
        else: 
            pass; 
    except : 
        print( str("ddd")); 
    
    
    if rt.alive: 
        rt.stop(); 
    
    
    print ('End OK .'); 
    del rt;