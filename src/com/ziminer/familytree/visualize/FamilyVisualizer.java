package com.ziminer.familytree.visualize;

import com.ziminer.familytree.family.FamilyParser;
import com.ziminer.familytree.family.Person;
import com.ziminer.familytree.family.RelationshipDictionary;
import com.ziminer.familytree.family.RelationshipType;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.nio.file.Paths;
import java.util.Vector;

public class FamilyVisualizer implements FamilyParser {
    private final Person root;
    private final RelationshipDictionary dict;
    private Graph graph;

    public FamilyVisualizer(Person root, RelationshipDictionary dict) {
        this.root = root;
        this.dict = dict;
    }

    public void display() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph = new SingleGraph(String.format("%s's Family", root.getName()));
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", String.format("url(file:///%s)", Paths.get("ui.css").toAbsolutePath()));
        root.traverseRelatives(this);
        graph.display();
    }

    private void addElementClass(Element element, String className) {
        String curClassStr = element.getAttribute("ui.class");
        element.setAttribute("ui.class", curClassStr == null ? className : String.format("%s, %s", curClassStr, className));
    }

    @Override
    public void setRoot(Person rootPerson) {
        Node rootNode = makeNode(rootPerson);
        addElementClass(rootNode, "root");
    }

    private Node makeNode(Person person) {
        if (graph.getNode(person.getName()) == null) {
            Node newNode = graph.addNode(person.getName());
            newNode.addAttribute("ui.label", person.getName());
            addElementClass(newNode, person.isMale() ? "male" : "female");
            return newNode;
        }
        return null;
    }

    private Edge makeEdge(Person from, Person to) {
        makeNode(from);
        makeNode(to);
        String edgeName = String.format("%s:%s", from.getName(), to.getName());
        String revEdgeName = String.format("%s:%s", to.getName(), from.getName());
        return graph.getEdge(revEdgeName) == null ? graph.addEdge(edgeName, from.getName(), to.getName(), true) : null;
    }

    @Override
    public void addDistantRelationship(Person to, Vector<RelationshipType> path) {
        if (to == root) {
            return;
        }
        Edge edge = makeEdge(root, to);
        if (edge != null) {
            String title = dict.getRelationship(path, to.isMale());
            edge.addAttribute("ui.label", title);
            edge.addAttribute("ui.class", "distant");
        }
    }

    @Override
    public void addDirectRelationship(Person from, Person to, RelationshipType type) {
        if (to == root) {
            return;
        }
        Edge edge = makeEdge(from, to);
        if (edge == null) {
            return;
        }
        // Rely on having appropriately named classes defined in the stylesheet.
        addElementClass(edge, type.name().toLowerCase());
        if (from == root) {
            String title = RelationshipDictionary.getBasic().getBasicRelationship(type, to.isMale());
            edge.addAttribute("ui.label", title);
        }
    }
}
