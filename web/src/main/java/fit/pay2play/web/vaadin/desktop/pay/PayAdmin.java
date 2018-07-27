package fit.pay2play.web.vaadin.desktop.pay;

import com.vaadin.ui.*;
import fit.pay2play.data.aws.dynamo.entity.Pay;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.base.BaseAdminPage;
import fit.pay2play.web.vaadin.desktop.base.BaseAdminLayout;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;
import xyz.cleangone.web.vaadin.ui.TwoDecimalField;

import static fit.pay2play.data.aws.dynamo.entity.Pay.*;

public class PayAdmin extends BaseAdminLayout
{
    private Pay pay;

    public PayAdmin(Pay2PlayManager p2pMgr, MessageDisplayer msgDisplayer, BaseAdminPage adminPage)
    {
        super(p2pMgr, msgDisplayer, adminPage);
    }

    public void setPay(Pay pay)
    {
        this.pay = pay;

        formLayout.removeAllComponents();

        formLayout.addComponent(createTextField(NAME_FIELD));
        formLayout.addComponent(createTextField(SHORT_NAME_FIELD));
        formLayout.addComponent(createTextField(PLURAL_NAME_FIELD));
        formLayout.addComponent(createTextField(DISPLAY_ORDER_FIELD));
        formLayout.addComponent(createTwoDecimalField(VALUE_FIELD));
        formLayout.addComponent(createCheckBox(ENABLED_FIELD));
        formLayout.addComponent(createCheckBox(REQUIRED_FIELD));
    }

    private TextField createTextField(EntityField field)
    {
        return createTextField(field, pay);
    }
    private CheckBox createCheckBox(EntityField field)
    {
        return createCheckBox(field, pay);
    }
    private TwoDecimalField createTwoDecimalField(EntityField field)
    {
        return createTwoDecimalField(field, pay);
    }

    protected void save(EntityField field)
    {
        p2pMgr.save(pay);
        msgDisplayer.displayMessage(field.getDisplayName() + " saved");
    }
}