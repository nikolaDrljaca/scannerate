package com.drbrosdev.studytextscan.ui.licences

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.LicenceListItemBinding
import com.drbrosdev.studytextscan.epoxy.ViewBindingKotlinModel
import com.drbrosdev.studytextscan.ui.util.UserInterfaceUtil

@EpoxyModelClass
abstract class LicenceListItemEpoxyModel :
    ViewBindingKotlinModel<LicenceListItemBinding>(R.layout.licence_list_item) {

    @EpoxyAttribute
    lateinit var licenceListItem: UserInterfaceUtil.Companion.LicenceListItem

    @EpoxyAttribute
    lateinit var onLicenceListItemClicked: () -> Unit

    override fun LicenceListItemBinding.bind() {
        licenceItemTitle.text = licenceListItem.title
        licenceItemLink.text = licenceListItem.link
        //TODO onClick
    }

}
