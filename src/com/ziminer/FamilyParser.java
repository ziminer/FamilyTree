package com.ziminer;


import java.util.Vector;

public interface FamilyParser {
    void setRoot(Person rootPerson);

    void addRelationship(Person from, Person to, Vector<RelationshipDictionary.Type> path);

    void addDirectRelationship(Person from, Person to, RelationshipDictionary.Type type);
}
