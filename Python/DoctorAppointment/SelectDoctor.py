import Conn
import time
from _winapi import NULL

class SelectDoctor(object):
    keshiName = ""
    conn_ = NULL;
    def __init__(self,keshiName):
        self.conn_=Conn.conn("BJGuahao")
        print("已选择科室："+keshiName)
        self.keshiName = keshiName
    def SelectDoctorbyTime(self,avilableDate):
        print("="*50)
        print("->"*11+self.keshiName+"<-"*11)
        print("="*50)
        self.conn_.sqlStr_ = "select doctorId,doctorTitleName,skill,avialbeDate,doctorName from doctorInfo where  keshiName='"+self.keshiName+"'"
        self.conn_.sqlStr_ +="  and avialbeDate like '%"+avilableDate+"%'"
        res = self.conn_.select() 
        a = 0
        for x in res:
            doctorName = "--"
            if x[4] != None:
                doctorName=x[4]
            print("["+str(a)+"]\t"+x[1]+"\t["+doctorName+"]\t时间:"+x[3]+"\r\n\n\t专长:"+x[2])
            a +=1
            print("."*50)
        indStr = input("请选择序号[回车返回]\r\n")
        while not indStr.isnumeric():
            if indStr == "":
                return
            indStr = input("输入非法，请重新输入\r\n")
        ind = int(indStr)
        print("\r\n已选择:\r\n["+str(ind)+"]\t"+res[ind][1]+"\t["+res[ind][4]+"\t时间:"+res[ind][3]+"]\r\n\t专长:"+res[ind][2])
        print("="*50)
         
    
                
                
               
if __name__ == '__main__':
    department = {1:"妇科门诊",2:"风湿免疫门诊",3:"口腔科门诊",4:"内分泌门诊",5:"运动医学门诊"}
    print("="*50)
    print(department)
    print("="*50)
    ind = input("请输入科室序号【回车结束】\r\n")
    keshiName = ""
    while (not ind.isnumeric() or  int(ind)<=0 or int(ind)>len(department)):
        ind = input("请输入科室序号有误！【回车结束】\r\n")
    keshiName  = department[int(ind)]
    instence = SelectDoctor(keshiName)
    print("="*50)
    getInput  = input("请输入就诊时间i.e.【周三上】\r\n")
    while getInput != "":
        instence.SelectDoctorbyTime(getInput)
        getInput = input("请输入就诊时间i.e.【周三上】\r\n")
        
    print("=" * 50) 
    print("=" * 50) 
    print("=" * 23+"结束"+"=" * 23) 
    print("=" * 50) 
    print("=" * 50)     
    time.sleep( 2000 )
        