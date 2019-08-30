package com.leo.liba;

import android.util.Log;

import com.leo.libc.LibC;

/**
 * <p>Date:2019-08-30.09:43</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
public class LibA {
    private LibC libC;
    public void showA(){
        System.out.println("showA");
        libC = new LibC();
        libC.showCSelf();
    }
}
