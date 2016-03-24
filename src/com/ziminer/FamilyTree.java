package com.ziminer;


public class FamilyTree {


    public enum Relationship {
        PARENTAL, MARITAL
    }

    public void AddRelationship(Person from, Person to, Relationship type) {

    }

    public Person CreatePerson(String name) {
        return new Person(this, name);
    }
}
