package com.example.appicfilterdemo.design

import android.content.Intent
import android.os.Bundle
import com.example.appicfilterdemo.R
import com.example.appicfilterdemo.base.DataBindingActivity
import com.example.appicfilterdemo.databinding.ActivityDashboardBinding
import com.example.appicfilterdemo.utils.PrefUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class DashboardActivity : DataBindingActivity() {

    private val binding: ActivityDashboardBinding by binding(R.layout.activity_dashboard)
    private lateinit var sharedPreferenceUtils: PrefUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    private fun initialize() {
        binding.lifecycleOwner = this
        sharedPreferenceUtils = PrefUtils(this)

        val responseData = JSONObject(
            try {
                loadJSONFromAsset()
            } catch (e: JSONException) {
                e.printStackTrace()
                "{}"
            }
        )
        sharedPreferenceUtils.putString("response", responseData.toString())

        binding.apply {
            tvDashboardAccountNumberTitle.setOnClickListener {
                filterLogic("Select Account Number")
            }
            tvDashboardBrandTitle.setOnClickListener {
                filterLogic("Select Brand")
            }
            tvDashboardLocationTitle.setOnClickListener {
                filterLogic("Select Location")
            }
            binding.tvDashboardCountClear.setOnClickListener {
                clearResponseLogic()
            }
        }
    }

    private fun filterLogic(title: String) {

        startActivity(
            Intent(this@DashboardActivity, FilterActivity::class.java)
                .putExtra("title", title)
        )
    }

    private fun loadJSONFromAsset(): String {
        val json = try {
            val `is`: InputStream = assets.open("responseDataNew.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, StandardCharsets.US_ASCII)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "{}"
        }
        return json
    }

    override fun onResume() {
        super.onResume()
        responseLogic()
    }

    private fun responseLogic() {

        val response = JSONObject(sharedPreferenceUtils.getString("response").toString())
        val hierarchyArray =
            response.optJSONArray("filterData")?.optJSONObject(0)?.optJSONArray("hierarchy")
                ?: JSONArray()

        val accountNumberJson = JSONArray()
        val brandNameJson = JSONArray()
        val locationNameJson = JSONArray()

        var accountNumberJsonCount = 0
        var brandNameJsonCount = 0
        var locationNameJsonCount = 0

        for (i in 0 until hierarchyArray.length()) {
            val hierarchyData = hierarchyArray.optJSONObject(i)
            accountNumberJson.put(hierarchyData)

        }

        for (i in 0 until accountNumberJson.length()) {
            val accountNumberData = accountNumberJson.optJSONObject(i)

            if (accountNumberData.optBoolean("check")) {
                accountNumberJsonCount++
                val brandResponse = accountNumberData.optJSONArray("brandNameList") ?: JSONArray()

                for (j in 0 until brandResponse.length()) {
                    val brandData = brandResponse.optJSONObject(j)
                    brandNameJson.put(brandData)

                    if (brandData.optBoolean("check")) {
                        brandNameJsonCount++
                        val locationNameResponse =
                            brandData.optJSONArray("locationNameList") ?: JSONArray()

                        for (k in 0 until locationNameResponse.length()) {
                            val locationNameData = locationNameResponse.optJSONObject(k)
                            locationNameJson.put(locationNameData)

                            if (locationNameData.optBoolean("check")) {
                                locationNameJsonCount++
                            }
                        }
                    }
                }
            }
        }

        binding.apply {
            tvDashboardAccountCount.text = "Acc no.: $accountNumberJsonCount"
            tvDashboardAccountNumberTitleCount.text = "$accountNumberJsonCount"
            tvDashboardBrandCount.text = "Brand: $brandNameJsonCount"
            tvDashboardBrandTitleCount.text = "$brandNameJsonCount"
            tvDashboardLocationCount.text = "Location: $locationNameJsonCount"
            tvDashboardLocationTitleCount.text = "$locationNameJsonCount"
        }
    }

    private fun clearResponseLogic() {

        val response = JSONObject(sharedPreferenceUtils.getString("response").toString())
        val hierarchyArray =
            response.optJSONArray("filterData")?.optJSONObject(0)?.optJSONArray("hierarchy")
                ?: JSONArray()

        val accountNumberJson = JSONArray()
        val brandNameJson = JSONArray()
        val locationNameJson = JSONArray()

        for (i in 0 until hierarchyArray.length()) {
            val hierarchyData = hierarchyArray.optJSONObject(i)
            accountNumberJson.put(hierarchyData)

        }

        for (i in 0 until accountNumberJson.length()) {
            val accountNumberData = accountNumberJson.optJSONObject(i)
            accountNumberData.put("check", false)

            val brandResponse = accountNumberData.optJSONArray("brandNameList") ?: JSONArray()

            for (j in 0 until brandResponse.length()) {
                val brandData = brandResponse.optJSONObject(j)
                brandData.put("check", false)
                brandNameJson.put(brandData)

                val locationNameResponse =
                    brandData.optJSONArray("locationNameList") ?: JSONArray()

                for (k in 0 until locationNameResponse.length()) {
                    val locationNameData = locationNameResponse.optJSONObject(k)
                    locationNameData.put("check", false)
                    locationNameJson.put(locationNameData)
                }
            }
        }

        sharedPreferenceUtils.putString("response", response.toString())

        binding.apply {
            tvDashboardAccountCount.text = "Acc no.: 0"
            tvDashboardAccountNumberTitleCount.text = "0"
            tvDashboardBrandCount.text = "Brand: 0"
            tvDashboardBrandTitleCount.text = "0"
            tvDashboardLocationCount.text = "Location: 0"
            tvDashboardLocationTitleCount.text = "0"
        }
    }
}