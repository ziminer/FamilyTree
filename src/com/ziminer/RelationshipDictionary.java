package com.ziminer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class RelationshipDictionary {

    // A basic dictionary with the most common types of relationships.
    private static RelationshipDictionary basic = null;

    // Not thread-safe, but that's okay in the context of this program.
    public static RelationshipDictionary getBasic() {
        if (basic == null) {
            basic = new RelationshipDictionary();
            basic.DefineRelationship("Husband", "Wife", new Vector<>(Arrays.asList(Type.MARITAL)));
            basic.DefineRelationship("Father", "Mother", new Vector<>(Arrays.asList(Type.PARENTAL)));
            basic.DefineRelationship("Father in Law", "Mother in Law", new Vector<>(Arrays.asList(Type.MARITAL, Type.PARENTAL)));
            basic.DefineRelationship("Grandfather", "Grandmather", new Vector<>(Arrays.asList(Type.PARENTAL, Type.PARENTAL)));
            basic.DefineRelationship("Son", "Daughter", new Vector<>(Arrays.asList(Type.CHILD)));
            basic.DefineRelationship("Grandson", "Granddaughter", new Vector<>(Arrays.asList(Type.CHILD, Type.CHILD)));
            basic.DefineRelationship("Uncle", "Aunt", new Vector<>(Arrays.asList(Type.PARENTAL, Type.PARENTAL, Type.CHILD)));
            basic.DefineRelationship("Brother in Law", "Sister in Law", new Vector<>(Arrays.asList(Type.MARITAL, Type.PARENTAL, Type.CHILD)));
            basic.DefineRelationship("Brother in Law", "Sister in Law", new Vector<>(Arrays.asList(Type.PARENTAL, Type.CHILD, Type.MARITAL)));
            basic.DefineRelationship("Brother", "Sister", new Vector<>(Arrays.asList(Type.PARENTAL, Type.CHILD)));
            basic.DefineRelationship("Nephew", "Niece", new Vector<>(Arrays.asList(Type.PARENTAL, Type.CHILD, Type.CHILD)));
        }
        return basic;
    }

    private class Node {
        String maleName;
        String femaleName;
        final Map<Type, Node> childNodes;

        Node() {
            this.maleName = "";
            this.femaleName = "";
            this.childNodes = new HashMap<>();
        }
    }

    private final Node root;

    enum Type {
        NONE, PARENTAL, MARITAL, CHILD
    }

    public RelationshipDictionary() {
        root = new Node();

    }

    /**
     * Define a new relationship for a path of relationship types.
     * <p>
     * For example, DefineRelationship("Grandfather", {PARENTAL, PARENTAL})
     *
     * @param maleName
     * @param femaleName
     * @param path
     */
    public void DefineRelationship(String maleName, String femaleName, Vector<Type> path) {
        if (path.size() == 0) {
            return;
        }
        Node curNode = root;
        for (Type type : path) {
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

    public String GetRelationship(Vector<Type> path, boolean male) {
        if (path == null) {
            return "";
        }
        Node curNode = root;
        for (Type type : path) {
            curNode = curNode.childNodes.get(type);
            if (curNode == null) {
                return "";
            }
        }
        return male ? curNode.maleName : curNode.femaleName;
    }
}
