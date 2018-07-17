package fit.pay2play.web.vaadin.desktop.components;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.BaseAdminPage;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;
import xyz.cleangone.web.vaadin.ui.TwoDecimalField;
import xyz.cleangone.web.vaadin.util.VaadinUtils;

import java.math.BigDecimal;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class BaseAdminLayout extends VerticalLayout
{
    protected final Pay2PlayManager p2pMgr;
    protected final MessageDisplayer msgDisplayer;
    protected final BaseAdminPage adminPage;

    protected final FormLayout formLayout = new FormLayout();

    public BaseAdminLayout(Pay2PlayManager p2pMgr, MessageDisplayer msgDisplayer, BaseAdminPage adminPage)
    {
        this.p2pMgr = p2pMgr;
        this.msgDisplayer = msgDisplayer;
        this.adminPage = adminPage;

        setLayout(this, MARGIN_FALSE, SPACING_FALSE, BACK_PINK);
        setLayout(formLayout, MARGIN_L, SPACING_FALSE, BACK_RED);

        Button closeButton = VaadinUtils.createCloseButton("Close Item");
        closeButton.addClickListener(e -> adminPage.set());

        addComponents(closeButton, formLayout);
        setExpandRatio(formLayout, 1.0f);
    }

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