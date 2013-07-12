package org.cthul.matchers.fluent.custom;

import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.builder.FluentBuilder;
import org.hamcrest.Matcher;

/**
 *
 * @author derari
 */
public class CustomFluentBuilder<Item, This extends CustomFluentMatcher<Item, This>>
        extends FluentBuilder<Item> 
        implements CustomFluentMatcher<Item, This> {

    @Override
    public This as(String reason) {
        return (This) super.as(reason);
    }

    @Override
    public This as(String reason, Object... args) {
        return (This) super.as(reason, args);
    }

    @Override
    public This is(Matcher<? super Item> matcher) {
        return (This) super.is(matcher);
    }

    @Override
    public This is() {
        return (This) super.is();
    }

    @Override
    public This and(Matcher<? super Item> matcher) {
        return (This) super.and(matcher);
    }

    @Override
    public This and() {
        return (This) super.and();
    }

    @Override
    public This isNot(Matcher<? super Item> matcher) {
        return (This) super.isNot(matcher);
    }

    @Override
    public This not(Matcher<? super Item> matcher) {
        return (This) super.not(matcher);
    }

    @Override
    public This not() {
        return (This) super.not();
    }

    @Override
    public This andNot(Matcher<? super Item> matcher) {
        return (This) super.andNot(matcher);
    }

    @Override
    public This andNot() {
        return (This) super.andNot();
    }

    @Override
    public This either(Matcher<? super Item> matcher) {
        return (This) super.either(matcher);
    }

    @Override
    public This either() {
        return (This) super.either();
    }

    @Override
    public This xor(Matcher<? super Item> matcher) {
        return (This) super.xor(matcher);
    }

    @Override
    public This xor() {
        return (This) super.xor();
    }

    @Override
    public This or(Matcher<? super Item> matcher) {
        return (This) super.or(matcher);
    }

    @Override
    public This or() {
        return (This) super.or();
    }

    @Override
    public This orNot(Matcher<? super Item> matcher) {
        return (This) super.orNot(matcher);
    }

    @Override
    public This orNot() {
        return (This) super.orNot();
    }
    
}
