import nature
narUrl = 'http://www.nature.com/nature/archive/index.html?year=2014-2013-2012-2011-2010'
nmethUrl = 'http://www.nature.com/nmeth/archive/index.html'
webTagField = 'introduction results discussion methods references acknowledgments author-information supplementary-information'
tableFeild = 'introduction results discussion methods reference acknowledgments author supplementary'

#nar = nature.nature('nmeth',2,narUrl,webTagField,tableFeild)
#nar.start()

nmeth = nature.nature('nmeth',1,nmethUrl,webTagField,tableFeild)
nmeth.start()
    
    