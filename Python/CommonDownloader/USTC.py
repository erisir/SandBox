from Common import Common
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
class USTC:
    
    headers = {
        "Accept":"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "Accept-Encoding":"gzip, deflate",
        "Accept-Language":"zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4",
         }
    def __init__(self,startUrl,savePath):
        self.common = Common(self.headers)
        self.startUrl = startUrl
        self.savePath = savePath
    def checkTarget(self,text):
        for x in self.targetStr:
            if re.compile(x).search(text):
                return True
        return False
     
    def getAllFile(self,ext):
        response =  self.common.open(self.startUrl)
        response.encoding = 'UTF-8'         
        queue = deque()                      
        soup=  BeautifulSoup(response.text, "html.parser")
        for x in soup.find_all('a'):
            try:
                if re.compile(ext).search(x['href']) :
                    title = x.get_text()
                    urlSave = self.startUrl+x['href']
                    queue.append(urlSave)
            except:
                pass  
        
        while queue:
            time.sleep(1)
            url = queue.popleft()
            self.savePath
            self.common.downLoadFile(url, self.savePath)
                    
if __name__ == '__main__':
    startUrl = "http://home.ustc.edu.cn/~rambo/kejian/gx/"
    savePath = "D:/光学课件/"
    ustc = USTC(startUrl,savePath)
    ustc.getAllFile("pdf")
    