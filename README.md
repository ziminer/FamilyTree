# FamilyTree

Basic family tree implementation, providing a way to build up a family tree using direct relationships, define
distant relationships, and graphically represent the family tree.

### Install (the tested way)
The tested way is using IntelliJ (Community Edition) and Java 1.8.

* Clone and import the pom.xml into an IntelliJ project.
* Create a new Maven run configuration with the command-line: ```clean install exec:java -e```
* Run the configuration


### Playing with it

Look at the Main class (in com.ziminer.familytree). You can add more people, add relationships, etc. and re-run.

If you want to be really adventurous you can create a different RelationshipDictionary (instead of the basic one)
and define other relationships to use for the traversal. Look into RelationshipDictionary.getBasic() to see how that's done.


### Reading the Code

Start at Main to see how the interfaces are supposed to be used and how they interact with each other, and go from there :)

The PersonImpl class contains the core of the implementation. Family is an inner class of PersonImpl which takes care of
the family representation and family tree traversal (using breadth-first search).
