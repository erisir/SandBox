
import pymysql 
from _winapi import NULL
class conn:
    connPt_ = NULL
    cursor_ = NULL
    
    host_= '127.0.0.1'
    port_ = 3306
    user_ = 'root'
    passswd_ = 'jiushizhu'
    db_ = ''
    charset_ = 'utf8'
    
    sqlStr_ = ''
    def __init__(self, db):
        self.db_ = db    
		try:
			self.connPt_ = pymysql.connect(host=self.host_,port=self.port_,user=self.user_,db=self.db_,charset=self.charset_)
			#self.connPt_ = pymysql.connect(host=self.host_,port=self.port_,user=self.user_,passwd=self.passswd_,db=self.db_,charset=self.charset_)
			self.cursor_ = self.connPt_.cursor()
		except:
			print("数据库无法连接，相关功能停用!")
    def close(self):
        self.connPt_.close()    
    def execute(self,sql,data):
        try:
            self.cursor_.execute(sql,data)
        except:
            print('\t\texectue sql error!\r\t\t'+sql) 
        
    def insert(self):        
        try:
            self.cursor_.execute(self.sqlStr_)
            return self.cursor_.lastrowid
        except:
            print("Insert error!\r\n")
            print(self.sqlStr_)
            
    def update(self):        
        try:
            self.cursor_.execute(self.sqlStr_)
            self.connPt_.commit()
        except:
            print("Update error!\r\n")
            self.connPt_.rollback()
            print(self.sqlStr_)

    def select(self):
        try:
            self.cursor_.execute(self.sqlStr_)
            return self.cursor_.fetchall()
        except:
            print("Fetch error!\r\n")
            print(self.sqlStr_)
            return NULL
        
    def getLastID(self):
        return self.cursor_.lastrowid