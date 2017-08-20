package de.n26.n26androidsamples.credit.domain;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import de.n26.n26androidsamples.base.BaseTest;
import de.n26.n26androidsamples.credit.data.CreditDataTestUtils;
import de.n26.n26androidsamples.credit.data.CreditDraftSummary;
import de.n26.n26androidsamples.credit.data.CreditRepository;
import io.reactivex.Completable;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.subscribers.TestSubscriber;
import polanski.option.Option;

import static org.mockito.Mockito.when;

public class GetCreditDraftSummaryListTest extends BaseTest {

    @Mock
    private CreditRepository creditRepository;

    private GetCreditDraftSummaryList interactor;

    private ArrangeBuilder arrangeBuilder;

    private TestSubscriber<List<CreditDraftSummary>> ts;

    @Before
    public void setUp() {
        interactor = new GetCreditDraftSummaryList(creditRepository);
        arrangeBuilder = new ArrangeBuilder();
        ts = new TestSubscriber<>();
    }

    @Test
    public void test() {

        List<CreditDraftSummary> testDraftList = createTestDraftList();
        interactor.getBehaviorStream(Option.none()).subscribe(ts);

        arrangeBuilder.emitDraftSummaryListFromRepo(Option.ofObj(testDraftList));

        ts.assertValue(testDraftList);
    }

    private List<CreditDraftSummary> createTestDraftList() {
        return new ArrayList<CreditDraftSummary>() {{
            add(CreditDataTestUtils.creditDraftSummaryTestBuilder().id("1").build());
            add(CreditDataTestUtils.creditDraftSummaryTestBuilder().id("2").build());
            add(CreditDataTestUtils.creditDraftSummaryTestBuilder().id("3").build());
        }};
    }

    private class ArrangeBuilder {

        private BehaviorProcessor<Option<List<CreditDraftSummary>>> repoDraftStream = BehaviorProcessor.create();

        private ArrangeBuilder() {
            when(creditRepository.getCreditDraftSummaryListBehaviorStream()).thenReturn(repoDraftStream);
        }

        private ArrangeBuilder emitDraftSummaryListFromRepo(@NonNull final Option<List<CreditDraftSummary>> listOption) {
            repoDraftStream.onNext(listOption);
            return this;
        }

        private ArrangeBuilder withSuccessfulFetch() {
            when(creditRepository.fetchCreditDraftSummariesSingle()).thenReturn(Completable.complete());
            return this;
        }

        private ArrangeBuilder withFetchError(@NonNull final Throwable throwable) {
            when(creditRepository.fetchCreditDraftSummariesSingle()).thenReturn(Completable.error(throwable));
            return this;
        }
    }
}
