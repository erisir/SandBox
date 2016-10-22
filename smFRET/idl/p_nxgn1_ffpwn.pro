;
; semi-automated routine to find all potential peaks
; in the current image. its sort of implicit that the
; image is 512 x 512...
;
; hazen 1/99
;
; modified to look in the "left" channel for peaks, then
; figure out where the peak should be in the "right" channel,
; and then evaluates both spots to see that they are not to
; close to other spots or otherwise ugly
;
; Hazen 1/99
;
; modified to also to the inverse of the previous comment
; i.e. "right" to "left". also, loads mapping coefficients
; so you have to run calc_mapping3 first.
;
; Hazen 2/99
;
; modified to use the same background subtraction routine
; as findpeak2
;
; Hazen 3/99
;
; modified to map the right half of the screen onto the
; left half of the screen to avoid biases in the histograms
; against peaks that have an intermediate FRET value, i.e.
; half of their intensity is in the left channel and half
; is in the right channel. image must be 512x512.
;
; Hazen 11/99
;
; modified to allow for and find half-integer peak centroid positions
;
; Hazen 11/99
;
; made into a procedure to work with batch analysis
;
; Hazen 3/00
;
; modified to work for TJ
;
; Hazen 3/00
;

pro p_nxgn1_ffpwn, run,deltax,deltay,Thita,CY3X1,CY3Y1

loadct, 5

COMMON colors, R_ORIG, G_ORIG, B_ORIG, R_CURR, G_CURR, B_CURR

circle = bytarr(11,11)

circle(*,0) = [ 0,0,0,0,1,1,1,0,0,0,0]
circle(*,1) = [ 0,0,0,1,1,1,1,1,0,0,0]
circle(*,2) = [ 0,0,1,1,0,0,0,1,1,0,0]
circle(*,3) = [ 0,1,1,0,0,0,0,0,1,1,0]
circle(*,4) = [ 1,1,0,0,0,0,0,0,0,1,1]
circle(*,5) = [ 1,1,0,0,0,0,0,0,0,1,1]
circle(*,6) = [ 1,1,0,0,0,0,0,0,0,1,1]
circle(*,7) = [ 0,1,1,0,0,0,0,0,1,1,0]
circle(*,8) = [ 0,0,1,1,0,0,0,1,1,0,0]
circle(*,9) = [ 0,0,0,1,1,1,1,1,0,0,0]
circle(*,10)= [ 0,0,0,0,1,1,1,0,0,0,0]

;circle(*,0) = [ 0,0,0,0,0,0,0,0,0,0,0]
;circle(*,1) = [ 0,0,0,0,0,0,0,0,0,0,0]
;circle(*,2) = [ 0,0,0,0,1,1,1,0,0,0,0]
;circle(*,3) = [ 0,0,0,1,0,0,0,1,0,0,0]
;circle(*,4) = [ 0,0,1,0,0,0,0,0,1,0,0]
;circle(*,5) = [ 0,0,1,0,0,0,0,0,1,0,0];
;circle(*,6) = [ 0,0,1,0,0,0,0,0,1,0,0]
;circle(*,7) = [ 0,0,0,1,0,0,0,1,0,0,0]
;circle(*,8) = [ 0,0,0,0,1,1,1,0,0,0,0]
;circle(*,9) = [ 0,0,0,0,0,0,0,0,0,0,0]
;circle(*,10)= [ 0,0,0,0,0,0,0,0,0,0,0]

; generate gaussian peaks

g_peaks = fltarr(3,3,7,7)

for k = 0, 2 do begin
    for l = 0, 2 do begin
       offx = 0.5*float(k-1)
       offy = 0.5*float(l-1)
       for i = 0, 6 do begin
         for j = 0, 6 do begin
          dist = 0.4 * ((float(i)-3.0+offx)^2 + (float(j)-3.0+offy)^2)
          g_peaks(k,l,i,j) = exp(-dist)
         endfor
       endfor
    endfor
endfor

; initialize variables

film_x = fix(1)
film_y = fix(1)
fr_no  = fix(1)



;;; input film

close, 1          ; make sure unit 1 is closed

openr, 1, run + ".tif"

; figure out size + allocate appropriately
ok=query_tiff(run+".tif",s)
 film_x = s.dimensions(0,0)
 film_y = s.dimensions(1,0)
film_l = s.num_images
;film_l=900


print, "film x,y,l : ", film_x,film_y,film_l

frame   = intarr(film_x,film_y)
ave_arr = fltarr(film_x,film_y)
blank   = intarr(film_x,film_y)




openr, 2, run + "_ave.tif", ERROR = err
if err eq 0 then begin
    close, 2

    close, 1
    frame = read_tiff(run + "_ave.tif")
endif else begin
    close, 2
;for i=0,99 do begin
for i=0,s.num_images-1 do begin
	frame=read_tiff(run+".tif",image_index=i)
	print, "read to array ",i
	;WRITE_TIFF, run +"_frame.tif", frame
    ave_arr = ave_arr + frame
endfor


    close, 1
    ;ave_arr = ave_arr/float(film_l - 1)
    ave_arr = ave_arr/float(film_l)
    ;ave_arr = ave_arr/float(film_l)
    ;ave_arr = ave_arr/100

    frame = fix(ave_arr)

    WRITE_TIFF, run + "_ave.tif", frame, 1, /SHORT
    ;WRITE_TIFF, run + "_ave.tif", frame, 1, RED = R_ORIG, GREEN = G_ORIG, BLUE = B_ORIG, /SHORT

endelse

; subtracts background
;frame=read_tiff(run+".tif",image_index=0)
;WRITE_TIFF, run + "_firstpic.tif", frame, 1, /SHORT;, RED = R_ORIG, GREEN = G_ORIG, BLUE = B_ORIG
temp1 = frame
;temp1 = smooth(temp1,2,/EDGE_TRUNCATE)
;WRITE_TIFF, run + "_0.tif", frame, /SHORT
aves = fltarr(film_x/16,film_y/16)

for i = 8, film_x, 16 do begin
    for j = 8, film_y, 16 do begin
       aves((i-8)/16,(j-8)/16) = min(temp1(i-8:i+7,j-8:j+7))
    endfor
endfor

aves = rebin(aves,film_x,film_y)
aves = smooth(aves,30,/EDGE_TRUNCATE)
;WRITE_TIFF, run + "_0.5.tif", aves, /SHORT
temp1 = fix(frame)-fix(aves)+200
;WRITE_TIFF, run + "_1.tif", temp1, /SHORT
; open file that contains how the channels map onto each other

P = fltarr(4,4)
Q = fltarr(4,4)
foo = float(1)


;print, ""
;openr, 1, "C:\user\tir data\rough.map" ;

;readf, 1, P
;readf, 1, Q
;close, 1

; and map the right half of the screen onto the left half of the screen

left  = temp1(0:255,0:511)
right = temp1(256:511,0:511)

;right = POLY_2D(right, P, Q, 2)
;combined = (left+right)
combined = temp1
; thresholds the image for peak finding purposes

temp2 = combined
med = float(median(combined))
std = 25


;for i = 0, 255 do begin
 ;   for j = 0, film_y - 1 do begin
  ;     if temp2(i,j) lt fix(med + std) then temp2(i,j) = 0    ;???
   ; endfor
;endfor

window, 0, xsize = 512, ysize = 512
tv, frame
window, 1, xsize = 255, ysize = 512
tv, combined
window, 2, xsize = 512, ysize = 512
tv, blank

; find the peaks
;WRITE_TIFF, run + "_2.tif", temp2, 1,/SHORT
temp3 = frame
temp4 = combined
temp5 = blank

good = fltarr(2,4000)
back = fltarr(4000)
foob = intarr(7,7)
diff = fltarr(3,3)

no_good = 0

for i = 270, 495 do begin
    for j = 15, 495 do begin
       if temp2(i,j) gt 0 then begin

         ; find the nearest maxima

         foob = temp2(i-2:i+2,j-2:j+2)
         z = max(foob,foo)
         y = foo / 5 - 2
         x = foo mod 5 - 2

         ; only analyze peaks in current column,
         ; and not near edge of area analyzed

         if x eq 0 then begin
          if y eq 0 then begin
              y = y + j
              x = x + i
              flt_x = float(x)
                 flt_y = float(y)


              ; check if its a good peak
              ; i.e. surrounding points below 1 stdev

              quality = 1
              for k = -3, 3 do begin
                 for l = -3, 3 do begin
                   if circle(k+5,l+5) gt 0 then begin

                    if combined(x+k,y+l) gt fix(0.95*float(z)) then quality = 0
                    ;if combined(xf+k,yf+l) gt fix(0.35*float(z2)) then quality = 0

                   endif
                 endfor
              endfor


              
             ;  if flt_x lt 241 then begin
             ;     xf=deltax+flt_x
             ;     yf=deltay+flt_y



             ;     foob2 = temp2(xf-3:xf+3,yf-3:yf+3)
             ;     z2 = max(foob2,foo)
             ;     yf = yf+(foo / 7) - 3
         		 ;   xf = xf+(foo mod 7) - 3
         		 ; int_xf = xf;round(xf)
             ;     int_yf = yf;round(yf)
             ;     endif    ;CY3X1,CY3Y1

                if flt_x gt 269 then begin
            
                 ;xf=round(CY3X1 +(flt_x-deltax-CY3X1)*cos(Thita)+(flt_y-deltay-CY3Y1)*sin(Thita))
                 ;yf=round(CY3Y1 -(flt_x-deltax-CY3X1)*sin(Thita)+(flt_y-deltay-CY3Y1)*cos(Thita))
                 xf=flt_x-deltax
                 yf=flt_y-deltay


                 foob2 = temp2(xf-3:xf+3,yf-3:yf+3)
                 z2 = max(foob2,foo)
                 yf = yf+(foo / 7) - 3
         		 xf = xf+(foo mod 7) - 3
         		 int_xf =  round(xf)
                 int_yf =  round(yf)
                 endif

 			 for k = -3, 3 do begin
                for l = -3, 3 do begin
                   if circle(k+5,l+5) gt 0 then begin

                    ;if combined(x+k,y+l) gt fix(0.35*float(z)) then quality = 0
                    if combined(xf+k,yf+l) gt fix(1.01*float(z2)) then quality = 0

                   endif
                 endfor
              endfor

              if quality eq 1 then begin

                 ; draw where peak was found on screen

                 for k = -5, 5 do begin
                   for l = -5, 5 do begin
                    if circle(k+5,l+5) gt 0 then begin
                        temp3(x+k,y+l) = 255
                        temp4(x+k,y+l) = 255
                        temp5(x+k,y+l) = 255
                    endif
                   endfor
                 endfor

                 ; compute difference between peak and gaussian peak
                 ;best_x=0
                 ;best_y=0

                 ;cur_best = 10000.0
                 ;for k = 0, 2 do begin
                  ; for l = 0, 2 do begin
                   ; diff(k,l) = total(abs((float(z) - aves(x,y)) * g_peaks(k,l,*,*) - (float(temp1(x-3:x+3,y-3:y+3)) - aves(x,y))))
                    ;if diff(k,l) lt cur_best then begin
                     ;   best_x = k
                      ;  best_y = l
                       ; cur_best = diff(k,l)
                   ; endif
                   ;endfor
                 ;endfor

                 flt_x = float(x); - 0.5*float(best_x-1)
                 flt_y = float(y); - 0.5*float(best_y-1)

                 ; calculate and draw location of companion peak

                 ;xf = 256.0
                 ;yf = 0.0
                 ;for k = 0, 3 do begin
                  ; for l = 0, 3 do begin
                   ; xf = xf + P(k,l) * float(flt_x^l) * float(flt_y^k)
                    ;yf = yf + Q(k,l) * float(flt_x^l) * float(flt_y^k)
                   ;endfor
                 ;endfor


                 for k = -5, 5 do begin
                   for l = -5, 5 do begin
                    if circle(k+5,l+5) gt 0 then begin
                        temp3(int_xf+k,int_yf+l) = 255
                        temp5(int_xf+k,int_yf+l) = 255

                    endif
                   endfor
                 endfor

                 ;xf = float(round(2.0 * xf)) * 0.5
                 ;yf = float(round(2.0 * yf)) * 0.5

                 ;wset, 0
                ;tv, temp3
                 ;wset, 1
                 ;tv, temp4

                 good(0,no_good) = flt_x
                 good(1,no_good) = flt_y
                 back(no_good) = aves(x,y)
                 no_good = no_good + 1
                 good(0,no_good) = xf
                 good(1,no_good) = yf
                 back(no_good) = aves(int_xf,int_yf)
                 no_good = no_good + 1
              endif
          endif
         endif
      endif
    endfor
endfor

wset, 0
tv, temp3
wset, 1
tv, temp4
wset, 2
tv, temp5

frame1=intarr(s.num_images,s.dimensions(0,0),s.dimensions(1,0))

for i=0,s.num_images-1 do begin
    if i mod 10 eq 0 then begin
	frame1(i,*,*)=read_tiff(run+".tif",image_index=i)
	print, "read to array ",i
	for j=0,s.dimensions(0,0)-1 do begin
		for k=0,s.dimensions(1,0)-1 do begin
		   frame1(i,j,k)=frame1(i,j,k)+temp5(j,k)
		endfor

	endfor

    endif
endfor

for i=0,s.num_images-1 do begin
    if i mod 10 eq 0 then begin
	;WRITE_TIFF, run + "selected.tif", frame1(i,*,*),/APPEND,/short
	endif
endfor

for j=0,511 do begin
		for k=0,s.dimensions(1,0)-1 do begin
		    combined(j,k)=combined(j,k)+100*temp5(j,k)
		endfor
endfor


;WRITE_TIFF, run + "_temp5.tif", temp5, 1,/SHORT

WRITE_TIFF, run + "_selected_ave.tif", combined, 1,/SHORT ;RED = R_ORIG, GREEN = G_ORIG, BLUE = B_ORIG
;WRITE_TIFF, run + "_selected_left.tif", temp4, 1,/SHORT;, RED = R_ORIG, GREEN = G_ORIG, BLUE = B_ORIG
;WRITE_TIFF, run + "_selected_right.tif", temp3, 1,/SHORT

;WRITE_TIFF, run + "_selected_test.tif", temp5, 1, RED = R_ORIG, GREEN = G_ORIG, BLUE = B_ORIG


print, "there were ", no_good, "good peaks"

close, 1
openw, 1, run + ".pks"
for i = 0, no_good - 1 do begin
    printf, 1, i+1, good(0,i),good(1,i),back(i)
endfor
close, 1
openw, 1, run + "position.txt"
for i = 0, no_good - 1 do begin
    printf, 1, i+1, good(0,i),good(1,i),back(i)
endfor
close, 1
end