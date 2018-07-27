package fit.pay2play.web.vaadin.desktop.play;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import fit.pay2play.data.aws.dynamo.entity.Play;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.base.BaseAdminPage;
import fit.pay2play.web.vaadin.desktop.base.BaseAdminLayout;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;
import xyz.cleangone.web.vaadin.ui.TwoDecimalField;

import static fit.pay2play.data.aws.dynamo.entity.Play.*;

public class PlayAdmin extends BaseAdminLayout
{
    private Play play;

    public PlayAdmin(Pay2PlayManager p2pMgr, MessageDisplayer msgDisplayer, BaseAdminPage adminPage)
    {
        super(p2pMgr, msgDisplayer, adminPage);
    }

    public void setPlay(Play play)
    {
        this.play = play;

        formLayout.removeAllComponents();

        formLayout.addComponent(createTextField(NAME_FIELD));
        formLayout.addComponent(createTextField(SHORT_NAME_FIELD));
        formLayout.addComponent(createTextField(PLURAL_NAME_FIELD));
        formLayout.addComponent(createTextField(DISPLAY_ORDER_FIELD));
        formLayout.addComponent(createTwoDecimalField(VALUE_FIELD));
        formLayout.addComponent(createCheckBox(ENABLED_FIELD));
    }

    private TextField createTextField(EntityField field)
    {
        return createTextField(field, play);
    }
    private CheckBox createCheckBox(EntityField field)
    {
        return createCheckBox(field, play);
    }
    private TwoDecimalField createTwoDecimalField(EntityField field)
    {
        return createTwoDecimalField(field, play);
    }

    protected void save(EntityField field)
    {
        p2pMgr.save(play);
        msgDisplayer.displayMessage(field.getDisplayName() + " saved");
    }
}