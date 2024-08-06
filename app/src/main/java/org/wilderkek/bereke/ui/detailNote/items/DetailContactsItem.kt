package org.wilderkek.bereke.ui.detailNote.items

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.view.isVisible
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemNoteDetailAddressBinding
import org.wilderkek.bereke.databinding.ItemNoteDetailContactsBinding
import org.wilderkek.bereke.util.setUserImage
import org.wilderkek.bereke.util.validatePhoneBeforeSend

class DetailContactsItem(
    private val phones: List<String>?,
    private val whatsapp: List<String>?,
    private val userName: String?,
    private val userId: String?,
    private val userImage: String?
) : BindableItem<ItemNoteDetailContactsBinding>() {


    override fun bind(viewBinding: ItemNoteDetailContactsBinding, position: Int) {
        viewBinding.apply {
            tvPhoneNumber.apply {
                if (phones.isNullOrEmpty())
                    text = root.context.getString(R.string.not_chosen)
                else text = phones.joinToString("\n") { it }
            }
            tvWhatsApp.apply {
                if (whatsapp.isNullOrEmpty())
                    text = root.context.getString(R.string.not_chosen)
                else text = whatsapp.joinToString("\n") { it }
            }

            ivUserPhoto.setUserImage(
                image = userImage ?: R.drawable.avatar_empty_square,
                transformations = listOf(CircleCropTransformation())
            )
            tvUserName.text = userName
            tvUserId.text = userId

            btnWriteWhatsapp.apply {
                isVisible = !phones.isNullOrEmpty() || !whatsapp.isNullOrEmpty()
                setOnClickListener {
                    openWhatsApp(context)
                }
            }

            btnCall.apply {
                isVisible = !phones.isNullOrEmpty() || !whatsapp.isNullOrEmpty()
                setOnClickListener {
                    openCall(context)
                }
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is DetailContactsItem) return false
        if (phones != other.phones) return false
        if (whatsapp != other.whatsapp) return false
        if (userName != other.userName) return false
        if (userImage != other.userImage) return false
        return true
    }


    private fun openWhatsApp(context : Context) {
        var whatsappPhone = ""
        if (!whatsapp.isNullOrEmpty()) {
            whatsappPhone = validatePhoneBeforeSend(whatsapp.first())
        } else if (!phones.isNullOrEmpty()) {
            whatsappPhone = validatePhoneBeforeSend(phones.first())
        }

        val url: String = "https://api.whatsapp.com/send?phone=$whatsappPhone"
        val i: Intent = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        context.startActivity(i)
    }

    private fun openCall(context : Context){
        var phone = ""
        if (!phones.isNullOrEmpty()) {
            phone = validatePhoneBeforeSend(phones.first())
        } else if (!whatsapp.isNullOrEmpty()) {
            phone = validatePhoneBeforeSend(whatsapp.first())
        }

        val url: String = "tel:$phone"
        val inn: Intent = Intent(Intent.ACTION_DIAL)
        inn.data = Uri.parse(url)
        context.startActivity(inn)
    }

    override fun getLayout(): Int = R.layout.item_note_detail_contacts

}