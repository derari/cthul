package org.cthul.parser.util;

import org.cthul.parser.util.InstanceMap.Factory;
import org.junit.*;
import static org.cthul.matchers.CthulMatchers.*;
import static org.cthul.proc.Procs.invoke;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class InstanceMapTest {
    
    protected InstanceMap iMap;
    
    public InstanceMapTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        iMap = new InstanceMap();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_put_stringKey() {
        iMap.put("theFoo", new Foo());
        assertThat(iMap.get("theFoo"), _isA(Foo.class));
    }
    
    @Test
    public void test_put_clazzKey() {
        iMap.put(Foo.class, new Foo());
        assertThat(iMap.get(Foo.class), _isA(Foo.class));
        assertThat(iMap.get(SuperFoo.class), _isA(Foo.class));
        assertThat(iMap.get(Fooable.class), _isA(Foo.class));
        assertThat(iMap.get(Object.class), _isA(Foo.class));
    }
    
    @Test
    public void test_put_clazzKey_classLimit() {
        iMap.put(Foo.class, new Foo(), SuperFoo.class);
        assertThat(iMap.get(Foo.class), _isA(Foo.class));
        assertThat(iMap.get(SuperFoo.class), _isA(Foo.class));
        assertThat(iMap.get(Fooable.class), is(nullValue()));
        assertThat(iMap.get(Object.class), is(nullValue()));
    }

    @Test
    public void test_put_clazzKey_ifaceLimit() {
        iMap.put(Foo.class, new Foo(), Foo.class, InstanceMap.AnyInterface.class);
        assertThat(iMap.get(Foo.class), _isA(Foo.class));
        assertThat(iMap.get(SuperFoo.class), is(nullValue()));
        assertThat(iMap.get(Fooable.class), _isA(Foo.class));
        assertThat(iMap.get(Object.class), is(nullValue()));
    }
    
    @Test
    public void test_put_clazzKey_ambiguous() {
        iMap.put(Foo.class, new Foo());
        assertThat("foo should be the only Object", 
                iMap.get(Object.class), _isA(Foo.class));
        
        iMap.put(Bar.class, new Bar());
        assertThat("getting an Object should be ambiguous",
                invoke(iMap, "get", (Object) Object.class), 
                raisesException("ambiguous"));
        
        assertThat(iMap.get(Foo.class), _isA(Foo.class));
        assertThat(iMap.get(Bar.class), _isA(Bar.class));
    }
    
    @Test
    public void test_getOrCreate() {
        Foo foo1 = iMap.getOrCreate(Foo.class);
        assertThat(foo1, _isA(Foo.class));
        Foo foo2 = iMap.getOrCreate(Foo.class);
        assertThat(foo2, is(sameInstance(foo1)));
    }
    
    @Test
    public void test_putNew() {
        Object foo = iMap.putNew(Foo.class, actuallyBarFactory());
        assertThat(foo, _isA(Bar.class));
        assertThat(iMap.get(Foo.class), _isA(Bar.class));
    }
    
    @Test
    public void test_remove() {
        iMap.put(Foo.class, new Foo());
        iMap.remove(Foo.class, Object.class);
        assertThat(iMap.get(Foo.class), is(nullValue()));
        assertThat(iMap.get(SuperFoo.class), is(nullValue()));
        assertThat(iMap.get(Object.class), is(nullValue()));
        assertThat(iMap.get(Fooable.class), _isA(Foo.class));
    }
    
    public static class InjectFoo {
        @Inject 
        public Foo foo;
    }
    
    @Test
    public void test_inject() {
        iMap.getOrCreate(Foo.class);
        InjectFoo iFoo = iMap.initialize(new InjectFoo());
        assertThat(iFoo.foo, _isA(Foo.class));
    }
    
    @Test
    public void test_inject_no_create() {
        InjectFoo iFoo = iMap.initialize(new InjectFoo());
        assertThat(iFoo.foo, is(nullValue()));
    }
    
    @Test
    public void test_inject_autoupdate() {
        Foo foo1 = new Foo(), foo2 = new Foo();
        iMap.put(Foo.class, foo1);
        InjectFoo iFoo = iMap.initialize(new InjectFoo());
        assertThat(iFoo.foo, is(sameInstance(foo1)));
        
        iMap.replace(Foo.class, foo2);
        assertThat(iFoo.foo, is(sameInstance(foo2)));
    }
    
    public static class InjectFooCreate {
        @Inject(create=true)
        public Foo foo;
    }
    
    @Test
    public void test_inject_create() {
        InjectFooCreate iFoo = iMap.initialize(new InjectFooCreate());
        assertThat(iFoo.foo, _isA(Foo.class));
        assertThat(iMap.get(Foo.class), _isA(Foo.class));
    }

    public static class InjectFooImpl {
        @Inject(impl=Foo.class)
        public SuperFoo foo;
    }
    
    @Test
    public void test_inject_impl() {
        InjectFooImpl iFoo = iMap.initialize(new InjectFooImpl());
        assertThat(iFoo.foo, _isA(Foo.class));
        assertThat(iMap.get(Foo.class), is(nullValue()));
        assertThat(iMap.get(SuperFoo.class), _isA(Foo.class));
    }
    
    public static class InjectFooFactory {
        @Inject(impl=InstanceMapTest.class, factory="FOO_FACTORY")
        public Foo foo;
    }
    
    @Test
    public void test_inject_factory() {
        InjectFooFactory iFoo = iMap.initialize(new InjectFooFactory());
        assertThat(iFoo.foo, _isA(Foo.class));
        assertThat(iMap.get(Foo.class), _isA(Foo.class));
    }
    
    public static class InjectFooKey {
        @Inject(value="theFoo", create=true)
        public Foo foo;
    }
    
    @Test
    public void test_inject_key() {
        InjectFooKey iFoo = iMap.initialize(new InjectFooKey());
        assertThat(iFoo.foo, _isA(Foo.class));
        assertThat(iMap.get("theFoo"), _isA(Foo.class));
        assertThat(iMap.get(Foo.class), is(nullValue()));
    }
    
    public static class InjectCircular1 {
        @Inject(create=true)
        public InjectCircular2 c2;
    }
        
    public static class InjectCircular2 {
        @Inject(create=true)
        public InjectCircular1 c1;
    }
    
    @Test
    public void test_inject_circular_fail() {
        assertThat(
                invoke(iMap, "initialize", new InjectCircular1()), 
                raisesException("Circular dependency"));
    }
        
    public static class InjectCircular1X {
        @Inject(create=true)
        public InjectCircular2Late c2;
    }
        
    public static class InjectCircular2Late {
        @Inject(create=true, late=true)
        public InjectCircular1X c1;
    }
    
    @Test
    public void test_inject_circular_late() {
        InjectCircular1X ic1 = iMap.getOrCreate(InjectCircular1X.class);
        InjectCircular2Late ic2 = ic1.c2;
        assertThat(iMap.get(InjectCircular1X.class), is(sameInstance(ic1)));
        assertThat(iMap.get(InjectCircular2Late.class), is(sameInstance(ic2)));
    }
    
    @Inject(value="iface", impl=IFaceImpl.class)
    public static interface IFace {}
    
    public static class IFaceImpl implements IFace {}
    public static class IFaceImpl2 implements IFace {}
    
    @Test
    public void test_inject_interface_impl() {
        IFace iface = iMap.getOrCreate(IFace.class);
        assertThat(iface, _isA(IFaceImpl.class));
    }
    
    @Test
    public void test_inject_interface_key() {
        iMap.put("iface", new IFaceImpl2());
        IFace iface = iMap.getOrCreate(IFace.class);
        assertThat(iface, _isA(IFaceImpl2.class));
    }
    
    public static class ParamConstructor {
        public Foo foo;
        protected ParamConstructor() {
        }
        public ParamConstructor(Foo foo) {
            this.foo = foo;
        }
    }
    
    @Test
    public void test_param_constructor() {
        iMap.getOrCreate(Foo.class);
        ParamConstructor pc = iMap.getOrCreate(ParamConstructor.class);
        assertThat(pc.foo, is(notNullValue()));
    }
    
    public static class InjectConstructor {
        public Fooable foo;
        public InjectConstructor() {
        }
        @Inject("theFooable")
        public InjectConstructor(Fooable foo) {
            this.foo = foo;
        }
    }
    
    @Test
    public void test_inject_constructor() {
        iMap.put("theFooable", new Foo());
        InjectConstructor pc = iMap.getOrCreate(InjectConstructor.class);
        assertThat(pc.foo, _isA(Foo.class));
    }
    
    @DefaultInject(@Inject(create=true))
    public static class DefInject {
        public Foo foo;
        public DefInject(Foo foo) {
            this.foo = foo;
        }
        @Inject
        public Bar bar;        
        @Inject("bar2")
        public void setBar2(Bar bar2) { this.bar2 = bar2; }
        public Bar bar2;
    }
    
    @Test
    public void test_default_inject() {
        DefInject ci = iMap.getOrCreate(DefInject.class);
        assertThat("foo", ci.foo, is(notNullValue()));
        assertThat("bar", ci.bar, is(notNullValue()));
        assertThat("bar2", ci.bar2, is(notNullValue()));
        assertThat(ci.bar2, is(not(sameInstance(ci.bar))));
    }
        
    public static class SuperFoo {}
    
    public static interface Fooable {}
    
    public static class Foo extends SuperFoo implements Fooable {}
    
    public static class Bar {}
    
    public static final Factory<Foo> FOO_FACTORY = new Factory<Foo>(){
        @Override
        public Foo create(InstanceMap map) {
            return new Foo();
        }
    };
    
    public static final Factory<Bar> BAR_FACTORY = new Factory<Bar>(){
        @Override
        public Bar create(InstanceMap map) {
            return new Bar();
        }
    };
    
    @SuppressWarnings("unchecked")
    protected static Factory<Foo> actuallyBarFactory() {
        return (Factory) BAR_FACTORY;
    }
}
