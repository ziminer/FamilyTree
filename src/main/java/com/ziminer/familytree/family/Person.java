package com.ziminer.familytree.family;

/**
 * A person is a node on the family tree.
 * <p>
 * All operations on the (implicit) family tree are done via
 * operations on the people in the tree.
 */
public interface Person {

    String getName();

    boolean isMale();

    /**
     * Add the specified person as this person's spouse. Also make all children of this person and spouse common
     * children. There is no concept of step-parents.
     * <p>
     * A person can only have one spouse. There are no explicit gender restrictions on spouses,
     * but children can only have one male and female parent respectively.
     *
     * @param spouse
     * @throws DoubleSpouseException If either spouse or this person already have a spouse.
     * @throws DoubleParentException If either spouse's children or this person's children already have two parents
     *                               different from spouse and person.
     */
    void addSpouse(Person spouse) throws DoubleSpouseException, DoubleParentException;

    /**
     * Add the specified person as this person's parent. If the parent has a spouse, add the person as the spouse's
     * child as well.
     * <p>
     * A person can only have one parent of each gender.
     *
     * @param parent
     * @throws DoubleParentException If there is already a parent of the same gender that's not equal to parent.
     */
    void addParent(Person parent) throws DoubleParentException;

    java.util.List<Person> getRelatives(String title);

    /**
     * Traverse the person's family tree. Fire all the relationship-establishing calls on the FamilyParser.
     *
     * @param parser
     */
    void traverseRelatives(FamilyParser parser);
}
