package com.rsicms.pluginUtilities.types;

import java.util.ArrayList;
import java.util.List;

import com.reallysi.rsuite.api.CriteriaType;
import com.reallysi.rsuite.api.ElementMatchingCriteria;
import com.reallysi.rsuite.api.ElementMatchingOptions;
import com.reallysi.rsuite.api.LayeredMetadataDefinition;
import com.reallysi.rsuite.api.ManagedObject;
import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.User;
import com.reallysi.rsuite.api.remoteapi.RemoteApiExecutionContext;

/**
 * Defines the criteria for matching element types. Used
 * to select element types, e.g., for assigning
 * metadata field definitions.
 */

public class ElementTypeMatchingCriteria implements ElementMatchingCriteria
{
  private CriteriaType criteriaType;
  private String namespaceUri;
  private String localName;
  
  /**
   * 
   */  
  private ElementTypeMatchingCriteria(
    CriteriaType criteriaType,
    String namespaceUri,
    String localName)
  {
    this.namespaceUri = namespaceUri;
    this.localName = localName;
  }
  
  /**
   * Creates an criteria that matches on element type (tagname
   * and namespace).
   * @param namespaceUri The namespace name of the element type to match
   * @param localName The local name of the element type match
   * @return The matching criteria instance.
   */
  public static ElementTypeMatchingCriteria createForElementType(
    String namespaceUri, 
    String localName)
  {
    return new ElementTypeMatchingCriteria
                 (CriteriaType.ELEMENT_TYPE, namespaceUri, localName);
  }

  /**
   * @return The criteria type for this criteria.
   */
  public CriteriaType getCriteriaType()
  {
    return criteriaType;
  }

  /**
   * @return Namespace name for this criteria.
   */
  public String getNamespaceUri()
  {
    return namespaceUri;
  }

  /**
   * @return The local name for this criteria
   */
  public String getLocalName()
  {
    return localName;
  }

  @Override
  public boolean isAssociatedWithCriteria(
	String namespaceUri, 
	String localName,
	ElementMatchingOptions options) 
  {
      if (getNamespaceUri() == null) {
        if (getLocalName().equals(localName)) {
          return true;
        } else { 
          return false;
        }
      } else {
        if (getNamespaceUri().equals(namespaceUri) && getLocalName().equals(localName)) {
          return true;
        } else {
          return false;
        }
      }      
  }

    /**
    * Adds the specified element to the layered metadata definition.
    * @param context
    * @param def
    * @param elementUri
    * @param elementLocalName
    * @throws RSuiteException
    */
public static void addElementToMetadataDefinition(RemoteApiExecutionContext context, LayeredMetadataDefinition def, String elementUri, String elementLocalName) throws RSuiteException {
      User user = context.getAuthorizationService().getSystemUser();
      ElementMatchingCriteria[] elements = def.getElementCriteria();
      List<ElementMatchingCriteria> updatedElements = new ArrayList<ElementMatchingCriteria>();
      updatedElements.add(ElementTypeMatchingCriteria.createForElementType(elementUri, elementLocalName));
      for (ElementMatchingCriteria e : elements) {
          updatedElements.add(e);
      }
      context.getMetaDataService().setLayeredMetaDataDefinitionElementCriteria(user, def.getName(), updatedElements);
  }


}
