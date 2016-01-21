/*******************************************************************************
 * Copyright 2015 Friedrich-Schiller-Universität Jena
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Stephan Druskat - initial API and implementation
 *******************************************************************************/
package org.corpus_tools.atomic.internal.projects;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;
import org.apache.commons.lang3.tuple.Pair;
import org.corpus_tools.atomic.internal.projects.OLDDefaultAtomicProjectData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link OLDDefaultAtomicProjectData}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class DefaultAtomicProjectDataTest {
	
	private OLDDefaultAtomicProjectData fixture = null;

	/**
	 * Set fixture.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new OLDDefaultAtomicProjectData("Test"));
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.OLDDefaultAtomicProjectData#createDocumentAndAddToCorpus(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateDocumentAndAddToCorpus() {
		// Empty corpus list
		assertEquals(0, (getFixture().getCorpora().size()));

		// Add one document to corpus 1
		getFixture().createDocumentAndAddToCorpus("test-corpus", "test-document", "test-source-text");
		assertEquals(1, (getFixture().getCorpora().size()));
		assertNotNull(getFixture().getCorpora().get("test-corpus"));
		assertEquals(1, getFixture().getCorpora().get("test-corpus").size());
		assertEquals("test-document", getFixture().getCorpora().get("test-corpus").iterator().next().getKey());
		assertEquals("test-source-text", getFixture().getCorpora().get("test-corpus").iterator().next().getValue());

		// Add second document to corpus 1
		getFixture().createDocumentAndAddToCorpus("test-corpus", "test-document-two", "test-source-text-two");
		assertEquals(1, (getFixture().getCorpora().size()));
		assertNotNull(getFixture().getCorpora().get("test-corpus"));
		assertEquals(2, getFixture().getCorpora().get("test-corpus").size());
		boolean corpusContainsDocument = false;
		for (Pair<String, String> document : getFixture().getCorpora().get("test-corpus")) {
			if (document.getKey().equals("test-document-two")) {
				corpusContainsDocument = true;
				assertEquals("test-source-text-two", document.getValue());
			}
		}
		assertTrue(corpusContainsDocument);
		
		// Add one document to corpus 2
		getFixture().createDocumentAndAddToCorpus("test-corpus-2", "test-document", "test-source-text");
		assertEquals(2, (getFixture().getCorpora().size()));
		assertNotNull(getFixture().getCorpora().get("test-corpus-2"));
		assertEquals(1, getFixture().getCorpora().get("test-corpus-2").size());
		assertEquals("test-document", getFixture().getCorpora().get("test-corpus-2").iterator().next().getKey());
		assertEquals("test-source-text", getFixture().getCorpora().get("test-corpus-2").iterator().next().getValue());

		// Add second document to corpus 2
		getFixture().createDocumentAndAddToCorpus("test-corpus-2", "test-document-two", "test-source-text-two");
		assertEquals(2, (getFixture().getCorpora().size()));
		assertNotNull(getFixture().getCorpora().get("test-corpus-2"));
		assertEquals(2, getFixture().getCorpora().get("test-corpus-2").size());
		boolean corpus2ContainsDocument = false;
		for (Pair<String, String> document : getFixture().getCorpora().get("test-corpus-2")) {
			if (document.getKey().equals("test-document-two")) {
				corpus2ContainsDocument = true;
				assertEquals("test-source-text-two", document.getValue());
			}
		}
		assertTrue(corpus2ContainsDocument);
		
		// Check what happens when a corpus is added for a second time: New doc gets added
		getFixture().createDocumentAndAddToCorpus("test-corpus", "test-document-new", "test-source-text-new");
		assertEquals(2, getFixture().getCorpora().size());
		assertNotNull(getFixture().getCorpora().get("test-corpus"));
		assertEquals(3, getFixture().getCorpora().get("test-corpus").size());
		boolean corpusContainsNewDocument = false;
		for (Pair<String, String> document : getFixture().getCorpora().get("test-corpus")) {
			if (document.getKey().equals("test-document-new")) {
				corpusContainsNewDocument = true;
				assertEquals("test-source-text-new", document.getValue());
			}
		}
		assertTrue(corpusContainsNewDocument);

		// Check what happens when a document is added to a corpus for a second time: Document gets replaced!
		getFixture().createDocumentAndAddToCorpus("test-corpus", "test-document", "test-source-text-replacement");
		assertEquals(3, getFixture().getCorpora().get("test-corpus").size());
		boolean corpusContainsDocumentWithNewSourceText = false;
		for (Pair<String, String> document : getFixture().getCorpora().get("test-corpus")) {
			if (document.getKey().equals("test-document")) {
				corpusContainsDocumentWithNewSourceText = true;
				assertEquals("test-source-text-replacement", document.getValue());
			}
		}
		assertTrue(corpusContainsDocumentWithNewSourceText);
		
		// Try to add the same document again: Should see no changes
		getFixture().createDocumentAndAddToCorpus("test-corpus", "test-document", "test-source-text-replacement");
		assertEquals(3, getFixture().getCorpora().get("test-corpus").size());
		boolean corpusContainsReplacementDocumentWithNewSourceText = false;
		for (Pair<String, String> document : getFixture().getCorpora().get("test-corpus")) {
			if (document.getKey().equals("test-document")) {
				corpusContainsReplacementDocumentWithNewSourceText = true;
				assertEquals("test-source-text-replacement", document.getValue());
			}
		}
		assertTrue(corpusContainsReplacementDocumentWithNewSourceText);
	}
	
	@Test
	public void testDocumentInsertionOrder() {
		setFixture(new OLDDefaultAtomicProjectData("Test 2"));
		getFixture().createDocumentAndAddToCorpus("c", "d1", "1");
		getFixture().createDocumentAndAddToCorpus("c", "d2", "2");
		getFixture().createDocumentAndAddToCorpus("c", "d3", "3");
		getFixture().createDocumentAndAddToCorpus("c", "d4", "4");
		getFixture().createDocumentAndAddToCorpus("c", "d5", "5");
		LinkedHashSet<Pair<String, String>> corpus = getFixture().getCorpora().get("c");
		assertNotNull(corpus);
		int i = 0;
		for (Pair<String, String> d : corpus) {
			i++;
			assertEquals(String.valueOf(i), d.getRight());
		}
		getFixture().createDocumentAndAddToCorpus("c", "d4", "4");
		getFixture().createDocumentAndAddToCorpus("c", "d2", "2");
		getFixture().createDocumentAndAddToCorpus("c", "d3", "3");
		getFixture().createDocumentAndAddToCorpus("c", "d4", "4");
		i = 0;
		for (Pair<String, String> d : corpus) {
			i++;
			assertEquals(String.valueOf(i), d.getRight());
		}
	}
	
	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.OLDDefaultAtomicProjectData#getProjectName()}.
	 */
	@Test
	public void testGetProjectName() {
		assertEquals("Test", getFixture().getProjectName());
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.internal.projects.OLDDefaultAtomicProjectData#getCorpora()}.
	 */
	@Test
	public void testGetCorpora() {
		assertNotNull(getFixture().getCorpora());
	}

	/**
	 * @return the fixture
	 */
	private OLDDefaultAtomicProjectData getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private void setFixture(OLDDefaultAtomicProjectData fixture) {
		this.fixture = fixture;
	}

}
