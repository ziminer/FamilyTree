package com.ziminer.familytree.family;


import java.util.Vector;

/**
 * The FamilyParser receives calls during a family traversal.
 * <p>
 * Implement the interface if you want to do anything fancy (e.g. visualize ;))
 * with the family graph.
 */
public interface FamilyParser {
    /**
     * This will be the person from whom the relationship graph starts.
     *
     * Called first during a family traversal.
     *
     * @param rootPerson
     */
    void setRoot(Person rootPerson);

    /**
     * Add a distant relationship from the root to a person.
     * <p>
     * Called the first time that person is visited during the traversal.
     *
     * @param to
     * @param path
     */
    void addDistantRelationship(Person to, Vector<RelationshipType> path);

    /**
     * Add a direct relationship between two people in a graph. "from" may or may not
     * be the root. For people <p1, p2> who are mutually related to each other there will be
     * two calls - <p1, p2> and <p2, p1>.
     *
     * @param from
     * @param to
     * @param type
     */
    void addDirectRelationship(Person from, Person to, RelationshipType type);
}
