package com.ziminer.familytree;

import com.ziminer.familytree.family.*;
import com.ziminer.familytree.visualize.FamilyVisualizer;

public class Main {

    public static void main(String[] args) throws DoubleParentException, DoubleSpouseException, ExistingOppositeGenderException {
        PersonFactory factory = new PersonFactory();
        Person me = factory.createMale("Me");
        Person spouse = factory.createFemale("Spouse");
        Person son = factory.createMale("Son");
        Person dad = factory.createMale("Dad");
        Person mom = factory.createFemale("Mom");
        Person motherInLaw = factory.createFemale("Mother in Law");
        Person fatherInLaw = factory.createMale("Father in Law");
        Person grandfather = factory.createMale("Grandfather");
        Person aunt = factory.createFemale("Aunt");
        Person sisterInLaw = factory.createFemale("Sister in Law");
        Person brother = factory.createMale("Brother");

        me.addSpouse(spouse);
        me.addParent(dad);
        dad.addSpouse(mom);
        son.addParent(me);
        spouse.addParent(motherInLaw);
        motherInLaw.addSpouse(fatherInLaw);
        dad.addParent(grandfather);
        aunt.addParent(grandfather);
        sisterInLaw.addParent(motherInLaw);
        brother.addParent(dad);

        FamilyVisualizer vis = new FamilyVisualizer(me, RelationshipDictionary.getBasic());
        vis.display();
    }
}
