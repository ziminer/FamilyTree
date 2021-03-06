package com.ziminer.familytree.family;


import javax.management.relation.Relation;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

class PersonImpl implements Person {
    private final String name;
    private final boolean male;
    private final Family family;

    PersonImpl(String name, boolean male) {
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
        if (spouse instanceof PersonImpl) {
            family.AddSpouse((PersonImpl) spouse);
        }
    }

    public void addParent(Person parent) throws DoubleParentException {
        if (parent instanceof PersonImpl) {
            PersonImpl parentImpl = (PersonImpl) parent;
            addParentInternal(parentImpl);
            if (parentImpl.family.spouse != null) {
                addParentInternal(parentImpl.family.spouse);
            }
        }
    }

    public List<Person> getRelatives(String title) {
        Vector<RelationshipType> path = RelationshipDictionary.getBasic().getPath(title);
        if (path == null) {
            return new ArrayList<>();
        }

        return family.getRelatives(new LinkedBlockingQueue<>(path), null);
    }

    public void traverseRelatives(FamilyParser parser) {
        family.traverse(parser);
    }

    private void addParentInternal(PersonImpl parent) throws DoubleParentException {
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

    private void addChild(PersonImpl child) {
        family.children.add(child);
    }

    // Equality based on name alone.
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PersonImpl) {
            return ((PersonImpl) obj).name.equals(name);
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


    /**
     * Information about a relative to the root of the family traversal.
     */
    private class RootRelative {
        final PersonImpl person;
        // Relationship path from root of traversal
        // to person.
        final Vector<RelationshipType> path;

        RootRelative(PersonImpl person, Vector<RelationshipType> path) {
            this.person = person;
            this.path = path;
        }
    }

    /**
     * Family encapsulates all the relatives of a person.
     * <p>
     * Keep it nested because it's not used or referred to anywhere else, and is just
     * a more convenient internal representation.
     */
    private class Family {
        // Because we're dealing with strictly defined roles
        // and assuming "traditional" social roles and rules
        // (e.g. no incest, no polygamy, no divorce,
        // children only inside a marriage etc.)
        // this representation is good enough. To deal with
        // more complex cases we'd probably want a full graph
        // structure.
        private Set<PersonImpl> children;
        private PersonImpl spouse;
        private PersonImpl mother;
        private PersonImpl father;
        private final PersonImpl me;


        Family(PersonImpl me) {
            this.me = me;
            this.spouse = null;
            this.mother = null;
            this.father = null;
            this.children = new HashSet<>();
        }

        List<Person> getRelatives(Queue<RelationshipType> path, Person caller) {
            if (path.isEmpty()) {
                return Arrays.asList(me);
            } else {
                RelationshipType type = path.poll();
                List<Person> retPerson = new ArrayList<Person>();

                if (RelationshipType.PARENTAL.equals(type)) {
                    if (father != null && father != caller) {
                        retPerson.addAll(father.family.getRelatives(new LinkedBlockingQueue<>(path), me));
                    }
                    if (mother != null && mother != caller) {
                        retPerson.addAll(mother.family.getRelatives(new LinkedBlockingQueue<>(path), me));
                    }
                } else if (RelationshipType.MARITAL.equals(type)) {
                    if (spouse != null && spouse != caller) {
                        retPerson.addAll(spouse.family.getRelatives(new LinkedBlockingQueue<>(path), me));
                    }
                } else {
                    for (PersonImpl child : children) {
                        if (child != caller) {
                            retPerson.addAll(child.family.getRelatives(new LinkedBlockingQueue<>(path), me));
                        }
                    }
                }
                return retPerson;
            }
        }

        void AddSpouse(PersonImpl spouse) throws DoubleSpouseException, DoubleParentException {
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
            for (PersonImpl child : me.family.children) {
                if (spouse.isMale() ? child.family.father != spouse : child.family.mother != spouse) {
                    child.addParent(spouse);
                }
                if (me.isMale() ? child.family.father != me : child.family.mother != me) {
                    child.addParent(me);
                }
            }
        }

        private void addRelative(RootRelative curPerson, PersonImpl relative, RelationshipType type, Set<PersonImpl> discovered, Queue<RootRelative> peopleToCheck) {
            discovered.add(relative);
            RootRelative relativeInfo = new RootRelative(relative, new Vector<>(curPerson.path));
            relativeInfo.path.add(type);
            peopleToCheck.add(relativeInfo);
        }

        // Breadth-first traversal on the family graph given by following
        // the links to parents/spouse/children.
        void traverse(FamilyParser parser) {
            parser.setRoot(me);
            Queue<RootRelative> peopleToCheck = new LinkedList<>();
            Set<PersonImpl> discovered = new HashSet<>();
            peopleToCheck.add(new RootRelative(me, new Vector<>()));
            discovered.add(me);
            while (!peopleToCheck.isEmpty()) {
                RootRelative curPerson = peopleToCheck.remove();
                // Add appropriate relationship from root to me
                // Only for non-immediate family, since immediate family
                // gets handled in the general case (i.e. for everyone).
                if (curPerson.person != me && curPerson.path.size() > 1) {
                    parser.addDistantRelationship(curPerson.person, curPerson.path);
                }

                PersonImpl curMother = curPerson.person.family.mother;
                if (curMother != null) {
                    if (!discovered.contains(curMother)) {
                        addRelative(curPerson, curMother, RelationshipType.PARENTAL, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, curMother, RelationshipType.PARENTAL);
                }

                PersonImpl curFather = curPerson.person.family.father;
                if (curFather != null) {
                    if (!discovered.contains(curFather)) {
                        addRelative(curPerson, curFather, RelationshipType.PARENTAL, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, curFather, RelationshipType.PARENTAL);
                }

                PersonImpl curSpouse = curPerson.person.family.spouse;
                if (curSpouse != null) {
                    if (!discovered.contains(curSpouse)) {
                        addRelative(curPerson, curSpouse, RelationshipType.MARITAL, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, curSpouse, RelationshipType.MARITAL);
                }

                for (PersonImpl child : curPerson.person.family.children) {
                    if (!discovered.contains(child)) {
                        addRelative(curPerson, child, RelationshipType.CHILD, discovered, peopleToCheck);
                    }
                    parser.addDirectRelationship(curPerson.person, child, RelationshipType.CHILD);
                }
            }
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

        boolean HasChild(PersonImpl child) {
            return children.contains(child);
        }

        int NumChildren() {
            return children.size();
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

    boolean hasChild(PersonImpl child) {
        return family.HasChild(child);
    }

    int numChildren() {
        return family.NumChildren();
    }
    // END - For testing basic functionality

}
