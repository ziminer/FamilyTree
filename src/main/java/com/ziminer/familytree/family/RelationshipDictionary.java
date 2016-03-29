package com.ziminer.familytree.family;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * The RelationshipDictionary maps relationship paths in a tree
 * to their associated gender-specific titles.
 */
public class RelationshipDictionary {

    // A basic dictionary with the most common types of relationships.
    private static RelationshipDictionary basic = null;

    // Not thread-safe, but that's okay in the context of this program.
    public static RelationshipDictionary getBasic() {
        if (basic == null) {
            basic = new RelationshipDictionary();
            basic.defineRelationship("Husband", "Wife", new Vector<>(Arrays.asList(RelationshipType.MARITAL)));
            basic.defineRelationship("Father", "Mother", new Vector<>(Arrays.asList(RelationshipType.PARENTAL)));
            basic.defineRelationship("Father in Law", "Mother in Law", new Vector<>(Arrays.asList(RelationshipType.MARITAL, RelationshipType.PARENTAL)));
            basic.defineRelationship("Grandfather", "Grandmather", new Vector<>(Arrays.asList(RelationshipType.PARENTAL, RelationshipType.PARENTAL)));
            basic.defineRelationship("Son", "Daughter", new Vector<>(Arrays.asList(RelationshipType.CHILD)));
            basic.defineRelationship("Grandson", "Granddaughter", new Vector<>(Arrays.asList(RelationshipType.CHILD, RelationshipType.CHILD)));
            basic.defineRelationship("Uncle", "Aunt", new Vector<>(Arrays.asList(RelationshipType.PARENTAL, RelationshipType.PARENTAL, RelationshipType.CHILD)));
            basic.defineRelationship("Brother in Law", "Sister in Law", new Vector<>(Arrays.asList(RelationshipType.MARITAL, RelationshipType.PARENTAL, RelationshipType.CHILD)));
            basic.defineRelationship("Brother in Law", "Sister in Law", new Vector<>(Arrays.asList(RelationshipType.PARENTAL, RelationshipType.CHILD, RelationshipType.MARITAL)));
            basic.defineRelationship("Brother", "Sister", new Vector<>(Arrays.asList(RelationshipType.PARENTAL, RelationshipType.CHILD)));
            basic.defineRelationship("Nephew", "Niece", new Vector<>(Arrays.asList(RelationshipType.PARENTAL, RelationshipType.CHILD, RelationshipType.CHILD)));
        }
        return basic;
    }

    /**
     * Internally a trie/suffix tree structure, where valid relationship nodes
     * have a maleName and femaleName.
     */
    private class Node {
        String maleName;
        String femaleName;
        final Map<RelationshipType, Node> childNodes;

        Node() {
            this.maleName = "";
            this.femaleName = "";
            this.childNodes = new HashMap<>();
        }
    }

    private final Node root;

    public RelationshipDictionary() {
        root = new Node();

    }

    /**
     * Define a new relationship for a path of relationship types.
     * <p>
     * For example, defineRelationship("Grandfather", {PARENTAL, PARENTAL})
     *
     * @param maleName
     * @param femaleName
     * @param path
     */
    public void defineRelationship(String maleName, String femaleName, Vector<RelationshipType> path) {
        if (path.size() == 0) {
            return;
        }
        Node curNode = root;
        for (RelationshipType type : path) {
            Node childNode = curNode.childNodes.get(type);
            if (childNode == null) {
                childNode = new Node();
                curNode.childNodes.put(type, childNode);
            }
            curNode = childNode;
        }
        curNode.maleName = maleName;
        curNode.femaleName = femaleName;
    }

    public String getBasicRelationship(RelationshipType type, boolean male) {
        Node target = root.childNodes.get(type);
        if (target == null) {
            return "";
        }
        return male ? target.maleName : target.femaleName;
    }

    public String getRelationship(Vector<RelationshipType> path, boolean male) {
        if (path == null) {
            return "";
        }
        Node curNode = root;
        for (RelationshipType type : path) {
            curNode = curNode.childNodes.get(type);
            if (curNode == null) {
                return "";
            }
        }
        return male ? curNode.maleName : curNode.femaleName;
    }
}
