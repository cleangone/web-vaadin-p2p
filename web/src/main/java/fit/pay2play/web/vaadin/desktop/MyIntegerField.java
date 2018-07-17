package fit.pay2play.web.vaadin.desktop;

import com.vaadin.data.Binder;
import com.vaadin.data.Result;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.TextField;

public class MyIntegerField extends TextField
{
    Binder<IntegerHolder> binder = new Binder<>();

    public MyIntegerField()
    {
        super();
        binder.forField(this)
            .withConverter(new IntegerConverter())
            .bind(IntegerHolder::getValue, IntegerHolder::setValue);
    }

    public MyIntegerField(String caption)
    {
        super(caption);
        binder.forField(this)
            .withConverter(new IntegerConverter())
            .bind(IntegerHolder::getValue, IntegerHolder::setValue);
    }

    public Integer getIntegerValue()
    {
        IntegerHolder holder = new IntegerHolder();
        try
        {
            binder.writeBean(holder);
            return holder.getValue();
        }
        catch (ValidationException e)
        {
            return null;
        }
    }

    class IntegerHolder
    {
        Integer value;
        Integer getValue()
        {
            return value;
        }

        void setValue(Integer value)
        {
            this.value = value == null ? 0 : value;
        }
    }

    class IntegerConverter extends StringToIntegerConverter
    {
        IntegerConverter()
        {
            super("Invalid format");
        } // error message

        @Override
        public Result<Integer> convertToModel(String value, ValueContext context)
        {
            return super.convertToModel(value, context);
        }
    }
}
