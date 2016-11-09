import requests
import re
from bs4 import BeautifulSoup
from collections import deque
import Conn
from _winapi import NULL

class nature:
    conn_ = NULL;
    fid_ = 0  
    urlQueue_ = deque()
    indexUrl_ = ''
    webFeild_ = ''    
    counter_  = 0
    def __init__(self,db,fid,enterPoint,webTagField,tableField):
        print("Start to download from nature")
        self.conn_ = Conn.conn(db)
        self.fid_ = fid
        self.indexUrl_ = enterPoint
        self.webFeild_ = webTagField
        self.tableFeild_ = tableField
    def getTextById(self,bs,_id): 
        temp =  bs.find(id=_id)
        try:
            textValue = temp.div.get_text().replace(temp.div.nav.get_text(), " ")
            textValue = textValue.replace('\'','\'')
            textValue = textValue.replace('\"','\"') 
            return textValue
        except:
            return 'None' 
    def getTitle(self,soup):
        heading=soup.find(class_="article-heading")
        try:
            articleTitle = heading.get_text()
            return articleTitle    
        except:
            print("\t\tget tittle Error!") 
            return 'NULL'
    def getAbstract(self,soup):
        firstSesion = soup.find(class_="section  first expanded")
        firstparagraph = soup.find(class_="first-paragraph")
        standfirst = soup.find(class_="standfirst")
        standfirst_abs = soup.find(class_="standfirst-abs")
        try:      
            articleAbstract= firstSesion.div.p.get_text()
        except:
            try:      
                articleAbstract= firstparagraph.get_text()
            except:
                try:
                    articleAbstract= standfirst.get_text()
                except:
                    try:
                        articleAbstract= standfirst_abs.get_text()
                    except:
                            articleAbstract= "null"
                            print("\t\tget Abstract Error!")
        return articleAbstract
    def getKeywords(self,soup):
        firstSesion = soup.find(class_="section  first expanded")
        article_keywords = soup.find(class_="article-keywords inline-list cleared")
        
        try:      
            keyWord= firstSesion.div.div.get_text()
        except:
            try:
                keyWord= article_keywords.get_text()
            except:
                keyWord= "null"
                print("\t\tget keyWord Error!")  
        return  keyWord
    def getDataFromUrl(self,url):
        try:
            return requests.get(url)
        except:
            return None
    def getImgDescription(self,soup):
        des = soup.find('div',class_="description")       
        try:
            description =  des.get_text().replace('\'','\''); 
            return description 
        except: 
            print('\t\t\tget description  error') 
            return None
    def getImgSrc(self,soup):
        img = soup.find('img',class_="fig")
        try:
            imgsrc = 'http://www.nature.com/'+img['src'] 
            return imgsrc
        except:
            print('\t\t\tget img src error')
            return None
    def getArcticleUrl(self):
        
        response = self.getDataFromUrl(self.indexUrl_)
        if(None == response):
            return
        
        queue = deque()    
            
        if(self.fid_ == 1):
            soup=  BeautifulSoup(BeautifulSoup(response.text, "html.parser").find_all(class_="issue").__str__(), "html.parser")
            for x in soup.find_all('a'):
                if re.compile("nmeth/journal").search(x['href']) and re.compile("index.html").search(x['href']):
                    queue.append('http://www.nature.com/'+x['href'])
                    #break           
            while queue:
                url1 = queue.popleft()
                print('visiting:  '+url1+"-----------aticle list")
                response1 = requests.get(url1)
                soup1 = BeautifulSoup(response1.text, "html.parser")    
                for x in soup1.find_all('a',class_="fulltext"):
                    self.urlQueue_.append('http://www.nature.com/'+x['href'])
                    #break
                    
        if(self.fid_ == 2):
            soup =BeautifulSoup(BeautifulSoup(response.text, "html.parser").find_all(class_="expanded").__str__(), "html.parser").find_all('a')
            for x in soup:
                if re.compile("supp").search(x['href']):
                    continue
                if re.compile("index").search(x['href']):
                    queue.append('http://www.nature.com/'+x['href'])
                    #break
            
            while queue:
                url = queue.popleft()
                print('\tvisiting:  '+url+"-----------aticle list")
                response1 = self.getDataFromUrl(url)
                if(None == response1):
                    continue
                soup1 =BeautifulSoup(BeautifulSoup(response1.text, "html.parser").find(id = 'af',class_="subsection").__str__(), "html.parser").find_all('a')
                for x in soup1:
                    if re.compile("full/nature").search(x['href']):
                        self.urlQueue_.append('http://www.nature.com/'+x['href'])
                        #break
                
    def start(self):
        self.getArcticleUrl()  
        
        while self.urlQueue_:
            url2 = self.urlQueue_.popleft()
            articleAbstract = ""
            keyWord=  "" 
            imgsrc =  ""
            description = "" 
            articleTitle =  ""
            print('\t\tvisiting:  '+url2+"-----------aticle fullpage")
             
            response2 = self.getDataFromUrl(url2)
            if(None == response2):
                continue
            soup2 = BeautifulSoup(response2.text, "html.parser")                                
            articleTitle = self.getTitle(soup2)     
            articleAbstract= self.getAbstract(soup2)
            keyWord = self.getKeywords(soup2)
            
            self.counter_ +=1
            try:
                print('\t\t['+str(self.counter_)+']articleTitle:  '+articleTitle)  
            except:
                print('\t\t['+str(self.counter_)+']articleTitle contain unprintable character ') 
                                  
            sqlstr = 'insert into article (fid,title,abstract,keywords,copyfrom'
            for x in self.tableFeild_.split(' '):
                sqlstr +=','+ x
            sqlstr +=') values ('+str(self.fid_)+',%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)'
            
            sqldata = [articleTitle,articleAbstract,keyWord,url2]
            for x in self.webFeild_.split(' '):
                sqldata.append(self.getTextById(soup2,x))
                
            self.conn_.execute(sqlstr,sqldata)                  
            lastId = self.conn_.getLastID()
            picCounter = 0    
            for figlink in soup2.find_all('li',class_="full"):#insert image
                url4 = 'http://www.nature.com/'+figlink.a['href']
                sqlstr2 = 'insert into pics (ffid,fid,url,description,copyfromurl) values  ('+ str(self.fid_)+','+ str(lastId)+',%s,%s,%s)'
                 
                response4 = self.getDataFromUrl(url4)
                if(None == response4):
                    continue
                soup4 = BeautifulSoup(response4.text, "html.parser")   
                     
                picCounter +=1
                print('\t\t\tDownloading  Fig['+str(picCounter)+']')  
          
                imgsrc = self.getImgSrc(soup4)
                description = self.getImgDescription(soup4) 
                
                self.conn_.execute(sqlstr2,[imgsrc,description,imgsrc])                
 
        self.conn_.close() 
       