package com.example.appicfilterdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appicfilterdemo.databinding.ItemFilterSearchBinding
import org.json.JSONArray

class FilterAdapter(
    private val response: JSONArray,
    private val title: String?,
    private val checkBoxClickListener: (Boolean, Int, String, String) -> Unit
) :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {
    private lateinit var binding: ItemFilterSearchBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemFilterSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding, response, title, checkBoxClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.setValue(position)

    }

    override fun getItemCount(): Int {
        return response.length()
    }

    class ViewHolder(
        private val binding: ItemFilterSearchBinding,
        private val response: JSONArray,
        private val title: String?,
        private val checkBoxClickListener: (Boolean, Int, String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setValue(position: Int) {
            val data = response.optJSONObject(position)

            binding.cbItemFilterSearchTitle.isChecked = data.optBoolean("check")
            binding.cbItemFilterSearchTitle.text = when (title) {
                "Select Account Number" -> {
                    data.optString("accountNumber")
                }
                "Select Brand" -> {
                    data.optString("brandName")
                }
                "Select Location" -> {
                    data.optString("locationName")
                }
                else -> {
                    ""
                }
            }

            binding.cbItemFilterSearchTitle.setOnClickListener {
                checkBoxClickListener.invoke(
                    binding.cbItemFilterSearchTitle.isChecked,
                    position,
                    data.optString("position"),
                    binding.cbItemFilterSearchTitle.text.toString()
                )
            }
        }

    }
}
