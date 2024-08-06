package org.wilderkek.bereke.ui.createNote.items

import android.text.InputFilter
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.allViews
import androidx.core.view.isVisible
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.util.getCategoriesList
import org.wilderkek.bereke.util.setAdapterData
import org.wilderkek.bereke.util.initInput
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemMainDataBinding
import org.wilderkek.bereke.databinding.LayoutPhoneFieldBinding
import org.wilderkek.bereke.model.response.NoteContactsModel
import org.wilderkek.bereke.util.validatePhoneBeforeSend

class MainDataItem(
    val name: String?,
    val description: String?,
    val category: String?,
    val phones: List<String>?,
    val whatsapp: List<String>?
) : BindableItem<ItemMainDataBinding>() {

    var onCategoryChangeCallback: (category: String) -> Unit = {}

    private var noteName = name ?: ""
    private var noteDescription = description ?: ""
    private var noteCategory = reformatCategory(category)

    private val callPhonesList = arrayListOf(PhoneData("")).apply {
        if (!phones.isNullOrEmpty()) {
            clear()
            phones.map { x -> add(PhoneData(x)) }
        }
    }
    private val whatsAppPhonesList = arrayListOf(PhoneData("")).apply {
        if (!whatsapp.isNullOrEmpty()) {
            clear()
            whatsapp.map { x -> add(PhoneData(x)) }
        }
    }


    private lateinit var mBinding: ItemMainDataBinding
    override fun bind(viewBinding: ItemMainDataBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            tvNoteNameRequired.isVisible = noteName.isNullOrBlank()
            tvNoteCategoryRequired.isVisible = noteCategory.isNullOrBlank()

            etNoteName.initInput(noteName) {
                tvNoteNameRequired.apply {
                    if (!it.toString().isNullOrBlank()) changeRequiredTextColor(true)
                    isVisible = it.toString().isNullOrBlank()
                }
                noteName = it.toString()
            }
            etNoteDescription.initInput(noteDescription) {
                noteDescription = it.toString()
            }
            etNoteCategory.apply {
                setAdapterData(getCategoriesList().toMutableList())
                initInput(noteCategory) {
                    tvNoteCategoryRequired.apply {
                        if (!it.toString().isNullOrBlank()) changeRequiredTextColor(true)
                        isVisible = it.toString().isNullOrBlank()
                    }
                    noteCategory = it.toString()
                }
                setOnItemClickListener { _, _, _, _ ->
                    if (!noteCategory.isNullOrBlank())
                        onCategoryChangeCallback.invoke(formatCategory(noteCategory))
                }
            }

            //call phone number
            lnCallNumbers.apply {
                removeAllViews()
                callPhonesList.forEach {
                    initCallPhoneField(viewBinding, it)
                }
            }
            tvAddCallPhone.setOnClickListener {
                if (!callPhonesList.lastOrNull()?.value.isNullOrBlank()) {
                    PhoneData(value = "").apply {
                        callPhonesList.add(this)
                        initCallPhoneField(viewBinding, this)
                    }
                } else {
                    if (!lnCallNumbers.allViews.toList().isNullOrEmpty()) {
                        lnCallNumbers.findViewById<EditText>(R.id.et_phone).requestFocus()
                    }
                }
            }

            //whats app number
            lnWhatsappNumbers.apply {
                removeAllViews()
                whatsAppPhonesList.forEach {
                    initWhatsAppPhoneField(viewBinding, it)
                }
            }
            tvAddWhatsAppPhone.setOnClickListener {
                if (!whatsAppPhonesList.lastOrNull()?.value.isNullOrBlank()) {
                    PhoneData(value = "").apply {
                        whatsAppPhonesList.add(this)
                        initWhatsAppPhoneField(viewBinding, this)
                    }
                } else {
                    if (!lnWhatsappNumbers.allViews.toList().isNullOrEmpty()) {
                        lnWhatsappNumbers.findViewById<EditText>(R.id.et_phone).requestFocus()
                    }
                }
            }
        }
    }


    private fun initCallPhoneField(viewBinding: ItemMainDataBinding, phone: PhoneData) {
        val binding = LayoutPhoneFieldBinding.inflate(
            LayoutInflater.from(viewBinding.root.context),
            viewBinding.lnCallNumbers,
            false
        )
        binding.etPhone.apply {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.toString().filterNot { it.isWhitespace() }
            })
            initInput(phone.value) { phone.value = it?.toString() ?: "" }
        }
        binding.ivDelete.setOnClickListener {
            binding.etPhone.text?.clear()
            if (callPhonesList.size < 2) {
                phone.value = ""
            } else {
                if (callPhonesList.remove(phone)) {
                    viewBinding.lnCallNumbers.removeView(binding.root)
                }
            }
        }

        viewBinding.lnCallNumbers.addView(binding.root)
    }

    private fun initWhatsAppPhoneField(viewBinding: ItemMainDataBinding, phone: PhoneData) {
        val binding = LayoutPhoneFieldBinding.inflate(
            LayoutInflater.from(viewBinding.root.context),
            viewBinding.lnWhatsappNumbers,
            false
        )
        binding.etPhone.apply {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.toString().filterNot { it.isWhitespace() }
            })
            initInput(phone.value) { phone.value = it?.toString() ?: "" }
        }
        binding.ivDelete.setOnClickListener {
            binding.etPhone.text?.clear()
            if (whatsAppPhonesList.size < 2) {
                phone.value = ""
            } else {
                if (whatsAppPhonesList.remove(phone)) {
                    viewBinding.lnWhatsappNumbers.removeView(binding.root)
                }
            }
        }

        viewBinding.lnWhatsappNumbers.addView(binding.root)
    }

    private fun formatCategory(category: String): String {
        return when (category) {
            "Работа, Вакансии" -> "work"
            "Квартиры, Гостиницы" -> "house"
            else -> ""
        }
    }

    private fun reformatCategory(category: String?): String {
        return when (category) {
            "work" -> "Работа, Вакансии"
            "house" -> "Квартиры, Гостиницы"
            else -> ""
        }
    }


    fun isMainDataValid(): Boolean {
        var valid = true
        if (noteName.isNullOrBlank()) {
            valid = false
            mBinding.tvNoteNameRequired.changeRequiredTextColor(false)
        }
        if (noteCategory.isNullOrBlank()) {
            valid = false
            mBinding.tvNoteCategoryRequired.changeRequiredTextColor(false)

        }
        return valid
    }

    fun isFieldsIsEmpty(): Boolean {
        var isEmpty = true
        if (!noteName.isNullOrBlank()) isEmpty = false
        if (!noteCategory.isNullOrBlank()) isEmpty = false
        if (!noteDescription.isNullOrBlank()) isEmpty = false
        return isEmpty
    }

    fun getMainData(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            put("name", noteName)
            put("description", noteDescription)
            put("category", formatCategory(noteCategory))
        }
    }

    fun getContactsData(): NoteContactsModel {
        return NoteContactsModel(
            getCallPhones(),
            getWhatsAppPhones(),
            emptyList()
        )
    }

    private fun getWhatsAppPhones(): List<String> {
        return whatsAppPhonesList.filter { x -> !x.value.isNullOrBlank() }
            .map { x -> validatePhoneBeforeSend(x.value) }
    }

    private fun getCallPhones(): List<String> {
        return callPhonesList.filter { x -> !x.value.isNullOrBlank() }
            .map { x -> validatePhoneBeforeSend(x.value) }
    }

    private fun TextView.changeRequiredTextColor(isCorrect: Boolean) {
        if (!isCorrect) setTextColor(context.getColor(R.color.error_text_color))
        else setTextColor(context.getColor(R.color.app_main_color))
    }


    override fun getLayout(): Int = R.layout.item_main_data
}

class PhoneData(
    var value: String
)