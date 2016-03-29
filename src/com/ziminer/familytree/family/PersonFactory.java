package com.ziminer.familytree.family;

import java.util.HashMap;

public class PersonFactory {
    private final HashMap<String, Person> registry = new HashMap<>();

    public Person createPerson(String name, boolean male) {
        Person retPerson = registry.get(name);
        if (retPerson == null) {
            retPerson = new Person(name, male);
            registry.put(name, retPerson);
        }
        return retPerson;
    }
}
