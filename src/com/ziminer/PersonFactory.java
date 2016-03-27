package com.ziminer;

import java.util.HashMap;

public class PersonFactory {
    private static final HashMap<String, Person> registry = new HashMap<>();

    public static Person createPerson(String name, boolean male) {
        Person retPerson = registry.get(name);
        if (retPerson == null) {
            retPerson = new Person(name, male);
            registry.put(name, retPerson);
        }
        return retPerson;
    }

    public static void refresh() {
        registry.clear();
    }
}
