package xyz.cleangone.web.vaadin.servlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/*", name = "P2pServlet", asyncSupported = true)
@VaadinServletConfiguration(ui = P2pUI.class, productionMode = false)
public class P2pServlet extends VaadinServlet
{
    @Override
    protected void servletInitialized() throws ServletException
    {
        super.servletInitialized();

        getService().setSystemMessagesProvider(new P2pSystemMessagesProvider());
    }

}
