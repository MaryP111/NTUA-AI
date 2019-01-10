## Prolog predicates



* `belongsTo(X, L).`

* `pair(X,Y).`

* `trueOrder(L).`

* `next(X, Y)`, gives all nodes Y that are immediate neighbors of point X (keep in mind that graph is directed).

  * `canMoveFromTo(X, Y):- neighbor(X, Y, L), ( (trueOrder(L) -> pair(X, Y)) ; pair(Y,X))  `, true if X has Y an immediate neighbor (keep in mind that graph is directed)
  * `neighbor(X, Y, L):- belongsTo(X, L), belongTo(Y, L)` : there is a common line between X and Y.

* `taxi(X, Y, id, available, lower_capacity, upper_capacity, languages, rating, long_distance, typeToLuggage(type))`.

  * `typeToLuggage("compact"):- 2. `
  * `typeToLuggage("subcompact"):- 1.`
  * `typeToLuggage("large"):- 5.`

* `client(X, Y, X_dest, Y_dest, longDistance, time, persons, language, luggage)`

  * `eligibleTaxi(taxi(taxiX, taxiY, _, True, lowerCapacity, upperCapacity, languages, _, longDistanceTaxi, luggageCapacity), client(clientX, clientY, dstX, dstY, longDistanceClient _, persons, language, luggage)):- luggageCapacity >= luggage, member(language, languages), lowerCapacity <= persons, upperCapacity >= persons, (longDistanceClient -> longDistanceTaxi).   `



## Statistics



1. $\approx 27,000$ lines
2. 