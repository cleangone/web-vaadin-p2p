package fit.pay2play.web.vaadin.desktop.actionCategory;

import com.vaadin.navigator.View;
import fit.pay2play.data.aws.dynamo.entity.ActionType;

public class PlaysAdminPage extends ActionCategoriesAdminPage implements View
{
    public static final String NAME = "Play";

    public void set()
    {
        set(ActionType.Play);
    }
}