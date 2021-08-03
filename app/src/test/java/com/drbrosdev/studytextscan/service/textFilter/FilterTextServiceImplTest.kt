package com.drbrosdev.studytextscan.service.textFilter

import org.junit.Test

class FilterTextServiceImplTest {

    private val service = FilterTextServiceImpl()

    @Test
    fun filterText() {
        val list = service.filterText("0123ognjen3312idegas+436776220309")
        println("ide nis a ide i subotica")
    }
}