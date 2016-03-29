package com.ziminer.familytree.family;


import java.util.*;

public class Person {
    private final String name;
    private final boolean male;
    private final Family family;

    Person(String name, boolean male) {
        this.name = name;
        this.male = male;
        this.family = new Family(this);
    }

    public String getName() {
        return name;
    }

    public boolean isMale() {
        return male;
    }

    public void addSpouse(Person spouse) throws DoubleSpouseException, DoubleParentException {
        family.AddSpouse(spouse);
    }

    public void addParent(Person parent) throws DoubleParentException {
        addParentInternal(parent);
        if (parent.family.spouse != null) {
            addParentInternal(parent.family.spouse);
        }
    }

    public void traverseRelatives(FamilyParser parser) {
        family.traverseFamily(parser);
    }

    private void addParentInternal(Person parent) throws DoubleParentException {
        if (parent.isMale()) {
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


    private class PersonInfo {
        final Person person;
        final Vector<RelationshipType> path;

        PersonInfo(Person person, Vector<RelationshipType> path) {
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
            return spouse == null ? "" : spouse.getName();
        }

        String GetMotherName() {
            return mother == null ? "" : mother.getName();
        }

        String GetFatherName() {
            return father == null ? "" : father.getName();
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
                if (spouse.isMale() ? child.family.father != spouse : child.family.mother != spouse) {
                    child.addParent(spouse);
                }
                if (isMale() ? child.family.father != me : child.family.mother != me) {
                    child.addParent(me);
                }
            }
        }

        private void addRelative(PersonInfo curPerson, Person relative, RelationshipType type, Set<Person> discovered, Queue<PersonInfo> peopleToCheck) {
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
                    parser.addDistantRelationship(curPerson.person, curPerson.path);
                }

                Person curMother = curPerson.person.family.mother;
                if (curMother != null) {
                    if (!discovered.contains(curMother)) {
                        addRelative(curPerson, curMother, RelationshipType.PARENTAL, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, curMother, RelationshipType.PARENTAL);
                }

                Person curFather = curPerson.person.family.father;
                if (curFather != null) {
                    if (!discovered.contains(curFather)) {
                        addRelative(curPerson, curFather, RelationshipType.PARENTAL, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, curFather, RelationshipType.PARENTAL);
                }

                Person curSpouse = curPerson.person.family.spouse;
                if (curSpouse != null) {
                    if (!discovered.contains(curSpouse)) {
                        addRelative(curPerson, curSpouse, RelationshipType.MARITAL, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, curSpouse, RelationshipType.MARITAL);
                }

                for (Person child : curPerson.person.family.children) {
                    if (!discovered.contains(child)) {
                        addRelative(curPerson, child, RelationshipType.CHILD, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, child, RelationshipType.CHILD);
                }
            }
        }

    }

    // START - For testing basic functionality
    String getSpouseName() {
        return family.GetSpouseName();
    }

    String getMotherName() {
        return family.GetMotherName();
    }

    String getFatherName() {
        return family.GetFatherName();
    }

    boolean hasChild(Person child) {
        return family.HasChild(child);
    }

    int numChildren() {
        return family.NumChildren();
    }
    // END - For testing basic functionality

}
