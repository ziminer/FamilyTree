package com.ziminer;


import com.ziminer.RelationshipDictionary.Type;

import java.util.*;

public class Person {
    private class PersonInfo {
        final Person person;
        final Vector<Type> path;

        PersonInfo(Person person, Vector<Type> path) {
            this.person = person;
            this.path = path;
        }
    }

    private class Family {
        // Because we're dealing with strictly defined roles
        // and assuming "traditional" social roles and rules
        // (e.g. no incest, no polygamy, no divorce,
        // children only inside a marriage etc.)
        // this representation is good enough. To deal with
        // more complex cases we'd probably want a full graph
        // structure.
        private Set<Person> children;
        private Person spouse;
        private Person mother;
        private Person father;
        private final Person me;


        Family(Person me) {
            this.me = me;
            this.spouse = null;
            this.mother = null;
            this.father = null;
            this.children = new HashSet<>();
        }

        String GetSpouseName() {
            return spouse == null ? "" : spouse.GetName();
        }

        String GetMotherName() {
            return mother == null ? "" : mother.GetName();
        }

        String GetFatherName() {
            return father == null ? "" : father.GetName();
        }

        boolean HasChild(Person child) {
            return children.contains(child);
        }

        int NumChildren() {
            return children.size();
        }

        void AddSpouse(Person spouse) throws DoubleSpouseException, DoubleParentException {
            if (this.spouse != null && !this.spouse.equals(spouse) ||
                    spouse.family.spouse != null && !spouse.family.spouse.equals(me)) {
                throw new DoubleSpouseException();
            }
            // If we passed the previous check with a non-null spouse we can
            // exit early.
            if (this.spouse != null) {
                return;
            }
            // Do this before assigning spouse in case it throws an exception.
            spouse.family.spouse = me;
            this.spouse = spouse;

            // Your children are now my children!
            this.children.addAll(spouse.family.children);
            // Don't need to combine sets twice.
            spouse.family.children = this.children;
            // Update parent links.
            for (Person child : family.children) {
                if (spouse.IsMale() ? child.family.father != spouse : child.family.mother != spouse) {
                    child.AddParent(spouse);
                }
                if (IsMale() ? child.family.father != me : child.family.mother != me) {
                    child.AddParent(me);
                }
            }
        }

        private void addRelative(PersonInfo curPerson, Person relative, Type type, Set<Person> discovered, Queue<PersonInfo> peopleToCheck) {
            discovered.add(relative);
            PersonInfo relativeInfo = new PersonInfo(relative, new Vector<>(curPerson.path));
            relativeInfo.path.add(type);
            peopleToCheck.add(relativeInfo);
        }

        void traverseFamily(FamilyParser parser) {
            parser.setRoot(me);
            Queue<PersonInfo> peopleToCheck = new LinkedList<>();
            Set<Person> discovered = new HashSet<>();
            peopleToCheck.add(new PersonInfo(me, new Vector<>()));
            discovered.add(me);
            while (!peopleToCheck.isEmpty()) {
                PersonInfo curPerson = peopleToCheck.remove();
                // Add appropriate relationship from root to me
                // Only for non-immediate family, since immediate family
                // gets handled in the general case (i.e. for everyone).
                if (curPerson.person != me && curPerson.path.size() > 1) {
                    parser.addRelationship(me, curPerson.person, curPerson.path);
                }

                Person curMother = curPerson.person.family.mother;
                if (curMother != null) {
                    if (!discovered.contains(curMother)) {
                        addRelative(curPerson, curMother, Type.PARENTAL, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, curMother, Type.PARENTAL);
                }

                Person curFather = curPerson.person.family.father;
                if (curFather != null) {
                    if (!discovered.contains(curFather)) {
                        addRelative(curPerson, curFather, Type.PARENTAL, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, curFather, Type.PARENTAL);
                }

                Person curSpouse = curPerson.person.family.spouse;
                if (curSpouse != null) {
                    if (!discovered.contains(curSpouse)) {
                        addRelative(curPerson, curSpouse, Type.MARITAL, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, curSpouse, Type.MARITAL);
                }

                for (Person child : curPerson.person.family.children) {
                    if (!discovered.contains(child)) {
                        addRelative(curPerson, child, Type.CHILD, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, child, Type.CHILD);
                }
            }
        }

    }

    private final String name;
    private final boolean male;
    private final Family family;

    Person(String name, boolean male) {
        this.name = name;
        this.male = male;
        this.family = new Family(this);
    }

    // START - For testing basic functionality
    String GetName() {
        return name;
    }

    String GetSpouseName() {
        return family.GetSpouseName();
    }

    String GetMotherName() {
        return family.GetMotherName();
    }

    String GetFatherName() {
        return family.GetFatherName();
    }

    boolean HasChild(Person child) {
        return family.HasChild(child);
    }

    int NumChildren() {
        return family.NumChildren();
    }
    // END - For testing basic functionality

    boolean IsMale() {
        return male;
    }

    public void AddSpouse(Person spouse) throws DoubleSpouseException, DoubleParentException {
        family.AddSpouse(spouse);
    }

    public void AddParent(Person parent) throws DoubleParentException {
        addParentInternal(parent);
        if (parent.family.spouse != null) {
            addParentInternal(parent.family.spouse);
        }
    }

    private void addParentInternal(Person parent) throws DoubleParentException {
        if (parent.IsMale()) {
            if (this.family.father != null && !this.family.father.equals(parent)) {
                throw new DoubleParentException();
            }
            this.family.father = parent;
        } else if (this.family.mother != null && !this.family.mother.equals(parent)) {
            throw new DoubleParentException();
        } else {
            this.family.mother = parent;
        }
        parent.addChild(this);
    }

    private void addChild(Person child) {
        family.children.add(child);
    }

    public void TraverseRelatives(FamilyParser parser) {
        family.traverseFamily(parser);
    }

    // Equality based on name alone.
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            return ((Person) obj).name.equals(name);
        } else {
            return name.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
