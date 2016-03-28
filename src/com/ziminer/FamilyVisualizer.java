package com.ziminer;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.Vector;

public class FamilyVisualizer implements FamilyParser {
    private Person root;
    private Graph graph;

    public FamilyVisualizer(Person root) {
        this.root = root;
    }

    public void display() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph = new SingleGraph(String.format("%s's Family", root.GetName()));
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        //graph.addAttribute("ui.stylesheet", "edge {text-alignment: along;}");
        root.TraverseRelatives(this);
        graph.display();
    }

    @Override
    public void setRoot(Person rootPerson) {
        graph.addNode(rootPerson.GetName());
    }

    private void makeNode(Person person) {
        if (graph.getNode(person.GetName()) == null) {
            Node newNode = graph.addNode(person.GetName());
            newNode.addAttribute("ui.label", person.GetName());
        }
    }

    private Edge makeEdge(Person from, Person to) {
        makeNode(from);
        makeNode(to);
        String edgeName = String.format("%s:%s", from.GetName(), to.GetName());
        String revEdgeName = String.format("%s:%s", to.GetName(), from.GetName());
        return graph.getEdge(revEdgeName) == null ? graph.addEdge(edgeName, from.GetName(), to.GetName(), false) : null;
    }

    @Override
    public void addRelationship(Person from, Person to, Vector<RelationshipDictionary.Type> path) {
        if (to == root) {
            return;
        }
        Edge edge = makeEdge(from, to);
        if (edge != null && from == root) {
            String title = RelationshipDictionary.getBasic().GetRelationship(path, to.IsMale());
            edge.addAttribute("ui.label", title);
        }
    }

    @Override
    public void addDirectRelationship(Person from, Person to, RelationshipDictionary.Type type) {
        if (to == root) {
            return;
        }
        Edge edge = makeEdge(from, to);
        if (edge != null && from == root) {
            String title = RelationshipDictionary.getBasic().GetBasicRelationship(type, to.IsMale());
            edge.addAttribute("ui.label", title);
        }
    }
}
