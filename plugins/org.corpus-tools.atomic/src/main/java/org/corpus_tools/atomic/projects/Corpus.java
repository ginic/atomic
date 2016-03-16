/*******************************************************************************
 * Copyright 2016 Friedrich-Schiller-Universität Jena
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
package org.corpus_tools.atomic.projects;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.corpus_tools.atomic.models.AbstractBean;
import org.eclipse.core.runtime.Assert;

/**
 * JavaBean definition of a corpus, i.e., a node in the
 * corpus structure tree. A corpus can be a root corpus,
 * i.e., the topmost structural element in a project.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class Corpus extends AbstractBean implements ProjectNode {
	
	/**
	 * Property <code>isProjectDataObject</code>, a flag whether this Corpus instance is the root node
	 * in the project structure tree, i.e., "the project". 
	 */
	private boolean isProjectDataObject = false;
	
	/**
	 * Property <code>name</name>, readable and writable.
	 */
	private String name = null;

	/**
	 * Property <code>children</name>, readable and writable.
	 */
	private Set<ProjectNode> children = null;
	
	/**
	 * Default no-arg constructor (JavaBean compliance). 
	 */
	public Corpus() {
		children = new LinkedHashSet<>();
	}
	
	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.ProjectNode#setName(java.lang.String)
	 */
	public void setName(final String name) {
		final String oldName = this.name;
		this.name = name;
		firePropertyChange("name", oldName, this.name);
	}
	
	/**
	 * Returns the child nodes of this corpus.
	 *
	 * @return the corpus' children
	 */
	public Set<ProjectNode> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(final Set<ProjectNode> children) {
		final Set<ProjectNode> oldChildren = this.children;
		this.children = children;
		firePropertyChange("children", oldChildren, this.children);
	}

	/**
	 * Adds a child, i.e., a {@link ProjectNode}, to this
	 * {@link Corpus}. Assert that this 
	 * is not null, and throw a {@link RuntimeException}
	 * if it is. The new child is added to the
	 * {@link List} of children. Returns the added
	 * child {@link ProjectNode}.
	 *
	 * @param the child to add (must not be null)
	 * @return the added child
	 * @throws RuntimeException if child is null
	 */
	public ProjectNode addChild(final ProjectNode child) {
		Assert.isNotNull(child);
		final Set<ProjectNode> newChildren = getChildren();
		newChildren.add(child);
		setChildren(newChildren);
		return child;
	}

	/**
	 * Removes an element from the corpus. The argument is
	 * the name of the {@link ProjectNode} to remove. Must 
	 * assert that this is not null, and throw a {@link RuntimeException}
	 * if it is. 
	 * <p>
	 * Returns the previous node associated with this name
	 * or null if there was no node of this name.
	 *
	 * @param the name of the child to remove (must not be null)
	 * @return the removed node or null
	 * @throws RuntimeException if childName is null
	 */
	public boolean removeChild(final ProjectNode child) {
		Assert.isNotNull(child);
		final Set<ProjectNode> newChildren = getChildren();
		boolean childrenContainedRemovedChild = newChildren.remove(child);
		setChildren(newChildren);
		return childrenContainedRemovedChild;
	}

	/**
	 * @return whether the {@link Corpus} is "the project", i.e., the root node in the project structure tree containing all other elements
	 */
	public boolean isProjectDataObject() {
		return isProjectDataObject;
	}

	/**
	 * @param isProjectDataObject whether the {@link Corpus} is to be "the project", i.e., the root node in the project structure tree containing all other elements
	 */
	public void setProjectDataObject(boolean isProjectDataObject) {
		this.isProjectDataObject = isProjectDataObject;
	}

}