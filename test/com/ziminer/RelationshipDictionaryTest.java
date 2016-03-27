package com.ziminer;

import com.ziminer.RelationshipDictionary.Type;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Vector;

import static org.junit.Assert.assertEquals;

/**
 * Created by ziminer on 2016-03-25.
 */
public class RelationshipDictionaryTest {
    private RelationshipDictionary dict;

    @Before
    public void InitDictionary() {
        dict = new RelationshipDictionary();
        dict.DefineRelationship("Father", "Mother", new Vector<>(Arrays.asList(Type.PARENTAL)));
        dict.DefineRelationship("Grandfather", "Grandmather", new Vector<>(Arrays.asList(Type.PARENTAL, Type.PARENTAL)));
        dict.DefineRelationship("Son", "Daughter", new Vector<>(Arrays.asList(Type.CHILD)));
        dict.DefineRelationship("Grandson", "Granddaughter", new Vector<>(Arrays.asList(Type.CHILD, Type.CHILD)));
        dict.DefineRelationship("Uncle", "Aunt", new Vector<>(Arrays.asList(Type.PARENTAL, Type.PARENTAL, Type.CHILD)));
        dict.DefineRelationship("Brother in Law", "Sister in Law", new Vector<>(Arrays.asList(Type.MARITAL, Type.PARENTAL, Type.CHILD)));
    }

    @Test
    public void TestDictionary() {
        String father = dict.GetRelationship(new Vector<>(Arrays.asList(Type.PARENTAL)), true);
        assertEquals(father, "Father");
        String mother = dict.GetRelationship(new Vector<>(Arrays.asList(Type.PARENTAL)), false);
        assertEquals(mother, "Mother");
        String bil = dict.GetRelationship(new Vector<>(Arrays.asList(Type.MARITAL, Type.PARENTAL, Type.CHILD)), true);
        assertEquals(bil, "Brother in Law");
        String none = dict.GetRelationship(new Vector<>(Arrays.asList(Type.MARITAL, Type.MARITAL)), true);
        assertEquals(none, "");
        assertEquals(dict.GetRelationship(null, true), "");
        assertEquals(dict.GetRelationship(new Vector<>(), true), "");
    }
}
