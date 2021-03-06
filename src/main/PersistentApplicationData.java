/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author TitarX
 */
public class PersistentApplicationData
{

    private static String schemaForSettingsXml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            +"<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" xmlns:XsdValidation=\"http://webservices.su/XsdValidation\" targetNamespace=\"http://webservices.su/XsdValidation\" elementFormDefault=\"qualified\">"
            +"<element name=\"settings\">"
            +"<complexType>"
            +"<sequence>"
            +"<element name=\"mainForm\" minOccurs=\"1\" maxOccurs=\"1\">"
            +"<complexType>"
            +"<sequence>"
            +"<element name=\"location\" minOccurs=\"1\" maxOccurs=\"1\">"
            +"<complexType>"
            +"<all>"
            +"<element name=\"x\" type=\"int\" minOccurs=\"1\" maxOccurs=\"1\" />"
            +"<element name=\"y\" type=\"int\" minOccurs=\"1\" maxOccurs=\"1\" />"
            +"</all>"
            +"</complexType>"
            +"</element>"
            +"</sequence>"
            +"</complexType>"
            +"</element>"
            +"<element name=\"variables\" minOccurs=\"1\" maxOccurs=\"1\">"
            +"<complexType>"
            +"<all>"
            +"<element name=\"lastFolderXmlFile\" type=\"XsdValidation:folderPathType\" minOccurs=\"1\" maxOccurs=\"1\" />"
            +"<element name=\"lastFolderXsdFile\" type=\"XsdValidation:folderPathType\" minOccurs=\"1\" maxOccurs=\"1\" />"
            +"</all>"
            +"</complexType>"
            +"</element>"
            +"</sequence>"
            +"</complexType>"
            +"</element>"
            +"<simpleType name=\"folderPathType\">"
            +"<restriction base=\"string\">"
            +"<pattern value=\"(([a-zA-Z]:)?([/\\\\][^/]+)+)|()\" />"
            +"</restriction>"
            +"</simpleType>"
            +"</schema>";

    public static String getSchemaForSettingsXml()
    {
        return schemaForSettingsXml;
    }
}
