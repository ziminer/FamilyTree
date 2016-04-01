package com.ziminer.familytree;

import com.ziminer.familytree.family.*;
import com.ziminer.familytree.visualize.FamilyVisualizer;

import java.util.List;

public class Main {

    public static void main(String[] args) throws DoubleParentException, DoubleSpouseException, ExistingOppositeGenderException {
        /*
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
        */

        PersonFactory factory = new PersonFactory();
        Person me = factory.createMale("Me");
        Person brother = factory.createMale("Brother");
        Person dad = factory.createMale("Dad");
        Person grandfather = factory.createMale("Grandfather");
        Person uncle = factory.createMale("Uncle");
        Person aunt = factory.createFemale("Aunt");
        Person cousin = factory.createMale("Cousin");
        Person cousin2 = factory.createFemale("Female Cousin");
        Person uncleCousin = factory.createMale("Uncle's Cousin");

        me.addParent(dad);
        brother.addParent(dad);
        dad.addParent(grandfather);
        aunt.addParent(grandfather);
        uncle.addParent(grandfather);
        cousin.addParent(aunt);
        cousin2.addParent(aunt);
        uncleCousin.addParent(uncle);

        System.out.println(me.getRelatives("Cousin").toString());
        FamilyVisualizer vis = new FamilyVisualizer(me, RelationshipDictionary.getBasic());
        vis.display();
    }
}
