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
    public void testCreatePerson() throws ExistingOppositeGenderException {
        Person teddy = factory.createMale("Teddy");
        assertEquals(teddy.getName(), "Teddy");
    }

    @Test
    public void testSpouse() throws DoubleSpouseException, DoubleParentException, ExistingOppositeGenderException {
        PersonImpl me = (PersonImpl) factory.createMale("Me");
        PersonImpl spouse = (PersonImpl) factory.createFemale("Spouse");
        me.addSpouse(spouse);
        assertEquals(me.getSpouseName(), spouse.getName());
        assertEquals(spouse.getSpouseName(), me.getName());

        // Now try to add another spouse and make sure that fails.
        PersonImpl fakeSpouse = (PersonImpl) factory.createFemale("Fake Spouse");
        try {
            me.addSpouse(fakeSpouse);
            fail(); // addSpouse should throw an exception.
        } catch (DoubleSpouseException e) {
            // We're good
        }
    }

    @Test
    public void testParent() throws DoubleParentException, DoubleSpouseException, ExistingOppositeGenderException {
        PersonImpl me = (PersonImpl) factory.createMale("Me");
        PersonImpl dad = (PersonImpl) factory.createMale("Dad");
        PersonImpl mom = (PersonImpl) factory.createFemale("Mom");
        me.addParent(dad);
        dad.addSpouse(mom);
        assertEquals(me.getFatherName(), dad.getName());
        assertEquals(me.getMotherName(), mom.getName());
        assertTrue(dad.hasChild(me));
        assertTrue(mom.hasChild(me));
        assertEquals(dad.numChildren(), mom.numChildren());
        assertEquals(dad.numChildren(), 1);

        // Now try to add another mom and dad; make sure it fails.
        PersonImpl fakeDad = (PersonImpl) factory.createMale("Fake Dad");
        PersonImpl fakeMom = (PersonImpl) factory.createFemale("Fake Mom");
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
