package org.cthul.objects;

import java.lang.reflect.Array;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 */
public class BoxingTest {
    
    public BoxingTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_deepBoxingType_1() {
        assertThat(Boxing.arrayBoxingType(int[][].class), 
                   is((Object) Integer[][].class));
    }

    @Test
    public void test_deepBoxingType_2() {
        assertThat(Boxing.arrayBoxingType(Integer[][].class), 
                   is((Object) int[][].class));
    }
    
    @Test
    public void test_deepUnbox() {
        Integer[][] boxed = {{1, 2}, {}, {3}};
        int[][] primd = Boxing.deepUnboxAs(boxed, int[][].class);
        assertThat(primd, arrayWithSize(3));
        assertThat(primd[0], is(new int[]{1, 2}));
        assertThat(primd[1], is(new int[]{}));
        assertThat(primd[2], is(new int[]{3}));
    }

    @Test
    public void test_deepBox() {
        int[][] primd = {{1, 2}, {}, {3}};
        Integer[][] boxed = Boxing.deepBoxAs(primd, Integer[][].class);
        assertThat(boxed, arrayWithSize(3));
        assertThat(boxed[0], is(new Integer[]{1, 2}));
        assertThat(boxed[1], is(new Integer[]{}));
        assertThat(boxed[2], is(new Integer[]{3}));
    }

}