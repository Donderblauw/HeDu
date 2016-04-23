package lung.hedu;

import android.util.Log;



import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static lung.hedu.FileIO.saveStringFilePrivate;


/**
 * Created by Sarah on 18-8-2015.
 */
public class XML_IO {

    public static Document open_document_xml(String input) throws FileNotFoundException, XmlPullParserException {

        String infile = FileIO.loadStringFilePrivate(input, "xml");
//        Log.e("XML parser", "infile" + infile);
        if(infile.length() < 2)
        {
            saveStringFilePrivate(input, "xml", "");
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
        if(doc == null)
        {
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = null;
            try {
                dbuilder = dbfactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            doc = dbuilder.newDocument();
        }
        return doc;
    }

    public static String find_value_in_userxml(String add_line_id, String value_id)
    {

        String return_string = null;
        Document user_info_xml = null;
        try {
            user_info_xml = XML_IO.open_document_xml("user_info");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        NodeList nodes_find = user_info_xml.getElementsByTagName(add_line_id);
        if(nodes_find != null) {
            Node node_find = nodes_find.item(0);
            if (node_find != null) {
                NamedNodeMap temp_atr = node_find.getAttributes();

                Node node_temp_atr = temp_atr.getNamedItem(value_id);
                if (node_temp_atr != null) {
                    return_string = node_temp_atr.getTextContent();

                }
                else
                {
                    // return_string = "no value_id";
                }
            }
            else
            {
                // return_string = "no add_line_id2";
            }
        }
        else
        {
            // return_string = "no add_line_id1";
        }
        user_info_xml = null;
        return return_string;
    }

    public static String find_value_in_doc(Document doc_input, String add_line_id, String value_id)
    {

        String return_string = null;

        NodeList nodes_find = doc_input.getElementsByTagName(add_line_id);
        if(nodes_find != null) {
            Node node_find = nodes_find.item(0);
            if (node_find != null) {
                NamedNodeMap temp_atr = node_find.getAttributes();

                Node node_temp_atr = temp_atr.getNamedItem(value_id);
                if (node_temp_atr != null) {
                    return_string = node_temp_atr.getTextContent();

                }
             }
         }
        else
            doc_input = null;
        return return_string;
    }


    public static void set_value_user_info(String add_line_id, String value_id, String add_value)
    {
        // Log.e("XML parser", "read doc");
        Document user_info_xml = null;
        try {
            user_info_xml = XML_IO.open_document_xml("user_info");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        // Log.e("XML parser", "read doc comple");

        if(user_info_xml == null) {
            // Log.e("XML parser", "Create new :)");

            Element new_info_node = user_info_xml.createElement("info");
            Node info_node = user_info_xml.appendChild(new_info_node);

            Element new_user_info = user_info_xml.createElement("user_info");
            Node user_info_node = info_node.appendChild(new_user_info);

            Element new_add_line_id = user_info_xml.createElement(add_line_id);
            Node temp = user_info_node.appendChild(new_add_line_id);
        }
        // Log.e("XML parser", "NOT new?");
        NodeList info_list = user_info_xml.getElementsByTagName("info");
        Node info_node = null;
        if(info_list.getLength() == 0)
        {
            Element new_info = user_info_xml.createElement("info");
            info_node = user_info_xml.appendChild(new_info);
        }
        else
        {
            info_node = info_list.item(0);
        }

        NodeList worlds_list = user_info_xml.getElementsByTagName("user_info");
        Node node_found_userinfo = null;
        if(worlds_list.getLength() == 0)
        {
            Element new_worlds = user_info_xml.createElement("user_info");
            node_found_userinfo = info_node.appendChild(new_worlds);
        }
        else
        {
            node_found_userinfo = worlds_list.item(0);
        }

        NodeList new_added = user_info_xml.getElementsByTagName(add_line_id);
        Node node_found_new_added = null;
        if(new_added.getLength() == 0)
        {
            Element new_add_line_id = user_info_xml.createElement(add_line_id);
            node_found_new_added = node_found_userinfo.appendChild(new_add_line_id);
        }
        else
        {
            node_found_new_added = worlds_list.item(0);
        }

        // NamedNodeMap temp_atr = node_found_userinfo.getAttributes();
        NamedNodeMap temp_atr = node_found_new_added.getAttributes();
        Node node_temp_atr = temp_atr.getNamedItem(value_id);
        if(node_temp_atr == null)
        {
            Element temp = (Element) user_info_xml.getElementsByTagName(add_line_id).item(0);
            temp.setAttribute(value_id, add_value);
        }
        else
        {
            node_temp_atr.setTextContent(add_value);
        }



        try {
            XML_IO.save_XML("user_info", user_info_xml);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        user_info_xml = null;
    }


    public static void save_XML(String input, Document doc) throws FileNotFoundException, XmlPullParserException {
        try{
            input = input+".xml";
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



}
