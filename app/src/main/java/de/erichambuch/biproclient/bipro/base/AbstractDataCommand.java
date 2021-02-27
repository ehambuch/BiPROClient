package de.erichambuch.biproclient.bipro.base;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import tellh.com.recyclertreeview_lib.LayoutItemType;
import tellh.com.recyclertreeview_lib.TreeNode;

/**
 * Basisklasse für alle Kommandos, die erhaltene Daten als strukturierten Baum darstellen wollen.
 */
public abstract class AbstractDataCommand extends BiproServiceCommand {

    public static class TreeNodeElement implements LayoutItemType {
        public String elementName;
        public String value;

        public TreeNodeElement(String name) {
            this.elementName = name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_treenode;
        }

    }

    private final String rootElement;

    public AbstractDataCommand(ProviderConfiguration configuration, RequestLogger logger, String rootElement) {
        super(configuration, logger);
        this.rootElement = rootElement;
    }

    public TreeNode<TreeNodeElement> createTreeView(String xmlResponse) throws Exception {
        TreeNode<TreeNodeElement> root = new TreeNode<>(new TreeNodeElement(rootElement));
        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
        xmlFactoryObject.setNamespaceAware(true);
        XmlPullParser myParser = xmlFactoryObject.newPullParser();
        myParser.setInput(new StringReader(xmlResponse));
        int event = myParser.getEventType();
        boolean startTree = false;
        TreeNodeElement currentElement = null;
        TreeNode<TreeNodeElement> child = root;
        while (event != XmlPullParser.END_DOCUMENT)  {
            String name=myParser.getName();
            switch (event){
                case XmlPullParser.START_TAG:
                    if(name.equals(rootElement)) {
                        startTree = true; // start aufbau tree
                    } else if(startTree) {
                        currentElement = new TreeNodeElement(name);
                        TreeNode<TreeNodeElement> newChild = new TreeNode<>(currentElement);
                        child.addChild(newChild);
                        child = newChild;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(name.equals(rootElement)) {
                        startTree = false;
                    } else if (startTree) {
                        child = child.getParent();
                    }
                    break;
                case XmlPullParser.TEXT:
                    String t = myParser.getText();
                    if (currentElement != null && startTree && t != null && t.trim().length() >0) {
                        currentElement.setValue(t.trim());
                    }
                    break;
                default: break;
            }
            event = myParser.next();
        }
        return root;
    }

}
