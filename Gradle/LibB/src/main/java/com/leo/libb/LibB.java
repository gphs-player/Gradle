package com.leo.libb;


import com.leo.libd.LibD;

/**
 * <p>Date:2019-08-30.09:43</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
public class LibB {
    private LibD libC;

    public void showB() {
        System.out.println("showB");
        libC = new LibD();
        libC.showDSelf();
    }
}
