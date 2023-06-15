Concept:
The traveling Salesman is a shortest path optimization problem. A salesman must travel from a start location to each other city (or node) - located on a 2D plane 
with x-y coordinates - exactly once in the shortest path possible (lowest cost) and then return to their original position.


Implimentation:
An adjacency matrix was created initially, enabling the program to determine which nodes were connected. A depth-first search method was utilized, travelling down 
each possible route and determining their individual costs. The solution is then easily identifiable by selecting the route with the lowest cost to return.


How To Run:
To compile the program run
gcc --std=gnu99 -o tsp tsp2.c -lm

To run the code use
./tsp tsp_example_1


To check the code use 
python tsp-verifier.py tsp_example_1 tsp_example_1.txt.tour


If you are using your own examples the names may change.
./tsp filename
python tsp-verifier.py filename filename.txt.tour
