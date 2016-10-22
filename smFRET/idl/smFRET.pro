 ;
; Performs analysis on all the files in the sub-directories of
; a given directory. Tired of running everything by hand.
;
; Hazen 3/00
;

; get directory to analyze from user
pro smFRET

CY3X1 = 35
CY3Y1 = 410

CY5X1 = 290
CY5Y1 = 411

CY3X2 = 9
CY3Y2 = 385

CY5X2 = 263
CY5Y2 = 387

deltax = CY5X1 - CY3X1
deltay = CY5Y1 - CY3Y1

CosThita = ((CY3X2-CY3X1)*(CY5X2-CY5X1)+(CY3Y2-CY3Y1)*(CY5Y2-CY5Y1))/(SQRT((CY3X2-CY3X1)^2+(CY3Y2-CY3Y1)^2 )*SQRT((CY5X2-CY5X1)^2+(CY5Y2-CY5Y1)^2 ))
Thita = ACos(CosThita)

if CY5Y2 - deltay lt CY3Y2  then begin
Thita = -1*Thita
endif

dir= "I:\t\"
run = "a"
print, "name of directories to analyze"
;read, run

path = dir + run
print, path

; find all the sub-directories in that directory

foo_dirs = findfile(path + '\*')
nfoo_dirs = size(foo_dirs)

nsub_dirs = 0                ; figure number of sub-directories
for i = 2, nfoo_dirs(1) - 1 do begin
    if rstrpos(foo_dirs(i),'\') eq (strlen(foo_dirs(i)) - 1) then begin
       nsub_dirs = nsub_dirs + 1
    endif
endfor

; print, "found : ", nsub_dirs, " sub-directories, which are :"
sub_dirs = strarr(nsub_dirs)
j = 0
for i = 2, nfoo_dirs(1) - 1 do begin    ; get sub-directory names
    if rstrpos(foo_dirs(i),'\') eq (strlen(foo_dirs(i)) - 1) then begin
       sub_dirs(j) = foo_dirs(i)
       j = j + 1
    endif
endfor

; for i = 0, nsub_dirs - 1 do begin     ; print sub_directory names
;   print, sub_dirs(i)
; endfor

; now go through sub-directories finding the files to be analyzed and
; analyzing them if necessary.

for i = 0, nsub_dirs - 1 do begin

    print, "Current Directory : ", sub_dirs(i)

    ; find all the *.pma files in the sub-directory
    ; analyze them if there is no currently existing .pks file

    f_to_a = findfile(sub_dirs(i) + '*.tif')
    print,f_to_a
    nf_to_a = size(f_to_a)
    for j = 0, nf_to_a(1) - 1 do begin
       f_to_a(j) = strmid(f_to_a(j), 0, strlen(f_to_a(j)) - 4)
       openr, 1, f_to_a(j) + ".pks", ERROR = err
       close, 1
       if strmid(f_to_a(j), strlen(f_to_a(j))-3, strlen(f_to_a(j))-1) eq "ave" then begin
        err=0;
       endif

       if err ne 0 then begin
         ; print, "Working on : ", f_to_a(j), err
         print, "Working on : ", f_to_a(j)
         ;run,deltax,deltay
         p_nxgn1_ffpwn, f_to_a(j),deltax,deltay,Thita,CY3X1,CY3Y1
         p_nxgn1_apwn, f_to_a(j)
       endif
    endfor
endfor

print, "Done."

end
