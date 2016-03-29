package com.ziminer.familytree.family;

import java.util.HashMap;

public class PersonFactory {
    private final HashMap<String, PersonImpl> registry = new HashMap<>();

    private Person createPerson(String name, boolean male) throws ExistingOppositeGenderException {
        PersonImpl retPerson = registry.get(name);
        if (retPerson == null) {
            retPerson = new PersonImpl(name, male);
            registry.put(name, retPerson);
        }
        if (retPerson.isMale() != male) {
            throw new ExistingOppositeGenderException();
        }
        return retPerson;
    }

    /**
     * Creates a male person with the given name. If a person of the same name was already created
     * by this factory, returns the existing person.
     *
     * @param name
     * @return
     * @throws ExistingOppositeGenderException If a person of the same name but opposite gender was
     *                                         already created by this factory.
     */
    public Person createMale(String name) throws ExistingOppositeGenderException {
        return createPerson(name, true);
    }

    /**
     * Same as createMale(), but with female gender.
     *
     * @param name
     * @return
     * @throws ExistingOppositeGenderException
     */
    public Person createFemale(String name) throws ExistingOppositeGenderException {
        return createPerson(name, false);
    }
}
