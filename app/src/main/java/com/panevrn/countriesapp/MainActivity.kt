package com.panevrn.countriesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.panevrn.countriesapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val presenter = MainActivityPresenter(this)

        binding.searchButton.setOnClickListener {
            val countryName = binding.countryNameEditText.text.toString()

            presenter.onSearchButtonClick(countryName)
        }
    }

    fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar(){
        binding.progressBar.visibility = View.GONE
    }

    fun hideStatusLayout(){
        binding.resultLayout.visibility = View.VISIBLE
        binding.statusLayout.visibility = View.INVISIBLE
    }

    fun showCountryInfo(country: Country){
        binding.countryNameTextView.text = country.name
        binding.capitalTextView.text = country.capital
        binding.populationTextView.text = formatNumber(country.population)
        binding.areaTextView.text = formatNumber(country.area)
        binding.languagesTextView.text = languagesToString(country.languages)
    }

    fun showFlag(flagUrl: String){
        lifecycleScope.launch {
            loadSvg(binding.imageView, flagUrl)
        }
    }
}

class MainActivityPresenter(val activity: MainActivity){
    fun onSearchButtonClick(countryName: String){
        GlobalScope.launch(Dispatchers.Main) {
            activity.showProgressBar()

            val countries = restCountriesApi.getCountryByName(countryName)
            val country = countries[0]

            activity.hideProgressBar()
            activity.showCountryInfo(country)
            activity.showFlag(country.flag)


            activity.hideStatusLayout()
        }
    }
}



