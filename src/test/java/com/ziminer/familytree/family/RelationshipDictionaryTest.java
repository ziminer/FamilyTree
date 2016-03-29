package com.ziminer.familytree.family;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Vector;

import static org.junit.Assert.assertEquals;


public class RelationshipDictionaryTest {
    private RelationshipDictionary dict;

    @Before
    public void InitDictionary() {
        dict = new RelationshipDictionary();
        dict.defineRelationship("Father", "Mother", new Vector<>(Arrays.asList(RelationshipType.PARENTAL)));
        dict.defineRelationship("Grandfather", "Grandmather", new Vector<>(Arrays.asList(RelationshipType.PARENTAL, RelationshipType.PARENTAL)));
        dict.defineRelationship("Son", "Daughter", new Vector<>(Arrays.asList(RelationshipType.CHILD)));
        dict.defineRelationship("Grandson", "Granddaughter", new Vector<>(Arrays.asList(RelationshipType.CHILD, RelationshipType.CHILD)));
        dict.defineRelationship("Uncle", "Aunt", new Vector<>(Arrays.asList(RelationshipType.PARENTAL, RelationshipType.PARENTAL, RelationshipType.CHILD)));
        dict.defineRelationship("Brother in Law", "Sister in Law", new Vector<>(Arrays.asList(RelationshipType.MARITAL, RelationshipType.PARENTAL, RelationshipType.CHILD)));
    }

    @Test
    public void TestDictionary() {
        String father = dict.getRelationship(new Vector<>(Arrays.asList(RelationshipType.PARENTAL)), true);
        assertEquals(father, "Father");
        String mother = dict.getRelationship(new Vector<>(Arrays.asList(RelationshipType.PARENTAL)), false);
        assertEquals(mother, "Mother");
        String bil = dict.getRelationship(new Vector<>(Arrays.asList(RelationshipType.MARITAL, RelationshipType.PARENTAL, RelationshipType.CHILD)), true);
        assertEquals(bil, "Brother in Law");
        String none = dict.getRelationship(new Vector<>(Arrays.asList(RelationshipType.MARITAL, RelationshipType.MARITAL)), true);
        assertEquals(none, "");
        assertEquals(dict.getRelationship(null, true), "");
        assertEquals(dict.getRelationship(new Vector<>(), true), "");
    }
}
