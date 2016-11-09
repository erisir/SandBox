import requests
import urllib
import re
from bs4 import BeautifulSoup
from collections import deque
import pymysql 
from _winapi import NULL


url = 'http://www.nature.com/nmeth/archive/index.html'
webFeild = 'introduction results discussion methods references acknowledgments author-information supplementary-information'
tableFeild = 'introduction results discussion methods reference acknowledgments author supplementary'

queue = deque()
queue1 = deque()
visited = set()

conn = pymysql.connect(host='127.0.0.1',port=3306,user='root',passwd='jiushizhu',db='nmeth',charset="utf8")
cursor = conn.cursor()
cursor.execute('delete from article where 1=1')
cursor.execute('delete from pics where 1=1')

def getTextById(bs,_id): 
    temp =  bs.find(id=_id)
    try:
        textValue = temp.div.get_text().replace(temp.div.nav.get_text(), " ")
        textValue = textValue.replace('\'','\'')
        textValue = textValue.replace('\"','\"') 
        return textValue;
    except:
        return 'None'  

def downloadImageFile(imgUrl):  
    temp = imgUrl.split('/')[-1]  ;
    local_filename = "F:\\Development\\php\\www\\nmethImage"+ temp     
    r = requests.get(imgUrl, stream=True) # here we need to set stream = True parameter  
    with open(local_filename, 'wb') as f:  
        for chunk in r.iter_content(chunk_size=1024):  
            if chunk: # filter out keep-alive new chunks  
                f.write(chunk)  
                f.flush()  
        f.close()  
    return "/nmethImage/"+ temp  

 
response = requests.get(url)
soup = BeautifulSoup(response.text, "html.parser")

soup1=  BeautifulSoup(soup.find_all(class_="issue").__str__(), "html.parser")
for x in soup.find_all('a'):
    if re.compile("nmeth/journal").search(x['href']) and re.compile("index.html").search(x['href']):
        queue.append('http://www.nature.com/'+x['href'])


while queue:
    url1 = queue.popleft()
    print('visiting:  '+url1+"-----------aticle list")
    response1 = requests.get(url1)
    soup1 = BeautifulSoup(response1.text, "html.parser")    
    for x in soup1.find_all('a',class_="fulltext"):
        queue1.append('http://www.nature.com/'+x['href'])


while queue1:
    url2 = queue1.popleft()
    articleAbstract = ""
    keyWord=  "" 
    imgsrc =  ""
    description = "" 
    articleTitle =  ""
    print('    visiting:  '+url2+"-----------aticle fullpage")
    try:
        response2 = requests.get(url2)
        soup2 = BeautifulSoup(response2.text, "html.parser")
        heading=soup2.find(class_="article-heading")
        firstSesion = soup2.find(class_="section  first expanded")
        firstparagraph = soup2.find(class_="first-paragraph")
        standfirst = soup2.find(class_="standfirst")
        standfirst_abs = soup2.find(class_="standfirst-abs")
        try:
            articleTitle = heading.get_text()
            print('    articleTitle:  '+articleTitle)      
        except:
            print("    get tittle Error!")  
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
                            print("    get Abstract Error!")
        try:      
            keyWord= firstSesion.div.div.get_text()
        except:
            keyWord= "null"
            print("    get keyWord Error!")   
             
        sqlstr = 'insert into article (fid,title,abstract,keywords,copyfrom'
        for x in tableFeild.split(' '):
            sqlstr +=','+ x
        sqlstr +=') values (1,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)'
        
        sqldata = [articleTitle,articleAbstract,keyWord,url2]
        for x in webFeild.split(' '):
            sqldata.append(getTextById(soup2,x))
        
        try:
            cursor.execute(sqlstr,sqldata)
        except:
            print('get article  errot') 
            continue
            
        lastId = cursor.lastrowid
            
        for figlink in soup2.find_all('li',class_="full"):
            url4 = 'http://www.nature.com/'+figlink.a['href']
            print(url4)
            sqlstr2 = 'insert into pics (fid,url,description,copyfromurl) values  ('+ str(lastId)+',%s,%s,%s)'
            try:
                response4 = requests.get(url4)
                soup4 = BeautifulSoup(response4.text, "html.parser")        
                img = soup4.find('img',class_="fig")
                des = soup4.find('div',class_="description")       
                try:
                    imgsrc = 'http://www.nature.com/'+img['src'] 
                except:
                    print('get img src error')
                try:
                    description =  des.get_text().replace('\'','\'');  
                except: 
                    print('get description  error') 
                try:
                    cursor.execute(sqlstr2,[imgsrc,description,imgsrc])
                except:
                    print('inser pics  error') 
            except:  
                print('get pics  error')    

    except:
         print('get html  error') 
conn.close() 
