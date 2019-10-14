package org.cyk.utility.client.controller;

public interface Constant {
	
	String CYK_CONTEXT_PARAMETER_NAME_FORMAT = "cyk.context.parameter.%s";
	
	static String formatCykContextParameterName(String string) {
		return String.format(CYK_CONTEXT_PARAMETER_NAME_FORMAT, string);
	}
	
	String FILE_URL_PATTERN_PREFIX = "/private/__file__/";
	String FILE_URL_PATTERN = FILE_URL_PATTERN_PREFIX + "*";
	
	String CONTEXT_PARAMETER_NAME_THEME_CLASS_NAME = formatCykContextParameterName("theme.class.name");
	String CONTEXT_PARAMETER_NAME_THEME_IDENTIFIER = formatCykContextParameterName("theme.identifier");
	String CONTEXT_PARAMETER_NAME_THEME_PRIMEFACES = formatCykContextParameterName("theme.primefaces");
	
	/* Favicon*/
	
	String CONTEXT_PARAMETER_NAME_THEME_FAVICON_FILE_RESOURCES_FOLDER = formatCykContextParameterName("theme.favicon.file.resources.folder");
	String CONTEXT_PARAMETER_NAME_THEME_FAVICON_FILE_FOLDER = formatCykContextParameterName("theme.favicon.file.folder");
	String CONTEXT_PARAMETER_NAME_THEME_FAVICON_FILE_NAME_PREFIX = formatCykContextParameterName("theme.favicon.file.name.prefix");
	String CONTEXT_PARAMETER_NAME_THEME_FAVICON_FILE_NAME_EXTENSION = formatCykContextParameterName("theme.favicon.file.name.extension");
	
	/* Logo*/
	
	String CONTEXT_PARAMETER_NAME_THEME_LOGO_FILE_RESOURCES_FOLDER = formatCykContextParameterName("theme.logo.file.resources.folder");
	String CONTEXT_PARAMETER_NAME_THEME_LOGO_FILE_FOLDER = formatCykContextParameterName("theme.logo.file.folder");
	String CONTEXT_PARAMETER_NAME_THEME_LOGO_FILE_NAME = formatCykContextParameterName("theme.logo.file.name");
	String CONTEXT_PARAMETER_NAME_THEME_LOGO_FILE_NAME_PREFIX = formatCykContextParameterName("theme.logo.file.name.prefix");
	String CONTEXT_PARAMETER_NAME_THEME_LOGO_FILE_NAME_EXTENSION = formatCykContextParameterName("theme.logo.file.name.extension");
	
}
