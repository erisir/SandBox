
;
; analyzes the time traces of the molecules
; that created the peaks that were identified
; with findpeak.
;
; hazen 1/99
;
; modified to give background subtracted value
; based on estimated background from findpeak2.
;
; hazen 3/99
;
; modified to calculate background directly using
; median of surrounding values.
;
; hazen 7/99
;
; above modification removed.
;
; hazen 7/99
;
; modified to use a gaussian weighting when calculating
; the peak intensity in hopes of an improvement in SNR.
;
; hazen 11/99
;
; modified to correct an error in how the background was subtracted
;
; hazen 2/00
;
; modified for use by TJ
;
; hazen 3/00
;

pro p_nxgn1_apwn, run

loadct, 5

; generate gaussian peaks

g_peaks = fltarr(2,2,7,7)

for k = 0, 1 do begin
	for l = 0, 1 do begin
		offx = -0.5*float(k)
		offy = -0.5*float(l)
		for i = 0, 6 do begin
			for j = 0, 6 do begin
				dist = 0.4 * ((float(i)-3.0+offx)^2 + (float(j)-3.0+offy)^2)
				;dist = 0.3 * ((float(i)-3.0+offx)^2 + (float(j)-3.0+offy)^2)
				g_peaks(k,l,i,j) = 2.0*exp(-dist)
			endfor
		endfor
	endfor
endfor

all  = fltarr(11,11)
apeak  = fltarr(1)	; temp storage for analysis
apeak1  = fltarr(3,3)
reformedpoint = fltarr(25)
;newback1 = fltarr(1)
;newback2 = fltarr(1)
;newback3 = fltarr(1)
;newback4 = fltarr(1)
;newback = fltarr(1,1)


; initialize variables

film_x = fix(1)
film_y = fix(1)
fr_no  = fix(1)

close, 1				; make sure unit 1 is closed
close, 2

info1 = file_info( run + ".tif" )
info2 = file_info( run + ".pks" )

if ( info1.READ ne 1 ) then begin
	print, "Can't read .tif file!"
	return
endif
if ( info2.READ ne 1 ) then begin
	print, "Can't read .pks file!"
	return
endif

openr, 1, run + ".tif"
openr, 2, run + ".pks"

; figure out size + allocate appropriately
ok=query_tiff(run+".tif",s)
 film_x = s.dimensions(0,0)
 film_y = s.dimensions(1,0)
film_l = s.num_images
;film_l = 9000;
;film_l = long64(long64(result.SIZE-4)/(long64(film_x)*long64(film_y)))

print, "film l : ",film_l

frame = intarr(film_x,film_y)

; load the locations of the peaks

foo = fix(0)
x = float(0)
y = float(0)
b=float(0)
;b = fltarr(film_l)
no_good = 0
good = fltarr(2,10000)
;back = fltarr(10000,film_l)
back = fltarr(10000)

while EOF(2) ne 1 do begin
	readf, 2, foo, x, y, b
	good(0,no_good) = x
	good(1,no_good) = y
	back(no_good) = b
	no_good = no_good + 1
endwhile

flgd = intarr(2,10000)
flgd(0,*) = floor(good(0,*))
flgd(1,*) = floor(good(1,*))

print, no_good, " peaks were found in file"

time_tr = intarr(no_good,film_l)
noise_tr = fltarr(no_good,film_l)
whc_gpk = intarr(no_good,2)
maxpixel = intarr(1)
maxpixel = 0
; calculate which peak to use for each time trace based on
; peak position

for i = 1, no_good - 1 do begin
	whc_gpk(i,0) = round(2.0 * (good(0,i) - flgd(0,i)))
	whc_gpk(i,1) = round(2.0 * (good(1,i) - flgd(1,i)))
endfor

; load the average image

ave_frame = read_tiff(run + "_ave.tif")

; now read values at peak locations into time_tr array

for i = 0, film_l - 1 do begin
	if (i mod 10) eq 0 then print, "working on : ", i, film_l
	frame=read_tiff(run+".tif",image_index=i)
	for j = 0, no_good - 1 do begin

	    ;all = float(frame(flgd(0,j)-5:flgd(0,j)+5,flgd(1,j)-5:flgd(1,j)+5))
	    ;apeak1 = float(frame(flgd(0,j)-2:flgd(0,j)+2,flgd(1,j)-2:flgd(1,j)+2))
	    ;newback = (total(all) - total(apeak1))/96  ; 121-25=96
	    ;newback1 = MEDIAN(frame(flgd(0,j)-5:flgd(0,j)-3,flgd(1,j)-5:flgd(1,j)+2), /EVEN)
	    ;newback2 = MEDIAN(frame(flgd(0,j)-2:flgd(0,j)+5,flgd(1,j)-5:flgd(1,j)-3), /EVEN)
	    ;newback3 = MEDIAN(frame(flgd(0,j)+3:flgd(0,j)+5,flgd(1,j)-2:flgd(1,j)+5), /EVEN)
	    ;newback4 = MEDIAN(frame(flgd(0,j)-5:flgd(0,j)+2,flgd(1,j)+3:flgd(1,j)+5), /EVEN)
        ;newback1 = min(frame(flgd(0,j)-5:flgd(0,j)-3,flgd(1,j)-5:flgd(1,j)+2))
	    ; newback2 = min(frame(flgd(0,j)-2:flgd(0,j)+5,flgd(1,j)-5:flgd(1,j)-3))
	    ; newback3 = min(frame(flgd(0,j)+3:flgd(0,j)+5,flgd(1,j)-2:flgd(1,j)+5))
	    ; newback4 = min(frame(flgd(0,j)-5:flgd(0,j)+2,flgd(1,j)+3:flgd(1,j)+5))
	    ;newback = float(newback1+newback2+newback3+newback4)/4
	   	;newback = rebin(newback,7,7)
	    ;frame(flgd(0,j)-4:flgd(0,j)+4,flgd(1,j)-4:flgd(1,j)+4)=frame(flgd(0,j)-4:flgd(0,j)+4,flgd(1,j)-4:flgd(1,j)+4)-newback
	   	;apeak = total(float(frame(flgd(0,j)-2:flgd(0,j)+2,flgd(1,j)-2:flgd(1,j)+2)-median(frame(flgd(0,j)-3:flgd(0,j)+3,flgd(1,j)-3:flgd(1,j)+3))))
;apeak = g_peaks(whc_gpk(j,0),whc_gpk(j,1),*,*) * float(frame(flgd(0,j)-3:flgd(0,j)+3,flgd(1,j)-3:flgd(1,j)+3)-min(frame(flgd(0,j)-3:flgd(0,j)+3,flgd(1,j)-3:flgd(1,j)+3)))
	    ;range=2;
	    ;maxpixel=0;
	    ;range_square=(2*range+1)*(2*range+1);
        ;reformedpoint=reform(frame(flgd(0,j)-range:flgd(0,j)+range,flgd(1,j)-range:flgd(1,j)+range),range_square)
        ;for m=1,3 do begin
        ;maxpixel=max(reformedpoint,maxnum)
        ;x=flgd(0,j)+maxnum mod (2*range+1)-range
        ;y=flgd(1,j)+maxnum/(2*range+1)-range
       ; y = foo / 7 - 3
        ; x = foo mod 7 - 3
        ;x=flgd(0,j)+(maxnum mod 5)
       ; y=flgd(1,j)+(maxnum/5)
        ;reformedpoint(maxnum)=0;
	    ;endfor
	    ;maxpixel= maxpixel/3
	    ;print,maxnum
	    ;apeak = maxpixel-min(frame(flgd(0,j)-range:flgd(0,j)+range,flgd(1,j)-range:flgd(1,j)+range))
	    ;apeak = min(frame((flgd(0,j)-range):(flgd(0,j)+range),(flgd(1,j)-range):(flgd(1,j)+range)));-newback
        ;apeak = g_peaks(whc_gpk(j,0),whc_gpk(j,1),*,*) * float(x-3:x+3,y-3:y-3)
	    ;apeak = max(frame(flgd(0,j)-2:flgd(0,j)+2,flgd(1,j)-2:flgd(1,j)+2));-min(frame(flgd(0,j)-2:flgd(0,j)+2,flgd(1,j)-2:flgd(1,j)+2));-min(frame(flgd(0,j)-1:flgd(0,j)+1,flgd(1,j)-1:flgd(1,j)+1));-newback
	    ;frame(x-1:x+1,y-1:y+1)= frame(x-1:x+1,y-1:y+1)+make_array(3,3,/float,value=200)
	    ;frame(x-2:x+2,y-2:y+2)=frame(x-2:x+2,y-2:y+2)+make_array(5,5,/float,value=200)
	    ;noise_tr(j,i) = newback

	   ;newback =  make_array(7,7,/float,value=min(float(frame(flgd(0,j)-3:flgd(0,j)+3,flgd(1,j)-3:flgd(1,j)+3))) )

	   ; apeak = total(g_peaks(whc_gpk(j,0),whc_gpk(j,1),*,*) * float(frame(flgd(0,j)-3:flgd(0,j)+3,flgd(1,j)-3:flgd(1,j)+3)-back(j)))

     	;apeak = total(float(frame(flgd(0,j)-3:flgd(0,j)+3,flgd(1,j)-3:flgd(1,j)+3)-newback))/49

        apeak = g_peaks(whc_gpk(j,0),whc_gpk(j,1),*,*) * float(frame(flgd(0,j)-3:flgd(0,j)+3,flgd(1,j)-3:flgd(1,j)+3)-back(j))
		time_tr(j,i) = round(total(apeak))
	    ;time_tr(j,i) = round(apeak)

	endfor
;	WRITE_TIFF, run + "_after.tif", frame,/APPEND,/short
endfor

close, 1
close, 2


no_good = no_good
openw, 1, run + ".traces"
writeu, 1, film_l
writeu, 1, no_good
writeu, 1, time_tr
close, 1

;openw, 1, run + "noise and signal.txt"
;for j = 0, no_good - 1 do begin
 ;   for i = 0, film_l - 1 do begin
  ;     printf, 1, i+1,time_tr(j,i)
   ; endfor
;endfor
;close, 1

openw, 1, run + ".txt"
printf, 1, film_l
printf, 1, no_good
printf, 1, time_tr
close, 1

;openw, 1, run + "M71 signal 1&2.txt"
 ;   for i = 0, film_l - 1 do begin
  ;     printf,1,time_tr(140,i),time_tr(140,i),time_tr(141,i),time_tr(141,i)
   ; endfor
;close, 1
;openw, 1, run + "M71 signal 2.txt"
 ;   for i = 0, film_l - 1 do begin
  ;     printf,1,time_tr(141,i)
   ; endfor
    ;close, 1
end