package com.drbrosdev.studytextscan.service.textFilter

import org.junit.Test

class TextFilterServiceImplTest {

    private val service = TextFilterServiceImpl()

    @Test
    fun filterText() {
        val list =
            service.filterTextForPhoneNumbers("a+912012185234ogn jen0212a04367a76220309asdasdas+9120 12185234idegas+123456 78910,asdqer1431+1122334455")
        list.forEach {
            println(it.toString())
        }
    }

    @Test
    fun filterEmail() {
        val list =
            service.filterTextForEmails("aaa1238123@hotmail.com,asd drljacao96@hotmail.com nigga shit mail@mail.com")
        list.forEach {
            println(it.toString())
        }
    }

    @Test
    fun filterLinks() {
        val list =
            service.filterTextForLinks("asdasdas https://web.whatsapp.com/ dasdasd https://trello.com/b/7gk0iyzu/text-scan-app aa")
        list.forEach {
            println(it.toString())
        }
    }
}