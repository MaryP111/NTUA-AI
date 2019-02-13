oneway(L):-line(L,_,1,_,_,_,_,_).

twoway(L):-line(L,_,0,_,_,_,_,_).

canMoveFromTo(X,Y):-neighbors(X,Y).

canMoveFromTo(X,Y):-neighbors(Y,X),belongsTo(X,L),belongsTo(Y,L),twoway(L).

isFree(T):-taxi(_,_,T,1,_,_,_,_,_).

