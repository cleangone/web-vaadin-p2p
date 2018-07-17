package fit.pay2play.web.vaadin.desktop.components;

import com.vaadin.ui.TextField;
import fit.pay2play.data.aws.dynamo.entity.Play;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.BaseAdminPage;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;
import xyz.cleangone.web.vaadin.ui.TwoDecimalField;
import xyz.cleangone.web.vaadin.util.VaadinUtils;

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
        formLayout.addComponent(createTwoDecimalField(VALUE_FIELD));
    }

    public TextField createTextField(EntityField field)
    {
        TextField textField = VaadinUtils.createTextField(field.getDisplayName(), play.get(field), null);
        textField.addValueChangeListener(event -> {
            play.set(field, (String)event.getValue());
            save(field);
        });

        return textField;
    }

    private TwoDecimalField createTwoDecimalField(EntityField field)
    {
        TwoDecimalField twoDecimalField = createTwoDecimalField(field.getDisplayName(), play.getBigDecimal(field));
        twoDecimalField.addValueChangeListener(event -> {
            play.setBigDecimal(field, twoDecimalField.getBigDecimalValue());
            save(field);
        });

        return twoDecimalField;
    }

    private void save(EntityField field)
    {
        p2pMgr.save(play);
        msgDisplayer.displayMessage(field.getDisplayName() + " saved");
    }
}