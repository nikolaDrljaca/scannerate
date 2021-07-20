package com.drbrosdev.studytextscan.persistence.repository

import com.drbrosdev.studytextscan.persistence.database.ApplicationDatabase
import com.drbrosdev.studytextscan.persistence.entity.Scan

class ScanRepository(
    database: ApplicationDatabase
) {
    private val dao = database.scanDao
    private val string =
        "Text linguistics is a field of study where texts are treated as communication systems. The analysis deals with stretches of language beyond the single sentence and focuses particularly on context, i.e. information that goes along with what is said and written. Context includes such things as the social relationship between two speakers or correspondents, the place where communication occurs, and non-verbal information such as body language. Linguists use this contextual information to describe the \"socio-cultural environment\" in which a text exists."

    private fun createScans(): MutableList<Scan> {
        val array = mutableListOf<Scan>()
        for (i in 0..27 ) {
            val scan = Scan(i.toLong(), string, "21.01.21", "21.01.21")
            array.add(scan)
        }
 /*       val scan = Scan(0, string, "21.01.21", "21.01.21")
        array.add(scan)
        return array*/
        return array
    }


    fun getAllScans() = createScans()
}