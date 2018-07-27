package fit.pay2play.web.vaadin.desktop.base;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.manager.Pay2PlayManager;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseNamedEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;
import xyz.cleangone.web.vaadin.ui.TwoDecimalField;
import xyz.cleangone.web.vaadin.util.VaadinUtils;

import java.math.BigDecimal;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public abstract class BaseAdminLayout extends VerticalLayout
{
    protected final Pay2PlayManager p2pMgr;
    protected final MessageDisplayer msgDisplayer;
    protected final Settable parent;

    protected final FormLayout formLayout = new FormLayout();

    public BaseAdminLayout(Pay2PlayManager p2pMgr, MessageDisplayer msgDisplayer, Settable parent)
    {
        this.p2pMgr = p2pMgr;
        this.msgDisplayer = msgDisplayer;
        this.parent = parent;

        setLayout(this, MARGIN_FALSE, SPACING_FALSE, BACK_PINK);
        setLayout(formLayout, MARGIN_L, SPACING_FALSE, BACK_RED);

        Button closeButton = VaadinUtils.createCloseButton("Close Item");
        closeButton.addClickListener(e -> parent.set());

        addComponents(closeButton, formLayout);
        setExpandRatio(formLayout, 1.0f);
    }

    public TextField createTextField(EntityField field, BaseNamedEntity entity)
    {
        TextField textField = VaadinUtils.createTextField(field.getDisplayName(), entity.get(field), null);
        textField.addValueChangeListener(event -> {
            entity.set(field, (String)event.getValue());
            save(field);
        });

        return textField;
    }

    protected CheckBox createCheckBox(EntityField field, BaseNamedEntity entity)
    {
        CheckBox checkBox = VaadinUtils.createCheckBox(field.getDisplayName(), entity.getBoolean(field));
        checkBox.addValueChangeListener(event -> {
            entity.setBoolean(field, event.getValue());
            save(field);
        });

        return checkBox;
    }

    protected TwoDecimalField createTwoDecimalField(EntityField field, BaseNamedEntity entity)
    {
        TwoDecimalField twoDecimalField = createTwoDecimalField(field.getDisplayName(), entity.getBigDecimal(field));
        twoDecimalField.addValueChangeListener(event -> {
            entity.setBigDecimal(field, twoDecimalField.getBigDecimalValue());
            save(field);
        });

        return twoDecimalField;
    }

    protected abstract void save(EntityField field);

    protected static TwoDecimalField createTwoDecimalField(String name, BigDecimal value)
    {
        TwoDecimalField field = new TwoDecimalField(name);
        field.setValueChangeMode(ValueChangeMode.BLUR);
        field.addStyleName(ValoTheme.TEXTFIELD_TINY);
        field.addStyleName(ValoTheme.LABEL_TINY);
        field.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        if (value != null) { field.setValue(value.toString()); } // todo refactor

        return field;
    }
}