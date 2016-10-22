function [ phase_shift ] = PhraseShift( a,b )
%PHRASESHIFT Summary of this function goes here
%   Detailed explanation goes here
phase_shift=acos(dot(a,b)/(norm(a)*norm(b)));

end

