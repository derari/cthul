package org.cthul.xml.idstax;

/**
 *
 * @author Arian Treffer
 */
public class MenuResolver extends AbstractElementIDResolver implements MenuSchema{

    public MenuResolver() {
        namespace(NS_MENU)
                .add("menu", MENU)
                .add("meal", MEAL)
                .add("name", NAME)
                .add("description", DESCRIPTION);
        // attributes
        namespace(null)
            .add("price", PRICE);
    }
    
}
