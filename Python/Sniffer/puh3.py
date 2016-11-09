import traceback  
import BjguahaoClient
from bs4 import BeautifulSoup as BS
import re
import Conn
import time
import requests
from wsgiref.util import application_uri
from _winapi import NULL
 

class puh3(object):

    homeurl = "http://www.bjguahao.gov.cn"
    appointMainurl = "http://www.bjguahao.gov.cn/hp/appoint/142.htm" #北医三院
    appointPosturl = "http://www.bjguahao.gov.cn/dpt/partduty.htm"
    hospitalId="142"
    departmentId="0"
    doctorId="201105418"
    dutySourceId = "0"
    patientId = ""
    keshiurl = ""
    keshiName = "普外门诊"
    appDate = ""
    appTime = ""
    client = NULL
    appOk = False
    tryCounter = 0;
    conn_ = NULL;
    
    userDict = {
        "农大官":"218874960",
        "张宇微":"225572924"
        }
    doctorDict = {
        #妇科
        "郭红燕":"201105700",#妇科肿瘤、子宫内膜异位症、盆腔疼痛，腹腔镜手术知名专家号14元
        "刘朝辉":"201105704",#妇科疾病、妇产科超声副教授号7元
        "杨池荪":"201105706",#更年期、月经不调副教授号7元
        "王秀云":"201105994",#妇科肿瘤、子宫内膜异位症知名专家号14元
        "王威":"201109446",#更年期.功血.生殖健康.避孕、多囊卵巢、宫腔镜副教授号7元
        "韩劲松":"201112760",#盆底疾病、尿失禁、肿瘤知名专家号14元
        "徐冰":"201121662",
        "耿力":"201105234",#宫颈疾病、阴道镜、炎症正教授号9元
        "张璐芳":"201105996",#内异症、肿瘤、腔镜知名专家号14元
        "王晓晔":"201111378",#高危计划生育、生殖健康、宫腔镜副教授号7元
        "李华":"201121994",#妇科肿瘤、普通妇科副教授号7元:
        "田惠":"201105238",#妇科疾病、宫腔镜手术、高危计划生育、生殖健康副教授号7元:
        "朱馥丽":"201121662"
        #妇科
        }
    
    def __init__(self,name,date,time,doctorName,patientName="张宇微"):
        
        self.keshiName = name
        self.appDate = date
        self.patientId = self.userDict[patientName]
        self.doctorId = self.doctorDict[doctorName]
        self.conn_=Conn.conn("BJGuahao")
        #2_2_2016-11-10 行_上1/下2午_日期
        if time == "上午" :
            self.appTime = "1"
        if time == "下午" :
            self.appTime = "2"
            
        self.client = BjguahaoClient.BjguahaoClient()
        #以下，找到对应科室的预约网址
        soup = BS(self.client.open(self.appointMainurl).text, "html.parser")
        self.keshiurl = self.homeurl
        for x in soup.find_all("a", class_="kfyuks_islogin",text=re.compile(self.keshiName)):
            if x.get_text() == self.keshiName:
                self.keshiurl += x["href"]               
                res = self.client.open(self.keshiurl).text
                soup = BS(res, "html.parser")
                self.departmentId = soup.find(id="dId")["value"]
                print("正在打开["+x.get_text()+"]\t科室代码["+self.departmentId+"]")
                print("=" * 50)                
                break
            
        # 若已经有 cookie 则直接登录
    def getSMSCode(self):
        getsmscodeurl = self.homeurl+"/v/sendorder.htm"
        smsSaveUrl = "http://sm4.iphy.ac.cn/g.php"
        print("正在发送验证码")
        res = self.client.getSession().post(getsmscodeurl,data="")
        print(res.text) 
        print("="*50)
        acqmode = "auto"
        smsCoder = 0
        
        if acqmode == "auto" :
            while smsCoder<1:
                res1 =  requests.get(smsSaveUrl) 
                smsCoder = int(res1.text)
             
        else :
            smsCoder = input("请输入手机中的验证码\r\n")
                      
        return smsCoder 
    
    def confirm(self,smsVerifyCode):
        data = {"dutySourceId":self.dutySourceId,
                "hospitalId":self.hospitalId,
                "departmentId":self.departmentId,
                "doctorId":self.doctorId,
                "patientId":self.patientId,
                "hospitalCardId":"",
                "medicareCardId":"",
                "reimbursementType":"-1",
                "smsVerifyCode":str(smsVerifyCode),
                "isFirstTime":"2",
                "hasPowerHospitalCard":"2",
                "cidType":"1",
                "childrenBirthday":"",
                "childrenGender":"2",
                "isAjax":"true"   
                }
        url = "http://www.bjguahao.gov.cn/order/confirm.htm"
        res = self.client.getSession().post(url, data=data)   
        if res.json()["msg"]== "OK":
            self.appOk = True
            tmp = res.json()["data"]
            #print("["+tmp["patientName"]+"]\t已经成功预约\t"+tmp["hospitalName"]+"\t["+tmp["departmentName"]+"]\t\r\n"+tmp["dutyDate"]+tmp["ampm"]+"的号")
        else:
            print(res.json()["msg"])
    def saveDoctorInfo(self,skill,doctorTitleName,doctorId,dayampm):
            self.conn_.sqlStr_ = "select avialbeDate from doctorInfo where doctorId="+str(doctorId)+ " and keshiName='"+self.keshiName+"'"
            res = self.conn_.select()           
            try:
                if  not len(res):
                    sqlstr = "insert into doctorInfo (hospitalName,hospitalID,keshiName,skill,doctorTitleName,doctorId,avialbeDate) VALUES('北京大学第三医院',142,'"+self.keshiName+"','"+str(skill)+"','"+str(doctorTitleName)+"','"+str(doctorId)+"','"+str(dayampm)+"')"
                    print(sqlstr)
                    self.conn_.sqlStr_ = sqlstr
                    res = self.conn_.insert()
                else:
                    sqlstr = "update doctorInfo  set avialbeDate='"+str(res[0][0])+","+dayampm+"'"+" where doctorId='"+str(doctorId)+"'"
                    print(sqlstr)
                    self.conn_.sqlStr_ = sqlstr
                    res = self.conn_.insert()
            except :  
                traceback.print_exc()
    def getDoctorIdDict(self):
        
        dateStart = 8
        weekStart = 2
        for d in range(dateStart,dateStart+7):            
            for t in range(1,3):
                time.sleep(3)
                apDate = "2016-11-"
                if d>=10 :
                    apDate +=str(d) 
                else:
                    apDate += ("0"+str(d)) 
                apTime = t
                if t==1:
                    tstr = "]上午"
                else:
                    tstr = "]下午"
                print("="*50+apDate+"[星期"+str((weekStart+d-dateStart)%7)+tstr)
          
                data = {
                "hospitalId":self.hospitalId,
                "departmentId": self.departmentId,
                "dutyCode":apTime,
                "dutyDate":apDate,
                "isAjax":"true"
                }
                try:
                    res = self.client.getSession().post(self.appointPosturl, data=data)
                    backup = res.json()["data"].copy()
                    x =backup.pop()
                     
                    while x:
                        
                        if x["doctorTitleName"] !="普通专业号5元":
                            self.saveDoctorInfo(x["skill"],x["doctorTitleName"],x["doctorId"],"[周"+str((weekStart+d-dateStart)%7)+tstr)
 
                        try:
                            x =backup.pop()
                        except:
                            x = 0
                except:
                    print(data)
                    print(backup)
                
                
           
    def start(self):
        
        self.tryCounter += 1
        print("*"*50+"  第[\t" +str(self.tryCounter)+"\t]次尝试")       
        res = self.client.open(self.keshiurl).text
        soup = BS(res, "html.parser")
        
        for avilable in soup.find_all(class_="ksorder_kyy"):
            temp = avilable.input["value"].split('_', 3 )
            if temp[2] == self.appDate and  temp[1] == self.appTime:
                data = {
                "hospitalId":self.hospitalId,
                "departmentId": self.departmentId,
                "dutyCode":self.appTime,
                "dutyDate":self.appDate,
                "isAjax":"true"
                }
                res = self.client.getSession().post(self.appointPosturl, data=data)
                backup = res.json()["data"].copy()
          
                x =backup.pop()
                while x:
                    if x["doctorId"] == self.doctorId:
                        self.dutySourceId = x["dutySourceId"]                      
                        smsCode = self.getSMSCode()
                        self.confirm(smsCode)   
                        break
                    
                    x =backup.pop()
                   
                break
            
        if(not self.appOk):
            print("该时段无法预约")
               

if __name__ == '__main__':
    instence = puh3("妇科门诊","2016-11-14","上午","郭红燕","张宇微")
    mode = "scanOne"
    if mode == "guahao":
        while not instence.appOk:
            instence.start()
            time.sleep( 2 )
    if mode =="scanAll":
        res = instence.client.open("http://www.bjguahao.gov.cn/hp/appoint/142.htm").text
        soup = BS(res, "html.parser")
        for x in soup.find_all('a',class_="kfyuks_islogin" ): 
            instence.keshiName = x.get_text()
            instence.keshiurl = instence.homeurl
            instence.keshiurl += x["href"]
            #以下，找到对应科室的预约网址
            soup = BS(instence.client.open(instence.appointMainurl).text, "html.parser")            
            res = instence.client.open(instence.keshiurl).text
            soup = BS(res, "html.parser")
            
            instence.departmentId = soup.find(id="dId")["value"]
            
            print("正在打开["+x.get_text()+"]\t科室代码["+instence.departmentId+"]")
            print("=" * 50)                
            print(instence.keshiName)       
            instence.getDoctorIdDict()
    if mode =="scanOne":
        instence.getDoctorIdDict()
 
   