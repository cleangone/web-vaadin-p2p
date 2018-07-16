package xyz.cleangone.web.vaadin.desktop.banner;

import com.vaadin.ui.Component;
import xyz.cleangone.web.manager.SessionManager;

public interface BannerComponent extends Component
{
    void reset(SessionManager sessionMgr);
}
