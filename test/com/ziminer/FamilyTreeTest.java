package com.ziminer;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Created by ziminer on 2016-03-24.
 */
public class FamilyTreeTest {

    @Test
    public void testCreatePerson() {
        FamilyTree tree = new FamilyTree();
        Person teddy = tree.CreatePerson("Teddy");
        assertEquals(teddy.GetName(), "Teddy");
    }
}
