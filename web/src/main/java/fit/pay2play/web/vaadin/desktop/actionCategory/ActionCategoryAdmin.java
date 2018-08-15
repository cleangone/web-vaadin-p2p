package fit.pay2play.web.vaadin.desktop.actionCategory;

import com.vaadin.ui.*;
import fit.pay2play.data.aws.dynamo.entity.ActionCategory;
import fit.pay2play.data.aws.dynamo.entity.ActionType;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.base.BaseAdminPage;
import fit.pay2play.web.vaadin.desktop.base.BaseAdminLayout;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;
import xyz.cleangone.web.vaadin.ui.TwoDecimalField;

import static fit.pay2play.data.aws.dynamo.entity.ActionCategory.*;

public class ActionCategoryAdmin extends BaseAdminLayout
{
    private ActionCategory actionCategory;

    public ActionCategoryAdmin(Pay2PlayManager p2pMgr, MessageDisplayer msgDisplayer, BaseAdminPage adminPage)
    {
        super(p2pMgr, msgDisplayer, adminPage);
    }

    public void set(ActionCategory actionCategory)
    {
        this.actionCategory = actionCategory;

        formLayout.removeAllComponents();

        formLayout.addComponent(createTextField(NAME_FIELD));
        formLayout.addComponent(createTextField(SHORT_NAME_FIELD));
        formLayout.addComponent(createTextField(PLURAL_NAME_FIELD));
        formLayout.addComponent(createTextField(DISPLAY_ORDER_FIELD));
        formLayout.addComponent(createTwoDecimalField(VALUE_FIELD));
        formLayout.addComponent(createIntegerField(actionCategory.isActionType(ActionType.Pay) ? TARGET_MIN_AMOUNT_FIELD : TARGET_MAX_AMOUNT_FIELD));
        formLayout.addComponent(createCheckBox(ENABLED_FIELD));
    }

    private TextField createTextField(EntityField field)
    {
        return createTextField(field, actionCategory);
    }
    private CheckBox createCheckBox(EntityField field)
    {
        return createCheckBox(field, actionCategory);
    }
    private TwoDecimalField createIntegerField(EntityField field)
    {
        return createIntegerField(field, actionCategory);
    }
    private TwoDecimalField createTwoDecimalField(EntityField field)
    {
        return createTwoDecimalField(field, actionCategory);
    }

    protected void save(EntityField field)
    {
        p2pMgr.save(actionCategory);
        msgDisplayer.displayMessage(field.getDisplayName() + " saved");
    }
}