package com.ziminer.familytree.family;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonTest {
    private PersonFactory factory;

    @Before
    public void initialize() {
        factory = new PersonFactory();
    }

    @Test
    public void testCreatePerson() {
        Person teddy = factory.createPerson("Teddy", true);
        assertEquals(teddy.getName(), "Teddy");
    }

    @Test
    public void testSpouse() throws DoubleSpouseException, DoubleParentException {
        Person me = factory.createPerson("Me", true);
        Person spouse = factory.createPerson("Spouse", true);
        me.addSpouse(spouse);
        assertEquals(me.getSpouseName(), spouse.getName());
        assertEquals(spouse.getSpouseName(), me.getName());

        // Now try to add another spouse and make sure that fails.
        Person fakeSpouse = factory.createPerson("Fake Spouse", true);
        try {
            me.addSpouse(fakeSpouse);
            fail(); // addSpouse should throw an exception.
        } catch (DoubleSpouseException e) {
            // We're good
        }
    }

    @Test
    public void testParent() throws DoubleParentException, DoubleSpouseException {
        Person me = factory.createPerson("Me", true);
        Person dad = factory.createPerson("Dad", true);
        Person mom = factory.createPerson("Mom", false);
        me.addParent(dad);
        dad.addSpouse(mom);
        assertEquals(me.getFatherName(), dad.getName());
        assertEquals(me.getMotherName(), mom.getName());
        assertTrue(dad.hasChild(me));
        assertTrue(mom.hasChild(me));
        assertEquals(dad.numChildren(), mom.numChildren());
        assertEquals(dad.numChildren(), 1);

        // Now try to add another mom and dad; make sure it fails.
        Person fakeDad = factory.createPerson("Fake Dad", true);
        Person fakeMom = factory.createPerson("Fake Mom", true);
        try {
            me.addParent(fakeDad);
            fail();
        } catch (DoubleParentException e) {
            // We're good
        }

        try {
            me.addParent(fakeMom);
            fail();
        } catch (DoubleParentException e) {
            // We're good.
        }
    }
}
