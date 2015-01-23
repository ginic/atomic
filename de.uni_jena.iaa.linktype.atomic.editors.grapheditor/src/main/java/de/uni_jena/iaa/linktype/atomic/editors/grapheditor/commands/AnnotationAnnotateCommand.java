/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.LabelableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationAnnotateCommand extends Command {
	
	public static final int SET_ALL = 0;
	public static final int SET_NAMESPACE_AND_VALUE = 1;
	public static final int SET_NEW_NAMESPACE_FOR_OLD_ANNOTATION = 2;
	public static final int CHANGE_OLD_ANNOTATION_VALUE = 3;
	private String namespace, key, value;
	private SAnnotation model;
	private LabelableElement modelParent;
	private HashMap<SAnnotation, Integer> duplicateAnnotationsToModify;
	
	@Override 
	public void execute() {
		String key = getKey();
		String value = getValue();
		String namespace = getNamespace();
		if (getDuplicateAnnotationsToModify() == null) {
			setAnnotationValues(key, value, namespace);
		}
		else {
			for (Entry<SAnnotation, Integer> entry : getDuplicateAnnotationsToModify().entrySet()) {
				SAnnotation annotation = entry.getKey();
				Integer executionType = entry.getValue();
				switch (executionType) {
				case AnnotationAnnotateCommand.SET_ALL:
					setAnnotationValues(key, value, namespace);
					break;
				case AnnotationAnnotateCommand.SET_NAMESPACE_AND_VALUE:
					annotation.setNamespace(namespace);
					annotation.setValue(value);
					break;
				case AnnotationAnnotateCommand.SET_NEW_NAMESPACE_FOR_OLD_ANNOTATION:
					annotation.setNamespace(namespace);
					break;
				case AnnotationAnnotateCommand.CHANGE_OLD_ANNOTATION_VALUE:
					annotation.setValue(value);
					((LabelableElement) modelParent).removeLabel(model.getNamespace(), model.getName());
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * @param key
	 * @param value
	 * @param namespace
	 */
	public void setAnnotationValues(String key, String value, String namespace) {
		model.setSName(key);
		model.setSValue(value);
		if (namespace != null) {
			model.setNamespace(namespace);
		}
		else {
			model.setNamespace(null);
		}
	}

	public void setModel(SAnnotation model) {
		this.model = model;
	}

	public void setModelParent(LabelableElement labelableElement) {
		this.modelParent = labelableElement;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public void setInputValues(String key, String value, String namespace) {
		setKey(key);
		setValue(value);
		setNamespace(namespace);
	}

	/**
	 * @return the annotationsToModify
	 */
	public HashMap<SAnnotation, Integer> getDuplicateAnnotationsToModify() {
		return duplicateAnnotationsToModify;
	}

	/**
	 * @param annotationsToModify the annotationsToModify to set
	 */
	public void setDuplicateAnnotationsToModify(HashMap<SAnnotation, Integer> annotationsToModify) {
		this.duplicateAnnotationsToModify = annotationsToModify;
	}

}
