
likes(mary,burger).
likes(mary,cheese).
likes(mary,beans).
likes(mary,wine).
likes(john,wine).
likes(john,mary).

healthy(beans).
healthy(wine).

bornAt(john,1980).
bornAt(mary,1988).

% simple rule with inequality check

agree(X,Y) :- likes(X,Z), likes(Y,Z), X \== Y.

% simple arithmetic

age(X,A) :- bornAt(X,Z), A is 2016 - Z.

%rule order is important

prefers(X, Z) :- likes(X,Z).

prefersHealthy(X, Z) :- likes(X,Z), healthy(Z).
prefersHealthy(X, Z) :- likes(X,Z).

%simple use of cut

prefersMost(X, Z) :- likes(X,Z), healthy(Z), !.