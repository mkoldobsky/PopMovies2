package com.example.mkoldobsky.popmovies.common;

import android.test.AndroidTestCase;

/**
 * Created by mkoldobsky on 24/9/15.
 */
public class ConstantsTests extends AndroidTestCase {


    public void testAssureApiKeyIsNotAssignedBeforeCommitting(){
        assertEquals("xxxx", Constants.API_KEY);
    }
}
