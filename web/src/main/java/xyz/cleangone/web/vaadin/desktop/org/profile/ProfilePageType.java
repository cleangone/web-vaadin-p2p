package xyz.cleangone.web.vaadin.desktop.org.profile;

public enum ProfilePageType
{
    GENERAL("General");

    private final String text;

    ProfilePageType(final String text)
    {
        this.text = text;
    }

    public String toString()
    {
        return text;
    }
}

