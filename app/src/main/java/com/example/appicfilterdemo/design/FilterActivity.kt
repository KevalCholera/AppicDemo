package com.example.appicfilterdemo.design

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appicfilterdemo.R
import com.example.appicfilterdemo.adapter.FilterAdapter
import com.example.appicfilterdemo.base.DataBindingActivity
import com.example.appicfilterdemo.databinding.ActivityFilterBinding
import com.example.appicfilterdemo.utils.PrefUtils
import org.json.JSONArray
import org.json.JSONObject

class FilterActivity : DataBindingActivity() {
    private val binding: ActivityFilterBinding by binding(R.layout.activity_filter)
    private val logTag = this.javaClass.simpleName.toString()
    private lateinit var sharedPreferenceUtils: PrefUtils
    private var shareJsonToAdapter = JSONArray()

    private var response = JSONObject()
    private var hierarchyArray = JSONArray()
    private val accountNumberJson = JSONArray()
    private val brandNameJson = JSONArray()
    private val locationNameJson = JSONArray()
    private var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        responseLogic()
        setClickListener()

    }

    private fun responseLogic() {

        response = JSONObject(sharedPreferenceUtils.getString("response").toString())
        hierarchyArray =
            response.optJSONArray("filterData")?.optJSONObject(0)?.optJSONArray("hierarchy")
                ?: JSONArray()

        for (i in 0 until hierarchyArray.length()) {
            val hierarchyData = hierarchyArray.optJSONObject(i)
            hierarchyData.put("position", i.toString())
            accountNumberJson.put(hierarchyData)
        }

        for (i in 0 until accountNumberJson.length()) {
            val accountNumberData = accountNumberJson.optJSONObject(i)

            if (accountNumberData.optBoolean("check")) {
                val brandResponse = accountNumberData.optJSONArray("brandNameList") ?: JSONArray()
                for (j in 0 until brandResponse.length()) {
                    val brandData = brandResponse.optJSONObject(j)
                    brandData.put("position", j.toString())
                    brandNameJson.put(brandData)
                }
            }
        }

        for (i in 0 until brandNameJson.length()) {
            val brandNameData = brandNameJson.optJSONObject(i)

            if (brandNameData.optBoolean("check")) {
                val locationNameResponse =
                    brandNameData.optJSONArray("locationNameList") ?: JSONArray()
                for (j in 0 until locationNameResponse.length()) {
                    val locationNameData = locationNameResponse.optJSONObject(j)
                    locationNameData.put("position", j.toString())
                    locationNameJson.put(locationNameData)
                }
            }
        }

        binding.apply {
            when (title) {
                "Select Account Number" -> {
                    shareJsonToAdapter = accountNumberJson
                    tvFilterListMsg.text = "No Data"
                }
                "Select Brand" -> {
                    shareJsonToAdapter = brandNameJson
                    tvFilterListMsg.text = "First Select Account Number"
                }
                "Select Location" -> {
                    shareJsonToAdapter = locationNameJson
                    tvFilterListMsg.text = "First Select Brand"
                }
                else ->
                    shareJsonToAdapter = JSONArray()

            }

            if (shareJsonToAdapter.length() > 0) {
                tvFilterListMsg.visibility = View.GONE
                rvFilterList.visibility = View.VISIBLE
                rvFilterList.adapter =
                    FilterAdapter(shareJsonToAdapter, title, checkBoxClickListener)
                rvFilterList.layoutManager = LinearLayoutManager(this@FilterActivity)
            } else {
                tvFilterListMsg.visibility = View.VISIBLE
                rvFilterList.visibility = View.GONE

                Log.i(logTag, "onCreate: $shareJsonToAdapter")

            }
        }
    }

    private fun initialize() {
        binding.lifecycleOwner = this
        sharedPreferenceUtils = PrefUtils(this)
        title = intent.getStringExtra("title").toString()

        binding.tvFilterTitle.text = title
    }

    private fun setClickListener() {

        binding.btFilterApplyFilter.setOnClickListener {
            sharedPreferenceUtils.putString("response", response.toString())
            finish()
        }
        binding.ivFilterBack.setOnClickListener {
            finish()
        }

        binding.cbFilterSelectAll.setOnClickListener {
            for (i in 0 until shareJsonToAdapter.length()) {
                shareJsonToAdapter.optJSONObject(i)
                    .put("check", binding.cbFilterSelectAll.isChecked)
            }

            binding.rvFilterList.adapter =
                FilterAdapter(shareJsonToAdapter, title, checkBoxClickListener)
            binding.rvFilterList.layoutManager = LinearLayoutManager(this@FilterActivity)

        }

        binding.tvDashboardCountClear.setOnClickListener {
            binding.cbFilterSelectAll.isChecked = false
            binding.etFilterSearch.setText("")
            for (i in 0 until shareJsonToAdapter.length()) {
                shareJsonToAdapter.optJSONObject(i)
                    .put("check", false)
            }

            binding.rvFilterList.adapter =
                FilterAdapter(shareJsonToAdapter, title, checkBoxClickListener)
            binding.rvFilterList.layoutManager = LinearLayoutManager(this@FilterActivity)

        }

        binding.etFilterSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0 != "") {
                    val filteredResponseArray = JSONArray()
                    for (i in 0 until shareJsonToAdapter.length()) {
                        val filteredResponseObject = shareJsonToAdapter.optJSONObject(i)

                        when (title) {
                            "Select Account Number" -> {
                                if (filteredResponseObject.optString("accountNumber").contains(p0)
                                )
                                    filteredResponseArray.put(filteredResponseObject)
                            }
                            "Select Brand" -> {
                                if (filteredResponseObject.optString("brandName").contains(p0)
                                )
                                    filteredResponseArray.put(filteredResponseObject)
                            }
                            "Select Location" -> {
                                if (filteredResponseObject.optString("locationName").contains(p0)
                                )
                                    filteredResponseArray.put(filteredResponseObject)
                            }
                            else -> {

                            }

                        }
                    }
                    binding.rvFilterList.adapter =
                        FilterAdapter(filteredResponseArray, title, checkBoxClickListener)
                    binding.rvFilterList.layoutManager = LinearLayoutManager(this@FilterActivity)

                } else {
                    binding.rvFilterList.adapter =
                        FilterAdapter(shareJsonToAdapter, title, checkBoxClickListener)
                    binding.rvFilterList.layoutManager = LinearLayoutManager(this@FilterActivity)

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }

    private val checkBoxClickListener =
        fun(isCheck: Boolean, position: Int, responseDataPosition: String, itemName: String) {


            for (i in 0 until shareJsonToAdapter.length()) {
                val shareJsonToObject = shareJsonToAdapter.optJSONObject(i)
                when (title) {
                    "Select Account Number" -> {
                        if (shareJsonToObject.optString("accountNumber")
                                .uppercase() == itemName.uppercase()
                        )
                            shareJsonToAdapter.optJSONObject(i).put("check", isCheck)
                    }
                    "Select Brand" -> {
                        if (shareJsonToObject.optString("brandName")
                                .uppercase() == itemName.uppercase()
                        )
                            shareJsonToAdapter.optJSONObject(i).put("check", isCheck)
                    }
                    "Select Location" -> {
                        if (shareJsonToObject.optString("locationName")
                                .uppercase() == itemName.uppercase()
                        )
                            shareJsonToAdapter.optJSONObject(i).put("check", isCheck)
                    }
                    else -> {

                    }
                }
            }

            when (title) {
                "Select Account Number" -> {
                    accountNumberJson.optJSONObject(responseDataPosition.toInt())
                        .put("check", isCheck)
                }
                "Select Brand" -> {
                    brandNameJson.optJSONObject(responseDataPosition.toInt())
                        .put("check", isCheck)
                }
                "Select Location" -> {
                    locationNameJson.optJSONObject(responseDataPosition.toInt())
                        .put("check", isCheck)
                }
                else -> {


                }
            }
            Log.i(logTag, "$isCheck - $position - $responseDataPosition - $response")
        }
}