package org.cthul.objects.reflection;

import java.io.Serializable;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class VirtualMethodTest {
    
    private static final Class<?> CLAZZ = VirtualMethodTest.class;
    
    private String m1(int i) {
        return "mI";
    }
    
    private String m1(double d) {
        return "mD";
    }
    
    private String m2(CharSequence s, int i) {
        return "mCI";
    }
    
    private String m2(Serializable s, double i) {
        return "mSD";
    }
    
    private String m2(CharSequence s, double i) {
        return "mCD";
    }
    
    private String mV(int i, Serializable... s) {
        return "mIS...";
    }
    
    private String mV(double i, Serializable... s) {
        return "mDS...";
    }
    
    private String mV(int i, CharSequence... s) {
        return "mIC...";
    }
    
    @Test
    public void test_simple() throws Exception {
        VirtualMethod<String> vms = VirtualMethod.instance(CLAZZ, "m1");
        assertThat(vms.invoke(this, 1), is("mI"));
        assertThat(vms.invoke(this, 1.0), is("mD"));
    }
    
    @Test
    public void test_masked() throws Exception {
        // m2("", 1.0); // ambiguous
        VirtualMethod<String> vms = VirtualMethod.instance(CLAZZ, "m2", CharSequence.class);
        assertThat(vms.invoke(this, "", 1), is("mCI"));
        assertThat(vms.invoke(this, "", 1.0), is("mCD"));
    }
    
    @Test
    public void test_masked2() throws Exception {
        // m2("", 1.0); // ambiguous
        VirtualMethod<String> vms = VirtualMethod.instance(CLAZZ, "m2", CharSequence.class, null);
        assertThat(vms.invoke(this, "", 1), is("mCI"));
        assertThat(vms.invoke(this, "", 1.0), is("mCD"));
    }
    
    @Test
    public void test_masked2b() throws Exception {
        VirtualMethod<String> vms = VirtualMethod.instance(CLAZZ, "m2", null, Double.class);
        assertThat(vms.invoke(this, SERIALIZABLE, 1), is("mSD"));
        assertThat(vms.invoke(this, CHAR_SEQUENCE, 1.0), is("mCD"));
    }
    
    @Test
    public void test_masked2c() throws Exception {
        VirtualMethod<String> vms = VirtualMethod.instance(CLAZZ, "m2", 1, Double.class);
        assertThat(vms.invoke(this, SERIALIZABLE, 1), is("mSD"));
        assertThat(vms.invoke(this, CHAR_SEQUENCE, 1.0), is("mCD"));
    }
    
    @Test
    public void test_masked_varArgs() throws Exception {
        // mV(1, ""); // ambiguous
        VirtualMethod<String> vms = VirtualMethod.instance(CLAZZ, "mV", 1, false, Serializable[].class);
        assertThat(vms.invoke(this, 1), is("mIS..."));
        assertThat(vms.invoke(this, 1, ""), is("mIS..."));
        assertThat(vms.invoke(this, 1, "", ""), is("mIS..."));
    }
    
    private static final Serializable SERIALIZABLE = new Serializable() {
        };
    
    private static final CharSequence CHAR_SEQUENCE = new CharSequence() {
            @Override
            public int length() { return 0; }

            @Override
            public char charAt(int index) { return '0'; }

            @Override
            public CharSequence subSequence(int start, int end) { return this; }
        };
}
