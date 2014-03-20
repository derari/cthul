package org.cthul.xml.validation;

import org.cthul.resolve.ObjectResolver;
import org.cthul.resolve.RResult;
import org.cthul.resolve.ResourceResolver;
import org.cthul.xml.OrgW3Resolver;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
public class ObjectResolverTest {

    @Test
    public void test_success_uri() {
        TestResolver r = new TestResolver(OrgW3Resolver.getInstance());
        RResult res = r.resolve("http://www.w3.org/2001/XMLSchema");
        assertThat(res, is(notNullValue()));
    }
    
    @Test
    public void test_success_relative() {
        TestResolver r = new TestResolver(OrgW3Resolver.getInstance());
        RResult res = r.resolve("XMLSchema", "http://www.w3.org/2001/foo.xml");
        assertThat(res, is(notNullValue()));
    }
    
    @Test
    public void test_fail_uri() {
        TestResolver r = new TestResolver(OrgW3Resolver.getInstance());
        RResult res = r.resolve("http://www.w3.org/2001/XMLStuff");
        assertThat(res, is(nullValue()));
    }
    
    @Test
    public void test_fail_relative() {
        TestResolver r = new TestResolver(OrgW3Resolver.getInstance());
        RResult res = r.resolve("XMLStuff", "http://www.w3.org/2001/foo.xml");
        assertThat(res, is(nullValue()));
    }

    static class TestResolver extends ObjectResolver<RResult, RuntimeException> {

        public TestResolver(ResourceResolver resolver) {
            super(resolver);
        }

        @Override
        public RResult resolve(String uri) throws RuntimeException {
            return super.resolve(uri);
        }

        @Override
        public RResult resolve(String systemId, String baseURI) throws RuntimeException {
            return super.resolve(systemId, baseURI);
        }

        @Override
        public RResult resolve(String uri, String publicId, String systemId, String baseURI) throws RuntimeException {
            return super.resolve(uri, publicId, systemId, baseURI);
        }

        @Override
        protected RResult result(RResult result) throws RuntimeException {
            return result;
        }
    }
}
