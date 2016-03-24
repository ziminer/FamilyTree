package com.ziminer;


public class Person {
    private String name;
    private FamilyTree tree;

    public Person(FamilyTree tree, String name) {
        this.tree = tree;
        this.name = name;
    }

    public String GetName() {
        return name;
    }

    public void CreateRelationship(Person other, FamilyTree.Relationship type) {
        tree.AddRelationship(this, other, type);
    }
}
