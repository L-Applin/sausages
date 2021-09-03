package help.sausage.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "crim/:crimName")
public class CrimView extends VerticalLayout
        implements BeforeEnterObserver, HasDynamicTitle {

    private String crimName;

    @Override
    public String getPageTitle() {
        return "Crim %s| Sausage".formatted(crimName + " ");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.crimName = event.getRouteParameters().get("crimName")
                .orElseThrow();
        add(new H1(crimName));
    }
}
