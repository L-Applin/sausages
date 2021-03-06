package help.sausage.ui.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import help.sausage.client.ReviewClient;
import help.sausage.ui.data.Review;
import help.sausage.utils.ApplicationContextProvider;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@CssImport("./styles/review-holder.css")
public class ReviewHolderComponent extends VerticalLayout {

    private static final int DEFAULT_SIZE = 50;
    private long totalServerReviews;
    private long currentlyLoadedReviews;

    private final ReviewClient reviewClient;
    @Getter
    private final Button loadMoreBtn = new Button();
    @Getter
    private Query<Review, Void> query;

    @Getter @Setter
    private int offset = 0;
    @Getter @Setter
    private int size;
    private DataProvider<Review, Void> dataProvider;

    public ReviewHolderComponent(FetchCallback<Review, Void> fetchCallback, int size, boolean preLoad) {
        this.dataProvider = DataProvider.fromCallbacks(fetchCallback, this::maxCount);
        this.reviewClient = ApplicationContextProvider.getCtx().getBean(ReviewClient.class);
        this.size = size;
        this.query = createQuery();

        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setPadding(false);
        setId("main-review-holder");

        loadMoreBtn.setText("more reviews");
        loadMoreBtn.addClickListener(this::loadMore);
        loadMoreBtn.setId("review-holder-load-btn");

        if (preLoad) loadReviews();

    }

    public ReviewHolderComponent(FetchCallback<Review, Void> fetchCallback, boolean preLoad) {
        this(fetchCallback, DEFAULT_SIZE, preLoad);
    }

    public void reset() {
        removeAll();
        offset = 0;
        query = createQuery();
    }

    public void loadReviews() {
        List<Review> reviews = dataProvider.fetch(query).toList();
        reviews.forEach(r -> add(new ReviewCardComponent(r)));
        offset += size;
        query = createQuery();
        add(loadMoreBtn);
        updateCount(query);
    }

    public void loadReviews(Query<Review, Void> query) {
        dataProvider.fetch(query)
                .forEach(r -> add(new ReviewCardComponent(r)));
        add(loadMoreBtn);
        updateCount(query);
    }


    public Query<Review, Void> createQuery() {
        return new Query<>(offset, size, QuerySortOrder.desc("dateCreated").build(),
                Comparator.comparing(Review::dateReview), null);
    }

    public int maxCount(Query<Review, Void> query) {
        Long value = reviewClient.getTotalReviewCount().getBody();
        this.totalServerReviews = value;
        return value.intValue();
    }

    public void updateCount(Query<Review, Void> query) {
        currentlyLoadedReviews += query.getLimit();
        if (currentlyLoadedReviews >= totalServerReviews) {
            loadMoreBtn.setDisableOnClick(true);
        }
    }

    public void loadMore(ClickEvent<Button> event) {
        remove(loadMoreBtn);
        loadReviews();
    }

}
