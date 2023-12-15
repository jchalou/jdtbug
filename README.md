# Reproducer for a bug in ECJ 3.34 and newer

* `mvn compile exec:java` to reproduce the bug. `ProcessingEnvironment#getAllMembers(<org.examle.SubClass>)` doesn't find the inherited static method `m1`. Uses ECJ 3.36. 
* `mvn compile exec:java -Pecj333` to show that the bug is not in ECJ 3.33. The inherited static method `m1` is found.
* `mvn compile exec:java -Dusejavac=true` to use the javac compiler instead of the eclipse compiler. It shows that the javac compiler also finds the inherited static method `m1`. 