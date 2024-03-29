package com.example.pollfishclient.view;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.pollfishclient.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OverlayLayoutTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void overlayLayoutTest() {
        ViewInteraction pollButton = onView(
                allOf(withId(R.id.open_poll), withText("Call Library"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.mainCoordinatorLayout),
                                        1),
                                0),
                        isDisplayed()));
        pollButton.perform(click());

        ViewInteraction webView = onView(
                allOf(withId(R.id.pollfish_webview),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        1),
                                0),
                        isDisplayed()));
        webView.check(matches(isDisplayed()));

        ViewInteraction paramView1 = onView(
                allOf(withId(R.id.pollfish_param1),
                        isDisplayed()));
        paramView1.check(matches(isDisplayed()));

        ViewInteraction paramView2 = onView(
                allOf(withId(R.id.pollfish_param2),
                        isDisplayed()));
        paramView2.check(matches(isDisplayed()));

        ViewInteraction closeButton = onView(
                allOf(withId(R.id.pollfish_button_close), withContentDescription("Close Button"),
                        isDisplayed()));
        closeButton.check(matches(isDisplayed()));
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
}
