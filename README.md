acs
===

Ant Colony System for TSP solution using Akka
http://www.idsia.ch/~luca/acs-bio97.pdf

And this is another paper, maybe it's good:
http://www.idsia.ch/~luca/acs-ec97.pdf


Actors:
- Graph: 
The graph we're traversing. Responsible for answering "whats here" queries and performing global trail updates

- Ant: 
A single act, keeps track of it's tour and makes decision where to go next on the graph

- Colony: 
Ant controller, spawns them and triggers global trail update for best tour when all the ants finish their traversal.
Notifies the parent of best route after each wave of ants.

- Main: 
Driver that loads up XQF131 graph from TSPLIB (http://www.math.uwaterloo.ca/tsp/vlsi/xqf131.tour.html) and attempts to slove it.
