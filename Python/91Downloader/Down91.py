import requests
import re
import sys
from bs4 import BeautifulSoup
from collections import deque
import Conn
import random
import json
from _winapi import NULL
import urllib  
import os
import time
import unicodedata
class Down91:
    urlQueue_ = deque()
    indexUrl_ = ''
    webFeild_ = ''    
    counter_  = 0
    workDir = "c:/91downLoad"
    #targetStr = ["露脸","乱伦","换妻","交换","户外","东北","熟女","少妇","对白","淫荡","妈妈","阿姨","群"]
    targetStr = ["乱伦","换妻","交换","熟女","妈妈","阿姨","群"]
    #targetStr = ["乱伦","换妻","交换","东北","熟女","妈妈","阿姨","肉肉","肉感"]
    headers = {
        "Accept":"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "Accept-Encoding":"gzip, deflate",
        "Accept-Language":"zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4",
        "Cache-Control":"max-age=0",
        "Connection":"keep-alive",
        "Content-Length":"36",
        "Content-Type":"application/x-www-form-urlencoded",
        "Cookie":"__cfduid=dee46732493dbddd0e63ffe23e6bd5e9a1483527149; a4184_pages=1; a4184_times=5",
        "Host":"www2.j289cwsupdownspckxs.info",
        "Origin":"http//www2.j289cwsupdownspckxs.info",
        "Referer":"http//www2.j289cwsupdownspckxs.info/freeone/file.php/OJDZGPq.html",
        "Upgrade-Insecure-Requests":"1",
        "User-Agent":"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36"
    }
    cookieFile = os.path.join(sys.path[0], "cookie")
    def __saveCookie(self):
        """cookies 序列化到文件
        即把dict对象转化成字符串保存
        """
        with open(self.cookieFile, "w") as output:
            cookies = self.__session.cookies.get_dict()
            json.dump(cookies, output)

    def __loadCookie(self):
        """读取cookie文件，返回反序列化后的dict对象，没有则返回None"""
        if os.path.exists(self.cookieFile):
            print("." * 50)
            with open(self.cookieFile, "r") as f:
                cookie = json.load(f)
                return cookie
        return None

    def open(self, url, delay=0, timeout=10):
        """打开网页，返回Response对象"""
        if delay:
            time.sleep(delay)
        return self.__session.get(url, timeout=timeout)

    def getSession(self):
        return self.__session

    def __init__(self,enterPoint,fid,index):
        print("Start to download from 91")
        self.enterPoint = enterPoint
        self.index = index
        self.fid = fid
        os.chdir(sys.path[0])  # 设置脚本所在目录为当前工作目录

        self.__session = requests.Session()
        self.__session.headers = self.headers  # 用self调用类变量是防止将来类改名
        # 若已经有 cookie 则直接登录
        self.__cookie = self.__loadCookie()
        if self.__cookie:
            self.__session.cookies.update(self.__cookie)
                
    def checkTarget(self,text):
        for x in self.targetStr:
            if re.compile(x).search(text):
                #print(x)
                return True
        return False
    def downLoadImg(self,url,path):
            temp = url.split("/")
            path = path+"/"+temp[len(temp)-1]
            data = requests.get(url)
            
            if data.status_code == 200:
                with open(path,'wb') as f:
                    for chunk in data:
                        f.write(chunk)              
        
    def saveDiscription(self,text,path,filename):
            path = path+"/dis.txt"
            print("正在保存信息"+path)
            with open(path,'w') as f:
                    f.write(text)
    def downLoadtorrent(self,url,path):
        response = self.getDataFromUrl(url)
        self.__saveCookie()
        soup=  BeautifulSoup(response.text, "html.parser")
        value = soup.find('input',id="id")["value"]
        data1 = {"type":"torrent",
                        "id":value,
                        "name":value
                        }
        url = "http://www2.j289cwsupdownspckxs.info/freeone/down.php"
        data = self.getSession().post(url, data=data1)
        temp = url.split("/")
        path = path+"/"+value+".torrent"
        print("正在下载torrent文件"+path)
        if data.status_code == 200:
            with open(path,'wb') as f:
                for chunk in data:
                    f.write(chunk)
    def getDataFromUrl(self,url):
        try:
            return requests.get(url)
        except:
            return None
      
    def getArcticleUrl(self,url):
        response = self.getDataFromUrl(url)
        response.encoding = 'UTF-8'
        if(None == response):
            return      
        queue = deque()                
        Titlequeue = deque()                
        soup=  BeautifulSoup(response.text, "html.parser")
        if self.fid ==3:
            for x in soup.find_all('a'):
                try:
                    if re.compile("a_ajax_").search(x['id']) :
                        title = x.get_text()
                        if re.compile("国产").search(title):
                            urlSave = self.index+x['href']
                            queue.append(urlSave)
                        #break         
                except:
                    pass  
        if self.fid ==15:
            for x in soup.find_all('a'):
                try:
                    if re.compile("a_ajax_").search(x['id']) :
                        title = x.get_text()
                        if self.checkTarget(title) :
                            urlSave = self.index+x['href']
                            queue.append(urlSave)
                            Titlequeue.append(title)
                        #break         
                except:
                    pass  
        
       
        while queue:
            time.sleep(1)
            url1 = queue.popleft()

            if self.fid ==3:
                try:
                    self.parseCurrentPage1(url1)
                except:
                    print("error")
            if self.fid == 15:
                title = Titlequeue.popleft()
                self.saveCurrentPageImg(url1,title)
                    
    
    def saveCurrentPageImg(self,url1,title):
        print(url1)
        constStr = "if(this.width>=1024) window.open('"
        response1 = requests.get(url1).text#.replace("<br>","")
        soup = BeautifulSoup( response1, "html.parser")
         
        path = self.workDir+"/"+title
        print("saving\t"+title+".......\t"+url1)
        if not os.path.exists(path):
            try:
                os.mkdir(path)  
            except:
                path = self.workDir+"/"+time.strftime("%y%m%d%H%M%S",time.localtime())+str(random.randint(100,999))
                os.mkdir(path)  
        counter = 0
        imgs = soup.find_all("img")
        for img in imgs: 
            if re.compile("http").search(img['src'] ) :          
                try:
                    print(img['src'])
                    self.downLoadImg(img['src'] ,path)
                    counter+=1
                except:
                    print("保存图片失败")
        print("total["+str(counter)+"]images saved")
    def parseCurrentPage(self,url1):
        str0 = "==================================================================="
        str1 = "==========================================="
        response1 = requests.get(url1)
       
        response1.encoding = 'UTF-8'
        content = response1.text.replace(str0,"</div><div class='temp'>")
        content= content.replace(str1,"</div><div class='temp'>")
        soup = BeautifulSoup(content, "html.parser")
        movies =  soup.find_all("div",class_="temp")

        counter = 0
        for movie in movies:
            text = movie.get_text()     
            print(text)        
            if self.checkTarget(text) :
                        counter +=1
                        print('*'*50)
                        print("="*50+"\r\n")
                        try:
                            self.getMovieDetail(movie,counter)
                        except:
                            print("出现未知错误")
    def parseCurrentPage1(self,url1):
        str0 = "【影片名稱】"
        str1 = "【影片名称】"
        str2 = "</div><div class='temp'>"
        str3 = "【影片格式】"
        response1 = requests.get(url1)
       
        response1.encoding = 'UTF-8'
        content = response1.text.replace(str1,str2)
        content= content.replace(str0,str2)
        soup = BeautifulSoup(content, "html.parser")
        movies =  soup.find_all("div",class_="temp")

        counter = 0
        for movie in movies:
            text = movie.get_text()  
            ind = text.find(str3)
            if ind == -1:
                ind = text.find("【影片大小】")
            text = text[1:ind] 
            counter +=1   
            print(counter) 
            if self.checkTarget(text) :
                        counter +=1
                        print('*'*50+text)
                        print("="*50+"\r\n")
                        try:
                            self.getMovieDetail(movie,counter,text)
                        except:
                            print("出现未知错误")
    def slugify(self,value):
        """
        Normalizes string, converts to lowercase, removes non-alpha characters,
        and converts spaces to hyphens.
        """
        
        value = unicodedata.normalize('NFKD', value).encode('ascii', 'ignore')
        value = re.sub('[^\w\s-]', '', value).strip().lower()
        value = re.sub('[-\s]+', '-', value)
               
    def getMovieDetail(self,movie,counter,filename):
        torrentUrl = movie.a['href']
        discription =filename
        path = self.workDir+"/"+time.strftime("%y%m%d%H%M%S",time.localtime())+str(random.randint(100,999))
        if not os.path.exists(path):
            os.mkdir(path)   
        try:    
            self.downLoadtorrent(torrentUrl,path)
        except:
            print("保存torrent错误")
        try:
            self.saveDiscription(discription,path,filename)
        except:
            print("保存信息错误")
        soup = BeautifulSoup(str(movie), "html.parser")
        images =  soup.find_all("img")
        for image in images:
            image = image['onclick']
            image = image.replace("if(this.width>=1024) window.open('","")
            image = image.replace("');","")         
            try:
                self.downLoadImg(image,path)
            except:
                print("保存图片失败")
    def start(self): 
       
        url = self.enterPoint+"&page="
        for x in range(1,10):
            print("*"*50)
            print("current page\t"+str(x))
            print(url+str(x) )
            print("*"*50)
            self.getArcticleUrl(url+str(x))  

        return