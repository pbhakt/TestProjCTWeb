package com.clicktable.model;

import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import com.clicktable.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author p.singh
 *
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Language extends Entity 
{
    	
	
	/**
     * 
     */
    private static final long serialVersionUID = 3448339996990855322L;
    
        @Required(message=ErrorCodes.LANG_NAME)
        @MaxLength(message=ErrorCodes.LANG_NAME_MAX_LENGTH,value=100)
        private String name;
        @MaxLength(message=ErrorCodes.LANG_CD_MAX_LENGTH,value=10)
    	@GraphProperty(propertyName="language_cd")
	private String languageCode;
    	
    	
    	
    	
	public String getName() {
	    return name;
	}
	public void setName(String name) {
		this.name = name == null?null:name.trim();
	}
	
	public String getLanguageCode() {
	    return languageCode;
	}
	
	public void setLanguageCode(String languageCode) {
		this.languageCode =  languageCode== null?null:languageCode.trim();
	}
	
	

	

}
