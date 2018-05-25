package av.udacity.bakingapp.ui.main;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import av.udacity.bakingapp.R;
import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OnePanelTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void test01Scroll() {
        pause(1000);
        ViewInteraction recyclerView = onView(withId(R.id.rv_main_recipes));
        recyclerView.perform(RecyclerViewActions.scrollToPosition(3)).perform(click());
        pause(1000);
    }

    @Test
    public void test02Click() {
        pause(1000);
        ViewInteraction recyclerView = onView(withId(R.id.rv_main_recipes));
        recyclerView.perform(click());
        pause(1000);
    }

    @Test
    public void test03AppTitle() {
        pause(1000);
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withText(R.string.app_name)).check(matches(withParent(withId(R.id.toolbar))));
        pause(1000);
    }

    @Test
    public void test04ToolbarTitle() {
        pause(1000);

        // Scroll RecyclerView to position 3
        ViewInteraction recyclerView = onView(allOf(withId(R.id.rv_main_recipes), childAtPosition(
                withId(R.id.content_main), 0)));
        recyclerView.perform(actionOnItemAtPosition(3, scrollTo()));
        pause(1000);

        // Click on position 3 of RecyclerView
        recyclerView.perform(actionOnItemAtPosition(3, click()));
        pause(1000);

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withText("Cheesecake")).check(matches(withParent(withId(R.id.toolbar))));
        pause(1000);
    }

    @Test
    public void test05RecipeSteps() {
        pause(1000);

        ViewInteraction recyclerView = onView(allOf(withId(R.id.rv_main_recipes), childAtPosition(
                withId(R.id.content_main), 0)));
        recyclerView.perform(actionOnItemAtPosition(3, click()));
        pause(1000);

        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.recipe_list), childAtPosition(
                withClassName(is("android.widget.LinearLayout")), 3)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));
        pause(3000);
    }

    @Test
    public void test06NavigationOnePanel() {
        pause(1000);

        ViewInteraction recyclerView = onView(allOf(withId(R.id.rv_main_recipes), childAtPosition(withId(R.id.content_main),0)));
        recyclerView.perform(actionOnItemAtPosition(2, click()));
        pause(1000);

        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.recipe_list), childAtPosition(
                withClassName(is("android.widget.LinearLayout")), 3)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));
        pause(1000);

        ViewInteraction appCompatButton = onView(allOf(withId(R.id.btn_next), childAtPosition(
                childAtPosition(withClassName(is("android.widget.LinearLayout")), 3), 1),
                isDisplayed()));
        appCompatButton.perform(click());
        pause(1000);

        ViewInteraction appCompatButton2 = onView(allOf(withId(R.id.btn_next), childAtPosition(
                childAtPosition(withClassName(is("android.widget.LinearLayout")), 3), 1),
                isDisplayed()));
        appCompatButton2.perform(click());
        pause(1000);

        ViewInteraction appCompatImageButton = onView(allOf(withContentDescription("Navigate up"),
                childAtPosition(allOf(withId(R.id.toolbar),
                        childAtPosition(withClassName(is("android.support.design.widget.AppBarLayout")),0)),1),
                        isDisplayed()));
        appCompatImageButton.perform(click());
        pause(1000);

        ViewInteraction appCompatImageButton2 = onView(allOf(withContentDescription("Navigate up"),
                        childAtPosition(allOf(withId(R.id.toolbar), childAtPosition( withId(R.id.app_bar), 0)), 1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());
        pause(1000);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private void pause(long millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException exc) {
            Timber.e(exc, "Pause interrupted.");
        }
    }
}
