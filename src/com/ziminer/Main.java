package com.ziminer;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws DoubleParentException, DoubleSpouseException {
        ArrayList<Person> people = new ArrayList<>();
        people.add(PersonFactory.createPerson("Me", true));
        people.add(PersonFactory.createPerson("Spouse", false));
        people.add(PersonFactory.createPerson("Son", true));
        people.add(PersonFactory.createPerson("Dad", true));
        people.add(PersonFactory.createPerson("Mom", false));
        people.add(PersonFactory.createPerson("Mother in Law", false));
        people.add(PersonFactory.createPerson("Father in Law", true));
        people.add(PersonFactory.createPerson("Grandfather", true));
        people.add(PersonFactory.createPerson("Aunt", false));
        people.add(PersonFactory.createPerson("Sister in Law", false));

        people.get(0).AddSpouse(people.get(1));
        people.get(2).AddParent(people.get(0));
        people.get(0).AddParent(people.get(3));
        people.get(3).AddSpouse(people.get(4));
        people.get(5).AddSpouse(people.get(6));
        people.get(1).AddParent(people.get(5));
        people.get(3).AddParent(people.get(7));
        people.get(8).AddParent(people.get(7));
        people.get(9).AddParent(people.get(6));

        FamilyVisualizer vis = new FamilyVisualizer(people.get(0));
        vis.display();
    }
}
