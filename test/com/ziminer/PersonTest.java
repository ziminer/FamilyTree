package com.ziminer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonTest {

    @Before
    public void initialize() {
        PersonFactory.refresh();
    }

    @Test
    public void testCreatePerson() {
        Person teddy = PersonFactory.createPerson("Teddy", true);
        assertEquals(teddy.GetName(), "Teddy");
    }

    @Test
    public void testSpouse() throws DoubleSpouseException, DoubleParentException {
        Person me = PersonFactory.createPerson("Me", true);
        Person spouse = PersonFactory.createPerson("Spouse", true);
        me.AddSpouse(spouse);
        assertEquals(me.GetSpouseName(), spouse.GetName());
        assertEquals(spouse.GetSpouseName(), me.GetName());

        // Now try to add another spouse and make sure that fails.
        Person fakeSpouse = PersonFactory.createPerson("Fake Spouse", true);
        try {
            me.AddSpouse(fakeSpouse);
            fail(); // AddSpouse should throw an exception.
        } catch (DoubleSpouseException e) {
            // We're good
        }
    }

    @Test
    public void testParent() throws DoubleParentException, DoubleSpouseException {
        Person me = PersonFactory.createPerson("Me", true);
        Person dad = PersonFactory.createPerson("Dad", true);
        Person mom = PersonFactory.createPerson("Mom", false);
        me.AddParent(dad);
        dad.AddSpouse(mom);
        assertEquals(me.GetFatherName(), dad.GetName());
        assertEquals(me.GetMotherName(), mom.GetName());
        assertTrue(dad.HasChild(me));
        assertTrue(mom.HasChild(me));
        assertEquals(dad.NumChildren(), mom.NumChildren());
        assertEquals(dad.NumChildren(), 1);

        // Now try to add another mom and dad; make sure it fails.
        Person fakeDad = PersonFactory.createPerson("Fake Dad", true);
        Person fakeMom = PersonFactory.createPerson("Fake Mom", true);
        try {
            me.AddParent(fakeDad);
            fail();
        } catch (DoubleParentException e) {
            // We're good
        }

        try {
            me.AddParent(fakeMom);
            fail();
        } catch (DoubleParentException e) {
            // We're good.
        }
    }

    @Test
    public void testRelatives() throws DoubleParentException, DoubleSpouseException {
        /*
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

        for (int i = 0; i < people.size(); ++i) {
            Person p1 = people.get(i);
            Map<Person, Person.RelativeInfo> p1Relatives = p1.GetRelatives();
            System.out.println(String.format("%s : %s", p1.GetName(), p1Relatives));
            for (int j = i + 1; j < people.size(); ++j) {
                Person p2 = people.get(j);
                Map<Person, Person.RelativeInfo> p2Relatives = p2.GetRelatives();
                // Within one family, should be related.
                assertEquals(p1Relatives.size(), p2Relatives.size());
            }
        }
        */
    }
}
