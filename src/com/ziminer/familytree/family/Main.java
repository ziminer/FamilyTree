package com.ziminer.familytree.family;

import com.ziminer.familytree.visualize.FamilyVisualizer;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws DoubleParentException, DoubleSpouseException {
        PersonFactory factory = new PersonFactory();
        ArrayList<Person> people = new ArrayList<>();
        people.add(factory.createPerson("Me", true));
        people.add(factory.createPerson("Spouse", false));
        people.add(factory.createPerson("Son", true));
        people.add(factory.createPerson("Dad", true));
        people.add(factory.createPerson("Mom", false));
        people.add(factory.createPerson("Mother in Law", false));
        people.add(factory.createPerson("Father in Law", true));
        people.add(factory.createPerson("Grandfather", true));
        people.add(factory.createPerson("Aunt", false));
        people.add(factory.createPerson("Sister in Law", false));

        people.get(0).addSpouse(people.get(1));
        people.get(2).addParent(people.get(0));
        people.get(0).addParent(people.get(3));
        people.get(3).addSpouse(people.get(4));
        people.get(5).addSpouse(people.get(6));
        people.get(1).addParent(people.get(5));
        people.get(3).addParent(people.get(7));
        people.get(8).addParent(people.get(7));
        people.get(9).addParent(people.get(6));

        FamilyVisualizer vis = new FamilyVisualizer(people.get(0), RelationshipDictionary.getBasic());
        vis.display();

        boolean test = true;
    }
}
