package fit.pay2play.web.vaadin.desktop.action;

import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import fit.pay2play.data.aws.dynamo.entity.Action;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.base.BaseAdminLayout;
import fit.pay2play.web.vaadin.desktop.base.MyIntegerField;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;

import static fit.pay2play.data.aws.dynamo.entity.Action.*;

public class ActionAdmin extends BaseAdminLayout
{
    private Action action;
    private Label description = new Label("desc");

    public ActionAdmin(Pay2PlayManager p2pMgr, MessageDisplayer msgDisplayer, ActionsLayout actionsLayout)
    {
        super(p2pMgr, msgDisplayer, actionsLayout);
    }

    public void set(Action action)
    {
        this.action = action;
        description.setValue(action.getDescription());

        formLayout.removeAllComponents();


        formLayout.addComponent(description);
        formLayout.addComponent(createIntegerField(AMOUNT_FIELD));
    }

    public TextField createIntegerField(EntityField field)
    {
        MyIntegerField integerField = new MyIntegerField(field.getDisplayName());
        integerField.setValue(action.getAmount() + "");
        integerField.addValueChangeListener(event -> {
            action.setInt(field, integerField.getIntegerValue());
            save(field);
        });

        return integerField;
    }

    private void save(EntityField field)
    {
        p2pMgr.save(action);
        msgDisplayer.displayMessage(field.getDisplayName() + " saved");
        description.setValue(action.getDescription());
    }
}