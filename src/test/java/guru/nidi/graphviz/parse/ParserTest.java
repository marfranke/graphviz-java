/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.parse;

import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.MutableNode;
import org.junit.Test;

import java.io.IOException;

import static guru.nidi.graphviz.model.Compass.NORTH_EAST;
import static guru.nidi.graphviz.model.Compass.SOUTH_WEST;
import static guru.nidi.graphviz.model.Factory.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ParserTest {
    @Test
    public void emptyGraph() throws IOException {
        assertEquals(mutGraph("bla"),
                Parser.read("graph bla{}"));
    }

    @Test
    public void emptyStrictDigraph() throws IOException {
        assertEquals(mutGraph(Label.html("bla")).setStrict().setDirected(),
                Parser.read("strict digraph <bla>{}"));
    }

    @Test
    public void attributesGraph() throws IOException {
        assertEquals(mutGraph()
                        .getGeneralAttrs().addAttr("x", "y")
                        .getGraphAttrs().addAttr("a", "b")
                        .getNodeAttrs().addAttr("c", "d")
                        .getLinkAttrs().addAttr("e", "f", "g", "h", "i", "j"),
                Parser.read("graph { x=y; graph [a=b]; node[c=d] edge[e=f,g=h][i=j] }"));
    }

    @Test
    public void nodes() throws IOException {
        assertEquals(mutGraph().addNodes("simple").add(node("attr").attr("a", "b")),
                Parser.read("graph { simple attr[\"a\"=b]}")); //TODO with port? "d:1 full:1:ne
    }

    @Test
    public void links() throws IOException {
        final MutableNode
                simple = mutNode("simple"),
                d = mutNode("d"),
                full = mutNode("full");
        assertEquals(mutGraph().addNodes(
                simple.addLink(to(d.withCompass(SOUTH_WEST)).attr("a", "b")),
                d.addLink(between(loc(SOUTH_WEST), full.withRecord("2").setCompass(NORTH_EAST)).attr("a", "b"))),
                Parser.read("graph { simple -- d:sw -- full:2:ne [a=b]}"));
    }

    @Test
    public void subgraph() throws IOException {
        assertEquals(mutGraph().addGraphs(
                mutGraph("s").getGeneralAttrs().addAttr("a", "b"),
                mutGraph().getGeneralAttrs().addAttr("c", "d"),
                mutGraph().getGeneralAttrs().addAttr("e", "f")),
                Parser.read("graph { subgraph s { a=b }; subgraph { c=d }; { e=f } }"));
    }

    @Test
    public void leftSubgraphEdge() throws IOException {
        assertEquals(mutGraph().addGraphs(
                mutGraph().addLink(to(mutNode("x")).attr("a", "b")),
                mutGraph().addLink(mutNode("y")),
                mutGraph("a").addLink(mutNode("z"))),
                Parser.read("graph{ {} -- x [a=b]  subgraph{} -- y  subgraph a{} -- z }"));
    }

    @Test
    public void rightSubgraphEdge() throws IOException {
        assertEquals(mutGraph().addNodes(
                mutNode("x").addLink(to(mutGraph()).attr("a", "b")),
                mutNode("y").addLink(mutGraph()),
                mutNode("z").addLink(mutGraph("a"))),
                Parser.read("graph{ x -- {} [a=b]  y -- subgraph{}  z -- subgraph a{} }"));
    }

    @Test
    public void subgraphSubgraphEdge() throws IOException {
        assertEquals(mutGraph().addGraphs(
                mutGraph().addLink(to(mutGraph()).attr("a", "b")),
                mutGraph().addLink(mutGraph()),
                mutGraph().addLink(mutGraph("a"))),
                Parser.read("graph{ {} -- {} [a=b]  {} -- subgraph{}  {} -- subgraph a{} }"));
    }

}