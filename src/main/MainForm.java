/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import datareader.xml.xpath.XPathDataReader;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 *
 * @author TitarX
 */
public class MainForm extends javax.swing.JFrame
{

    private String lastFolderXmlFile=System.getProperty("user.home");
    private String lastFolderXsdFile=System.getProperty("user.home");
    private boolean xmlFileReady=false;
    private boolean xsdFileReady=false;

    /**
     * Creates new form MainForm
     */
    public MainForm()
    {
        initComponents();
        initComponents2();
        applySettings();
    }

    private void initComponents2()
    {

        xmlFileTextField.getDocument().addDocumentListener(new DocumentListener()
        {

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                xmlFileReady=checkTextField(xmlFileTextField);
                allowOrDenyStart();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                xmlFileReady=checkTextField(xmlFileTextField);
                allowOrDenyStart();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                xmlFileReady=checkTextField(xmlFileTextField);
                allowOrDenyStart();
            }
        });

        xsdFileTextField.getDocument().addDocumentListener(new DocumentListener()
        {

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                xsdFileReady=checkTextField(xsdFileTextField);
                allowOrDenyStart();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                xsdFileReady=checkTextField(xsdFileTextField);
                allowOrDenyStart();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                xsdFileReady=checkTextField(xsdFileTextField);
                allowOrDenyStart();
            }
        });
    }

    private boolean checkTextField(JTextField textField)
    {
        String filePath=textField.getText().trim();
        File file=new File(filePath);
        if(filePath.matches("^.+\\.(?:(?:xml)|(?:xsd))$")&&file.exists()&&file.isFile())
        {
            if(textField.equals(xmlFileTextField))
            {
                lastFolderXmlFile=file.getParent();
            }
            else if(textField.equals(xsdFileTextField))
            {
                lastFolderXsdFile=file.getParent();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private void allowOrDenyStart()
    {
        if(xmlFileReady&&xsdFileReady)
        {
            startButton.setEnabled(true);
        }
        else
        {
            startButton.setEnabled(false);
        }
    }

    private void applySettings()
    {
        File settingsFile=new File("settings.xml");
        if(settingsFile.exists())
        {
            try
            {
                XPathDataReader xPathDataReader=XPathDataReader.newInstance(settingsFile,PersistentApplicationData.getSchemaForSettingsXml());

                int x=Integer.parseInt(xPathDataReader.readData("/settings/mainForm/location/x/text()"));
                int y=Integer.parseInt(xPathDataReader.readData("/settings/mainForm/location/y/text()"));
                this.setLocation(x,y);

                lastFolderXmlFile=xPathDataReader.readData("/settings/variables/lastFolderXmlFile/text()");
                lastFolderXsdFile=xPathDataReader.readData("/settings/variables/lastFolderXsdFile/text()");
            }
            catch(Exception ex)
            {
                settingsFile.delete();
                this.setLocationRelativeTo(null);
            }
        }
        else
        {
            this.setLocationRelativeTo(null);
        }
    }

    private void selectXmlFile()
    {
        if(!new File(lastFolderXmlFile).exists())
        {
            lastFolderXmlFile=System.getProperty("user.home");
        }

        JFileChooser fileChooser=new JFileChooser(lastFolderXmlFile);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("*.xml","xml"));

        if(fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
        {
            xmlFileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void selectXsdFile()
    {
        if(!new File(lastFolderXsdFile).exists())
        {
            lastFolderXsdFile=System.getProperty("user.home");
        }

        JFileChooser fileChooser=new JFileChooser(lastFolderXsdFile);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("*.xsd","xsd"));

        if(fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
        {
            xsdFileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void startProcess()
    {
        String xmlFilePath=xmlFileTextField.getText().trim();
        String xsdFilePath=xsdFileTextField.getText().trim();
        try
        {
            SchemaFactory schemaFactory=SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema=schemaFactory.newSchema(new File(xsdFilePath));
            Validator validator=schema.newValidator();
            Source source=new StreamSource(new File(xmlFilePath));
            validator.validate(source);

            JOptionPane.showMessageDialog(this,"XML document is valid","Successfully",WIDTH);
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSettings()
    {
        try
        {
            Document xmlDocument=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            Element settingsElement=xmlDocument.createElementNS("http://webservices.su/XsdValidation","settings");
            xmlDocument.appendChild(settingsElement);

            Element mainFormElement=xmlDocument.createElement("mainForm");
            settingsElement.appendChild(mainFormElement);
            Element locationElement=xmlDocument.createElement("location");
            mainFormElement.appendChild(locationElement);
            Element xElemet=xmlDocument.createElement("x");
            locationElement.appendChild(xElemet);
            Text xText=xmlDocument.createTextNode(String.valueOf(this.getLocation().x));
            xElemet.appendChild(xText);
            Element yElemet=xmlDocument.createElement("y");
            locationElement.appendChild(yElemet);
            Text yText=xmlDocument.createTextNode(String.valueOf(this.getLocation().y));
            yElemet.appendChild(yText);

            Element variablesElement=xmlDocument.createElement("variables");
            settingsElement.appendChild(variablesElement);
            Element lastFolderXmlFileElement=xmlDocument.createElement("lastFolderXmlFile");
            variablesElement.appendChild(lastFolderXmlFileElement);
            Text lastFolderXmlFileText=xmlDocument.createTextNode(lastFolderXmlFile);
            lastFolderXmlFileElement.appendChild(lastFolderXmlFileText);
            Element lastFolderXsdFileElement=xmlDocument.createElement("lastFolderXsdFile");
            variablesElement.appendChild(lastFolderXsdFileElement);
            Text lastFolderXsdFileText=xmlDocument.createTextNode(lastFolderXsdFile);
            lastFolderXsdFileElement.appendChild(lastFolderXsdFileText);

            Transformer transformer=TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.setOutputProperty(OutputKeys.METHOD,"xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2");
            transformer.transform(new DOMSource(xmlDocument),new StreamResult(new File("settings.xml")));
        }
        catch(Exception ex)
        {
            File settingsFile=new File("settings.xml");
            if(settingsFile.exists())
            {
                settingsFile.delete();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        exitButton = new javax.swing.JButton();
        startButton = new javax.swing.JButton();
        xmlFileLabel = new javax.swing.JLabel();
        xsdFileLabel = new javax.swing.JLabel();
        xmlFileTextField = new javax.swing.JTextField();
        xsdFileTextField = new javax.swing.JTextField();
        selectXmlFileButton = new javax.swing.JButton();
        selectXsdFileButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        startButton.setText("Start");
        startButton.setEnabled(false);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        xmlFileLabel.setText("XML file:");

        xsdFileLabel.setText("XSD file:");

        selectXmlFileButton.setText("Select");
        selectXmlFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectXmlFileButtonActionPerformed(evt);
            }
        });

        selectXsdFileButton.setText("Select");
        selectXsdFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectXsdFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(exitButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(startButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(xsdFileLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(xsdFileTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(xmlFileLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(xmlFileTextField)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectXmlFileButton, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(selectXsdFileButton, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xmlFileLabel)
                    .addComponent(xmlFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectXmlFileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xsdFileLabel)
                    .addComponent(xsdFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectXsdFileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exitButton)
                    .addComponent(startButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exitButtonActionPerformed
    {//GEN-HEADEREND:event_exitButtonActionPerformed
        saveSettings();
        System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        saveSettings();
    }//GEN-LAST:event_formWindowClosing

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startButtonActionPerformed
    {//GEN-HEADEREND:event_startButtonActionPerformed
        startProcess();
    }//GEN-LAST:event_startButtonActionPerformed

    private void selectXmlFileButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_selectXmlFileButtonActionPerformed
    {//GEN-HEADEREND:event_selectXmlFileButtonActionPerformed
        selectXmlFile();
    }//GEN-LAST:event_selectXmlFileButtonActionPerformed

    private void selectXsdFileButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_selectXsdFileButtonActionPerformed
    {//GEN-HEADEREND:event_selectXsdFileButtonActionPerformed
        selectXsdFile();
    }//GEN-LAST:event_selectXsdFileButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exitButton;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton selectXmlFileButton;
    private javax.swing.JButton selectXsdFileButton;
    private javax.swing.JButton startButton;
    private javax.swing.JLabel xmlFileLabel;
    private javax.swing.JTextField xmlFileTextField;
    private javax.swing.JLabel xsdFileLabel;
    private javax.swing.JTextField xsdFileTextField;
    // End of variables declaration//GEN-END:variables
}
