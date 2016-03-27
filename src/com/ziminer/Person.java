package com.ziminer;


import com.ziminer.RelationshipDictionary.Type;

import java.util.*;

public class Person {

    private class RelationshipPath {
        private final Stack<Type> type;
        private final Stack<Person> person;

        RelationshipPath() {
            type = new Stack<>();
            person = new Stack<>();
        }

        void push(Person person, Type type) {
            this.type.push(type);
            this.person.push(person);
        }

        void pop() {
            this.type.pop();
            this.person.pop();
        }

        Type peekType() {
            return this.type.empty() ? Type.NONE : this.type.peek();
        }

        Person peekPerson() {
            return this.person.empty() ? null : this.person.peek();
        }

        Stack<Type> getPath() {
            return type;
        }

        @Override
        public String toString() {
            return String.format("%s\n%s", type, person);
        }
    }

    private class RelativeInfo {
        final String title;
        final Vector<Type> path; // Path from the source person to the relative

        RelativeInfo(String title, Vector<Type> path) {
            this.title = title;
            this.path = new Vector<>(path);
        }

        @Override
        public String toString() {
            return String.format("%s : %s", title, path);
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


        void getRelatives(Map<Person, RelativeInfo> relatives, RelationshipPath path) {
            RelativeInfo existingInfo = relatives.get(me);
            String title = RelationshipDictionary.getBasic().GetRelationship(path.getPath(), IsMale());
            RelativeInfo newInfo = new RelativeInfo(title, path.getPath());
            if (existingInfo == null) {
                relatives.put(me, newInfo);
            } else if (!existingInfo.title.equals(newInfo.title)) {
                // Shouldn't get this.
                System.out.println(String.format("Two paths resulting in different titles:\n%s\n%s", existingInfo, newInfo));
                return;
            } else {
                return;
            }

            Type lastRelationship = path.peekType();
            // No point re-visiting mother/father from a child.
            if (lastRelationship != Type.CHILD) {
                if (mother != null) {
                    path.push(me, Type.PARENTAL);
                    mother.family.getRelatives(relatives, path);
                    path.pop();
                }
                if (father != null) {
                    path.push(me, Type.PARENTAL);
                    father.family.getRelatives(relatives, path);
                    path.pop();
                }
            }

            // Since we don't allow remarriage, mother and father are
            // guaranteed to be married to each other or not at all.
            // Therefore, we'll visit both of them from the child.
            if (lastRelationship != Type.PARENTAL && lastRelationship != Type.MARITAL) {
                if (spouse != null) {
                    path.push(me, Type.MARITAL);
                    spouse.family.getRelatives(relatives, path);
                    path.pop();
                }
            }

            // No point re-visiting the children - we'll visit them
            // from the spousal point of view.
            if (lastRelationship != Type.MARITAL) {
                Person srcChild = lastRelationship == Type.PARENTAL ? path.peekPerson() : null;
                path.push(me, Type.CHILD);
                children.forEach(child -> {
                    if (child != srcChild) {
                        child.family.getRelatives(relatives, path);
                    }
                });
                path.pop();
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

    public Map<Person, String> GetRelatives() {
        Map<Person, RelativeInfo> relatives = new HashMap<>();
        family.getRelatives(relatives, new RelationshipPath());
        relatives.remove(this);

        Map<Person, String> relativeTitles = new HashMap<>();
        for (Map.Entry<Person, RelativeInfo> relative : relatives.entrySet()) {
            String title = relative.getValue().title.equals("") ? "Other" : relative.getValue().title;
            relativeTitles.put(relative.getKey(), title);
        }
        return relativeTitles;
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
