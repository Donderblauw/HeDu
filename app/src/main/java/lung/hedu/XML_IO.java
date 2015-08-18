package lung.hedu;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static lung.hedu.FileIO.saveStringFilePrivate;


/**
 * Created by Sarah on 18-8-2015.
 */
public class XML_IO {
/*
    public static void load_XML(String input) throws FileNotFoundException, XmlPullParserException {
        try{
            FileInputStream in = null;
            in = ApplicationContextProvider.getContext().openFileInput(input);

            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
            Document doc = dbuilder.parse(in);
            doc.getDocumentElement().normalize();

            Node node_answer = null;
            NodeList nodes_answers = doc.getElementsByTagName("awnser");
            for(int tel_nodes =0; tel_nodes < nodes_answers.getLength(); tel_nodes++)
            {
                node_answer = nodes_answers.item(tel_nodes);
                String text_answer = node_answer.getTextContent();
                Log.e("XML parser", "worked!" + text_answer);
            }
            Node parent_answer = node_answer.getParentNode();

            Element new_answer_element = doc.createElement("awnser");
            new_answer_element.setAttribute("goto", "temp");
            new_answer_element.appendChild(doc.createTextNode("option C"));
            parent_answer.appendChild(new_answer_element);

            TransformerFactory save_new_answer_factory = TransformerFactory.newInstance();
            Transformer save_new_answer_transformer = save_new_answer_factory.newTransformer();
            DOMSource source = new DOMSource(doc);

            FileOutputStream out = null;
            out = ApplicationContextProvider.getContext().openFileOutput(input, ApplicationContextProvider.getContext().MODE_PRIVATE);
            StreamResult result = new StreamResult(out);

            save_new_answer_transformer.transform(source, result);
        }


        catch (Exception e)
        {
            Log.e("XML parser", "error load_XML" + e.toString());
        }

    }
*/
    public static Document open_document_xml(String input) throws FileNotFoundException, XmlPullParserException {

        String infile = FileIO.loadStringFilePrivate(input, ".xml");
        if(infile.length() < 2)
        {
            saveStringFilePrivate(input, ".xml", "");
        }

        input = input +".xml";
        Document doc = null;
        try{
            FileInputStream in = null;
            in = ApplicationContextProvider.getContext().openFileInput(input);

            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
            doc = dbuilder.parse(in);
            doc.getDocumentElement().normalize();


        }
        catch (Exception e)
        {
            Log.e("XML parser", "error load_XML" + e.toString());
        }
        return doc;
    }


    public static void save_XML(String input, Document doc) throws FileNotFoundException, XmlPullParserException {
        try{

            TransformerFactory save_new_answer_factory = TransformerFactory.newInstance();
            Transformer save_new_answer_transformer = save_new_answer_factory.newTransformer();
            DOMSource source = new DOMSource(doc);

            FileOutputStream out = null;
            out = ApplicationContextProvider.getContext().openFileOutput(input, ApplicationContextProvider.getContext().MODE_PRIVATE);
            StreamResult result = new StreamResult(out);

            save_new_answer_transformer.transform(source, result);
        }
        catch (Exception e)
        {
            Log.e("XML parser", "error load_XML" + e.toString());
        }

    }


    /*
    public String[] XML_ini_read(String XML_name, String find_tag, String parent_name, String value_to_find) {
        XmlPullParser XmlPullParser_temp = null;
        String text_return = "";

        try {
            XmlPullParser_temp = load_XML(XML_name+".xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        int event;

        Integer tel = 0;
        String[] return_value = new String[99];

        String parents_xml[] = new String[9];
        Integer level_parent_atm = 0;
        String xml_atm = "";

        try {
            event = XmlPullParser_temp.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = XmlPullParser_temp.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        xml_atm = name;
                        level_parent_atm = level_parent_atm+1;
                        parents_xml[level_parent_atm] = xml_atm;


                        if(xml_atm.equals(find_tag))
                        {
                            boolean found = false;
                            if(parent_name.equals(""))
                            {
                                found = true;
                            }
                            else if( parents_xml[(level_parent_atm -1)].equals(parent_name) )
                            {
                                found = true;
                            }

                            if(found == true)
                            {
                                return_value[tel] = XmlPullParser_temp.getAttributeValue(null, value_to_find).toString();
                                tel = tel+1;
                            }
                        }
                    break;


                    case XmlPullParser.END_TAG:

                        level_parent_atm = level_parent_atm-1;
                        xml_atm = parents_xml[level_parent_atm];
                        break;
                }

                event = XmlPullParser_temp.next();

            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return return_value;
    }

    public XmlPullParser load_XML(String input) throws FileNotFoundException, XmlPullParserException {

        FileInputStream in = null;
        in = ApplicationContextProvider.getContext().openFileInput(input);

        XmlPullParserFactory xmlFactoryObject;

        xmlFactoryObject = XmlPullParserFactory.newInstance();
        XmlPullParser myparser = xmlFactoryObject.newPullParser();

        myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        myparser.setInput(in, null);

        return myparser;
    }
    public void add_xml_tag (String XML_name, String add_tag, String parent_name, String name_value_to_add, String value_to_add, String text)
    {
        String file_old = FileIO.loadStringFilePrivate(XML_name, "xml");

        String new_tag = "<" + add_tag + " " + name_value_to_add + "=" + value_to_add + ">" + text + "</" + add_tag + ">";
        String new_file = "";

        if(parent_name == "")
        {
            new_file= file_old + new_tag;
        }
        else {
            Integer start_parent = file_old.indexOf(parent_name);
            Integer end_parent = file_old.indexOf(">", start_parent) + 1;

            new_file = file_old.substring(0, end_parent) + new_tag + file_old.substring(0, file_old.length());
        }
        FileIO.saveStringFilePrivate(XML_name, "xml", new_file);
    }
*/
}
